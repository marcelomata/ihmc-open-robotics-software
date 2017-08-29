package us.ihmc.avatar.networkProcessor.rrtToolboxModule;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import us.ihmc.avatar.drcRobot.DRCRobotModel;
import us.ihmc.avatar.networkProcessor.modules.ToolboxController;
import us.ihmc.commons.PrintTools;
import us.ihmc.communication.controllerAPI.StatusMessageOutputManager;
import us.ihmc.communication.net.PacketConsumer;
import us.ihmc.euclid.geometry.Pose3D;
import us.ihmc.euclid.tuple4D.Quaternion;
import us.ihmc.graphicsDescription.yoGraphics.YoGraphicCoordinateSystem;
import us.ihmc.graphicsDescription.yoGraphics.YoGraphicsListRegistry;
import us.ihmc.humanoidRobotics.communication.packets.manipulation.constrainedWholeBodyPlanning.AtlasKinematicsConfiguration;
import us.ihmc.humanoidRobotics.communication.packets.manipulation.constrainedWholeBodyPlanning.ConfigurationSpace;
import us.ihmc.humanoidRobotics.communication.packets.manipulation.constrainedWholeBodyPlanning.ConstrainedEndEffectorTrajectory;
import us.ihmc.humanoidRobotics.communication.packets.manipulation.constrainedWholeBodyPlanning.ConstrainedWholeBodyPlanningRequestPacket;
import us.ihmc.humanoidRobotics.communication.packets.manipulation.constrainedWholeBodyPlanning.ConstrainedWholeBodyPlanningToolboxOutputStatus;
import us.ihmc.humanoidRobotics.communication.packets.manipulation.constrainedWholeBodyPlanning.TaskRegion;
import us.ihmc.manipulation.planning.rrt.constrainedplanning.configurationAndTimeSpace.CTTaskNode;
import us.ihmc.manipulation.planning.rrt.constrainedplanning.configurationAndTimeSpace.CTTaskNodeTree;
import us.ihmc.manipulation.planning.rrt.constrainedplanning.configurationAndTimeSpace.CTTreeVisualizer;
import us.ihmc.manipulation.planning.rrt.constrainedplanning.configurationAndTimeSpace.GenericTaskNode;
import us.ihmc.manipulation.planning.rrt.constrainedplanning.configurationAndTimeSpace.TreeStateVisualizer;
import us.ihmc.manipulation.planning.rrt.constrainedplanning.tools.WheneverWholeBodyKinematicsSolver;
import us.ihmc.robotModels.FullHumanoidRobotModel;
import us.ihmc.robotModels.FullRobotModelUtils;
import us.ihmc.robotics.math.frames.YoFramePose;
import us.ihmc.robotics.referenceFrames.ReferenceFrame;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoBoolean;
import us.ihmc.yoVariables.variable.YoDouble;
import us.ihmc.yoVariables.variable.YoInteger;

public class ConstrainedWholeBodyPlanningToolboxController extends ToolboxController
{
   public static double handCoordinateOffsetX = -0.2;

   /*
    * essential classes
    */
   private DRCRobotModel drcRobotModelFactory;

   public static ConstrainedEndEffectorTrajectory constrainedEndEffectorTrajectory;

   private WheneverWholeBodyKinematicsSolver kinematicsSolver;

   private static ReferenceFrame midZUpFrame;

   private static ReferenceFrame worldFrame;

   /*
    * YoVariables
    */
   private final YoInteger updateCount = new YoInteger("updateCount", registry);

   private final YoInteger expandingCount = new YoInteger("expandingCount", registry);

   // check the current pose is valid or not.   
   private final YoBoolean currentIsValid = new YoBoolean("currentIsValid", registry);

   // check the tree reaching the normalized time from 0.0 to 1.0.
   private final YoDouble currentTrajectoryTime = new YoDouble("currentNormalizedTime", registry);

   private final YoBoolean isDone = new YoBoolean("isDone", registry);

   private final YoDouble solutionQuality = new YoDouble("solutionQuality", registry);

   private final YoDouble jointlimitScore = new YoDouble("jointlimitScore", registry);

   private double bestScoreInitialGuess = 0;

   private final YoInteger cntKinematicSolver = new YoInteger("cntKinematicSolver", registry);

   /*
    * Visualizer
    */
   private boolean startYoVariableServer;

   private CTTaskNode visualizedNode;

   private AtlasKinematicsConfiguration initialConfiguration;

   private FullHumanoidRobotModel visualizedFullRobotModel;

   private TreeStateVisualizer treeStateVisualizer;

   private CTTreeVisualizer treeVisualizer;

   private final YoFramePose endeffectorPose;

   private final YoGraphicCoordinateSystem endeffectorFrame;

   /*
    * Configuration and Time space Tree
    */
   private CTTaskNode rootNode;

   private CTTaskNodeTree tree;

   private TaskRegion taskRegion;

   /*
    * API
    */
   private final AtomicReference<ConstrainedWholeBodyPlanningRequestPacket> latestRequestReference = new AtomicReference<ConstrainedWholeBodyPlanningRequestPacket>(null);

   private int numberOfExpanding = 1;

   private int numberOfInitialGuess = 1;

   private int numberOfMotionPath = 1;

   private static int terminateToolboxCondition = 1000;

   private CTTaskNodeWholeBodyTrajectoryMessageFactory ctTaskNodeWholeBodyTrajectoryMessageFactory;

   /**
    * Toolbox state
    */
   private CWBToolboxState state;

   private enum CWBToolboxState
   {
      DO_NOTHING, FIND_INITIAL_GUESS, EXPAND_TREE, SHORTCUT_PATH, GENERATE_MOTION
   }

   public ConstrainedWholeBodyPlanningToolboxController(DRCRobotModel drcRobotModel, FullHumanoidRobotModel fullRobotModel,
                                                        StatusMessageOutputManager statusOutputManager, YoVariableRegistry registry,
                                                        YoGraphicsListRegistry yoGraphicsRegistry, boolean startYoVariableServer)
   {
      super(statusOutputManager, registry);
      this.drcRobotModelFactory = drcRobotModel;
      this.visualizedFullRobotModel = fullRobotModel;
      this.isDone.set(false);

      this.startYoVariableServer = startYoVariableServer;
      this.treeStateVisualizer = new TreeStateVisualizer("TreeStateVisualizer", "VisualizerGraphicsList", yoGraphicsRegistry, registry);
      this.state = CWBToolboxState.DO_NOTHING;

      this.endeffectorPose = new YoFramePose("endeffectorPose", ReferenceFrame.getWorldFrame(), registry);
      this.endeffectorFrame = new YoGraphicCoordinateSystem("endeffectorPoseFrame", this.endeffectorPose, 0.25);
      this.endeffectorFrame.setVisible(true);

      yoGraphicsRegistry.registerYoGraphic("endeffectorPoseViz", this.endeffectorFrame);

      state = CWBToolboxState.FIND_INITIAL_GUESS;
   }

   @Override
   protected void updateInternal()
   {
      updateCount.increment();
      PrintTools.info("update toolbox " + updateCount.getIntegerValue() + " " + state);

      // ************************************************************************************************************** //      
      switch (state)
      {
      case DO_NOTHING:

         break;
      case FIND_INITIAL_GUESS:

         findInitialGuess();

         break;
      case EXPAND_TREE:

         expandingTree();

         break;
      case SHORTCUT_PATH:

         shortcutPath();

         break;
      case GENERATE_MOTION:

         generateMotion();

         break;
      }
      // ************************************************************************************************************** //

      // ************************************************************************************************************** //

      updateVisualizerRobotConfiguration(kinematicsSolver.getFullRobotModelCopy());
      updateVisualizers();
      updateYoVariables();

      // ************************************************************************************************************** //      
      if (updateCount.getIntegerValue() == terminateToolboxCondition)
         isDone.set(true);
   }

   /**
    * state == GENERATE_MOTION
    */
   private void generateMotion()
   {
      int sizeOfPath = tree.getPath().size();

      kinematicsSolver.updateRobotConfigurationData(tree.getPath().get(sizeOfPath - numberOfMotionPath));

      kinematicsSolver.initialize();
      kinematicsSolver.holdCurrentTrajectoryMessages();
      kinematicsSolver.putTrajectoryMessages();

      numberOfMotionPath--;
      if (numberOfMotionPath == 0)
      {
         state = CWBToolboxState.DO_NOTHING;
         isDone.set(true);

         /*
          * generate WholeBodyTrajectoryMessage.
          */
         ctTaskNodeWholeBodyTrajectoryMessageFactory = new CTTaskNodeWholeBodyTrajectoryMessageFactory();
      }
   }

   /**
    * state = SHORTCUT_PATH
    */
   private void shortcutPath()
   {
      ArrayList<CTTaskNode> revertedPath = new ArrayList<CTTaskNode>();
      CTTaskNode currentNode = tree.getNewNode();
      revertedPath.add(currentNode);

      while (true)
      {
         currentNode = currentNode.getParentNode();
         if (currentNode != null)
         {
            revertedPath.add(currentNode);
         }
         else
            break;
      }

      int revertedPathSize = revertedPath.size();

      tree.getPath().clear();
      for (int j = 0; j < revertedPathSize; j++)
         tree.addNodeOnPath(revertedPath.get(revertedPathSize - 1 - j));

      PrintTools.info("the size of the path is " + tree.getPath().size());

      numberOfMotionPath = tree.getPath().size();
      /*
       * terminate state
       */
      state = CWBToolboxState.GENERATE_MOTION;
   }

   /**
    * state == EXPAND_TREE
    */
   private void expandingTree()
   {
      expandingCount.increment();

      tree.updateRandomConfiguration();
      tree.updateNearestNode();
      tree.updateNewConfiguration();
      tree.getNewNode().convertNormalizedDataToData(taskRegion);
      tree.getNewNode().setParentNode(tree.getNearNode());

      if (updateValidity(tree.getNewNode()))
      {
         tree.connectNewNode(true);
         if (tree.getNewNode().getTime() == constrainedEndEffectorTrajectory.getTrajectoryTime())
         {
            PrintTools.info("terminate expanding");
            numberOfExpanding = 1; // for terminate
         }
      }
      else
      {
         tree.connectNewNode(false);
      }

      visualizedNode = tree.getNewNode().createNodeCopy();
      visualizedNode.setValidity(tree.getNewNode().getValidity());

      /*
       * terminate expanding tree.
       */
      numberOfExpanding--;
      if (numberOfExpanding == 0)
      {
         state = CWBToolboxState.SHORTCUT_PATH;

         PrintTools.info("Total update solver");
      }
   }

   /**
    * state == FIND_INITIAL_GUESS
    */
   private void findInitialGuess()
   {
      GenericTaskNode initialGuessNode = new GenericTaskNode();

      tree.setRandomNormalizedNodeData(initialGuessNode, true);
      initialGuessNode.setNormalizedNodeData(0, 0);
      initialGuessNode.convertNormalizedDataToData(taskRegion);

      visualizedNode = initialGuessNode;

      updateValidity(visualizedNode);
      double scoreInitialGuess = kinematicsSolver.getArmJointLimitScore(constrainedEndEffectorTrajectory.getRobotSide());
      if (!visualizedNode.getValidity())
         scoreInitialGuess = 0.0;

      jointlimitScore.set(scoreInitialGuess);

      if (bestScoreInitialGuess < scoreInitialGuess)
      {
         bestScoreInitialGuess = scoreInitialGuess;

         rootNode = visualizedNode.createNodeCopy();
         rootNode = new GenericTaskNode(visualizedNode);
      }

      /*
       * terminate finding initial guess.
       */
      numberOfInitialGuess--;
      if (numberOfInitialGuess == 0)
      {
         PrintTools.info("initial guess terminate");
         state = CWBToolboxState.EXPAND_TREE;

         rootNode.convertDataToNormalizedData(taskRegion);

         PrintTools.info("" + bestScoreInitialGuess);
         for (int i = 0; i < rootNode.getDimensionOfNodeData(); i++)
            PrintTools.info("" + i + " " + rootNode.getNodeData(i));

         tree = new CTTaskNodeTree(rootNode);
         tree.setTaskRegion(taskRegion);
      }
   }

   @Override
   protected boolean initialize()
   {
      isDone.set(false);
      ConstrainedWholeBodyPlanningRequestPacket request = latestRequestReference.getAndSet(null);
      if (request == null)
         return false;

      PrintTools.info("initialize CWB toolbox");

      /*
       * bring control parameters from request.
       */
      numberOfExpanding = request.numberOfExpanding;
      numberOfInitialGuess = request.numberOfFindInitialGuess;

      initialConfiguration = request.initialConfiguration;

      /*
       * initialize kinematicsSolver.
       */
      kinematicsSolver = new WheneverWholeBodyKinematicsSolver(drcRobotModelFactory);

      kinematicsSolver.updateRobotConfigurationData(initialConfiguration);

      kinematicsSolver.initialize();
      kinematicsSolver.holdCurrentTrajectoryMessages();
      kinematicsSolver.putTrajectoryMessages();

      /*
       * start toolbox
       */
      rootNode = new GenericTaskNode();
      tree = new CTTaskNodeTree(rootNode);
      tree.setTaskRegion(constrainedEndEffectorTrajectory.getTaskRegion());

      rootNode.convertDataToNormalizedData(constrainedEndEffectorTrajectory.getTaskRegion());

      /*
       * bring constrainedEndEffectorTrajectory
       */
      taskRegion = constrainedEndEffectorTrajectory.getTaskRegion();
      if (startYoVariableServer)
      {
         treeVisualizer = new CTTreeVisualizer(tree);
         treeVisualizer.initialize();
      }

      return true;
   }

   @Override
   protected boolean isDone()
   {
      return isDone.getBooleanValue();
   }

   public PacketConsumer<ConstrainedWholeBodyPlanningRequestPacket> createRequestConsumer()
   {
      return new PacketConsumer<ConstrainedWholeBodyPlanningRequestPacket>()
      {
         @Override
         public void receivedPacket(ConstrainedWholeBodyPlanningRequestPacket packet)
         {
            if (packet == null)
               return;
            latestRequestReference.set(packet);
         }
      };
   }

   private ConstrainedWholeBodyPlanningToolboxOutputStatus packResult()
   {
      ConstrainedWholeBodyPlanningToolboxOutputStatus result = new ConstrainedWholeBodyPlanningToolboxOutputStatus();

      return result;
   }

   /**
    * update validity of input node. 
    */
   private boolean updateValidity(CTTaskNode node)
   {
      if (node.getParentNode() != null)
      {
         kinematicsSolver.updateRobotConfigurationData(node.getParentNode().getConfiguration());
      }
      else
      {
         kinematicsSolver.updateRobotConfigurationData(initialConfiguration);
      }

      kinematicsSolver.initialize();

      kinematicsSolver.holdCurrentTrajectoryMessages();

      /*
       * set whole body tasks.
       */
      ConfigurationSpace configurationSpace = new ConfigurationSpace();
      configurationSpace.setTranslation(node.getNodeData(5), node.getNodeData(6), node.getNodeData(7));
      configurationSpace.setRotation(node.getNodeData(8), node.getNodeData(9), node.getNodeData(10));

      /*
       * pose from 'constrainedEndEffectorTrajectory' is considered as in MidZUp
       * frame.
       */
      Pose3D desiredPose = constrainedEndEffectorTrajectory.getEndEffectorPose(node.getNodeData(0), configurationSpace);
      setEndEffectorPose(desiredPose);

      /*
       * for kinematics solver, append offset
       */
      desiredPose.appendTranslation(handCoordinateOffsetX, 0.0, 0.0);

      kinematicsSolver.setDesiredHandPose(constrainedEndEffectorTrajectory.getRobotSide(), desiredPose);

      Quaternion desiredChestOrientation = new Quaternion();
      desiredChestOrientation.appendYawRotation(node.getNodeData(2));

      desiredChestOrientation.appendPitchRotation(node.getNodeData(3));
      desiredChestOrientation.appendRollRotation(node.getNodeData(4));
      kinematicsSolver.setDesiredChestOrientation(desiredChestOrientation);

      kinematicsSolver.setDesiredPelvisHeight(node.getNodeData(1));

      kinematicsSolver.putTrajectoryMessages();

      /*
       * result
       */
      kinematicsSolver.solve();
      boolean result = kinematicsSolver.getResult();

      node.setConfigurationJoints(kinematicsSolver.getFullRobotModelCopy());

      node.setValidity(result);

      cntKinematicSolver.set(kinematicsSolver.getCntForUpdateInternal());

      return result;
   }

   /**
    * set fullRobotModel.
    */
   private void updateVisualizerRobotConfiguration(FullHumanoidRobotModel solverRobotModel)
   {
      visualizedFullRobotModel.getRootJoint().setPosition(solverRobotModel.getRootJoint().getTranslationForReading());
      visualizedFullRobotModel.getRootJoint().setRotation(solverRobotModel.getRootJoint().getRotationForReading());

      for (int i = 0; i < FullRobotModelUtils.getAllJointsExcludingHands(visualizedFullRobotModel).length; i++)
         FullRobotModelUtils.getAllJointsExcludingHands(visualizedFullRobotModel)[i].setQ(FullRobotModelUtils.getAllJointsExcludingHands(solverRobotModel)[i].getQ());
   }

   /**
    * update visualizers.
    */
   private void updateVisualizers()
   {
      if (visualizedNode != null)
      {
         treeStateVisualizer.setCurrentNormalizedTime(visualizedNode.getNormalizedNodeData(0));
         treeStateVisualizer.setCurrentCTTaskNodeValidity(visualizedNode.getValidity());
         treeStateVisualizer.updateVisualizer();

         currentIsValid.set(visualizedNode.getValidity());
         currentTrajectoryTime.set(visualizedNode.getNormalizedNodeData(0));
         if (startYoVariableServer)
            treeVisualizer.update(visualizedNode);
      }
   }

   /**
    * YoVariables.
    */
   private void updateYoVariables()
   {
      solutionQuality.set(kinematicsSolver.getSolution().getSolutionQuality());
      endeffectorFrame.setVisible(true);
      endeffectorFrame.update();
   }

   /**
    * update end effector pose
    */
   private void setEndEffectorPose(Pose3D desiredPose)
   {
      endeffectorPose.setPosition(desiredPose.getPosition());
      endeffectorPose.setOrientation(desiredPose.getOrientation());
   }
}
