package us.ihmc.commonWalkingControlModules.momentumBasedController.optimization;

import org.ejml.data.DenseMatrix64F;
import org.junit.Test;
import us.ihmc.commonWalkingControlModules.bipedSupportPolygons.ContactablePlaneBodyTools;
import us.ihmc.commonWalkingControlModules.configurations.JointPrivilegedConfigurationParameters;
import us.ihmc.commonWalkingControlModules.controllerCore.WholeBodyControlCoreToolbox;
import us.ihmc.commonWalkingControlModules.momentumBasedController.GeometricJacobianHolder;
import us.ihmc.commonWalkingControlModules.momentumBasedController.HighLevelHumanoidControllerToolbox;
import us.ihmc.commonWalkingControlModules.wrenchDistribution.WrenchMatrixCalculator;
import us.ihmc.graphics3DDescription.yoGraphics.YoGraphicsListRegistry;
import us.ihmc.humanoidRobotics.bipedSupportPolygons.ContactablePlaneBody;
import us.ihmc.humanoidRobotics.frames.HumanoidReferenceFrames;
import us.ihmc.robotModels.FullHumanoidRobotModel;
import us.ihmc.robotModels.FullRobotModelTestTools;
import us.ihmc.robotics.dataStructures.registry.YoVariableRegistry;
import us.ihmc.robotics.random.RandomTools;
import us.ihmc.robotics.referenceFrames.ReferenceFrame;
import us.ihmc.robotics.robotSide.RobotSide;
import us.ihmc.robotics.screwTheory.*;
import us.ihmc.sensorProcessing.frames.CommonHumanoidReferenceFrames;
import us.ihmc.tools.continuousIntegration.ContinuousIntegrationAnnotations.ContinuousIntegrationTest;
import us.ihmc.tools.testing.JUnitTools;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class DynamicsMatrixCalculatorTest
{
   private final Random random = new Random(5641654L);

   private FullHumanoidRobotModel fullHumanoidRobotModel;
   private WholeBodyControlCoreToolbox toolbox;

   private WrenchMatrixCalculator wrenchMatrixCalculator;
   private JointIndexHandler jointIndexHandler;

   private InverseDynamicsCalculator inverseDynamicsCalculator;
   private DynamicsMatrixCalculator dynamicsMatrixCalculator;

   int degreesOfFreedom;
   int floatingBaseDoFs;
   int bodyDoFs;

   @ContinuousIntegrationTest(estimatedDuration = 1.1)
   @Test(timeout = 30000)
   public void testEquivalence() throws Exception
   {
      setupTest();

      ArrayList<OneDoFJoint> joints = new ArrayList<>();
      fullHumanoidRobotModel.getOneDoFJoints(joints);

      ScrewTestTools.setRandomPositions(joints, random);
      ScrewTestTools.setRandomVelocities(joints, random);

      DenseMatrix64F rhoSolution = RandomTools.generateRandomMatrix(random, wrenchMatrixCalculator.getRhoSize(), 1, 0.0, 1000.0);
      DenseMatrix64F qddotSolution = RandomTools.generateRandomMatrix(random, degreesOfFreedom, 1);

      solveAndCompare(qddotSolution, rhoSolution);
   }

   @ContinuousIntegrationTest(estimatedDuration = 1.1)
   @Test(timeout = 30000)
   public void testCoriolisAndGravityOnly() throws Exception
   {
      setupTest();

      ArrayList<OneDoFJoint> joints = new ArrayList<>();
      fullHumanoidRobotModel.getOneDoFJoints(joints);

      ScrewTestTools.setRandomPositions(joints, random);
      ScrewTestTools.setRandomVelocities(joints, random);

      DenseMatrix64F rhoSolution = new DenseMatrix64F(wrenchMatrixCalculator.getRhoSize(), 1);
      DenseMatrix64F qddotSolution = new DenseMatrix64F(degreesOfFreedom, 1);

      solveAndCompare(qddotSolution, rhoSolution);
   }

   @ContinuousIntegrationTest(estimatedDuration = 1.1)
   @Test(timeout = 30000)
   public void testMassMatrixAndGravityOnly() throws Exception
   {
      setupTest();

      ArrayList<OneDoFJoint> joints = new ArrayList<>();
      fullHumanoidRobotModel.getOneDoFJoints(joints);

      ScrewTestTools.setRandomPositions(joints, random);

      DenseMatrix64F rhoSolution = new DenseMatrix64F(wrenchMatrixCalculator.getRhoSize(), 1);
      DenseMatrix64F qddotSolution = RandomTools.generateRandomMatrix(random, degreesOfFreedom, 1);

      solveAndCompare(qddotSolution, rhoSolution);
   }

   @ContinuousIntegrationTest(estimatedDuration = 1.1)
   @Test(timeout = 30000)
   public void testForceAndGravityOnly() throws Exception
   {
      setupTest();

      ArrayList<OneDoFJoint> joints = new ArrayList<>();
      fullHumanoidRobotModel.getOneDoFJoints(joints);

      ScrewTestTools.setRandomPositions(joints, random);

      DenseMatrix64F rhoSolution = RandomTools.generateRandomMatrix(random, wrenchMatrixCalculator.getRhoSize(), 1, 0.0, 1000.0);
      DenseMatrix64F qddotSolution = new DenseMatrix64F(degreesOfFreedom, 1);

      solveAndCompare(qddotSolution, rhoSolution);
   }

   private void setupTest()
   {
      YoVariableRegistry registry = new YoVariableRegistry(getClass().getSimpleName());
      YoGraphicsListRegistry yoGraphicsListRegistry = new YoGraphicsListRegistry();

      double gravityZ = 9.81;
      double controlDT = 0.005;

      fullHumanoidRobotModel = new FullRobotModelTestTools.RandomFullHumanoidRobotModel(random);
      fullHumanoidRobotModel.updateFrames();
      CommonHumanoidReferenceFrames referenceFrames = new HumanoidReferenceFrames(fullHumanoidRobotModel);

      TwistCalculator twistCalculator = new TwistCalculator(ReferenceFrame.getWorldFrame(), fullHumanoidRobotModel.getElevator());
      GeometricJacobianHolder geometricJacobianHolder = new GeometricJacobianHolder();

      MomentumOptimizationSettings momentumOptimizationSettings = new MomentumOptimizationSettings();
      JointPrivilegedConfigurationParameters jointPrivilegedConfigurationParameters = new JointPrivilegedConfigurationParameters();
      ArrayList<ContactablePlaneBody> contactablePlaneBodies = new ArrayList<>();
      for (RobotSide robotSide : RobotSide.values)
      {
         RigidBody footBody = fullHumanoidRobotModel.getFoot(robotSide);
         ReferenceFrame soleFrame = fullHumanoidRobotModel.getSoleFrame(robotSide);
         contactablePlaneBodies.add(ContactablePlaneBodyTools.createTypicalContactablePlaneBodyForTests(footBody, soleFrame));
      }

      InverseDynamicsJoint[] jointsToOptimizeFor = HighLevelHumanoidControllerToolbox.computeJointsToOptimizeFor(fullHumanoidRobotModel, new InverseDynamicsJoint[0]);
      toolbox = new WholeBodyControlCoreToolbox(fullHumanoidRobotModel, null, jointsToOptimizeFor, momentumOptimizationSettings,
            jointPrivilegedConfigurationParameters, referenceFrames, controlDT, gravityZ, geometricJacobianHolder, twistCalculator, contactablePlaneBodies,
            yoGraphicsListRegistry, registry);

      wrenchMatrixCalculator = new WrenchMatrixCalculator(toolbox, registry);
      jointIndexHandler = toolbox.getJointIndexHandler();

      inverseDynamicsCalculator = new InverseDynamicsCalculator(twistCalculator, gravityZ);
      dynamicsMatrixCalculator = new DynamicsMatrixCalculator(toolbox, wrenchMatrixCalculator);

      degreesOfFreedom = jointIndexHandler.getNumberOfDoFs();
      floatingBaseDoFs = fullHumanoidRobotModel.getRootJoint().getDegreesOfFreedom();
      bodyDoFs = degreesOfFreedom - floatingBaseDoFs;
   }

   private void solveAndCompare(DenseMatrix64F qddotSolution, DenseMatrix64F rhoSolution)
   {
      fullHumanoidRobotModel.updateFrames();
      toolbox.getTwistCalculator().compute();
      toolbox.getGeometricJacobianHolder().compute();

      wrenchMatrixCalculator.computeMatrices();
      Map<RigidBody, Wrench> contactWrenches = wrenchMatrixCalculator.computeWrenchesFromRho(rhoSolution);
      for (int i = 0; i < toolbox.getContactablePlaneBodies().size(); i++)
      {
         RigidBody rigidBody = toolbox.getContactablePlaneBodies().get(i).getRigidBody();
         inverseDynamicsCalculator.setExternalWrench(rigidBody, contactWrenches.get(rigidBody));
      }

      DenseMatrix64F inverseDynamicsTauSolution = new DenseMatrix64F(bodyDoFs, 1);
      DenseMatrix64F dynamicsMatrixTauSolution = new DenseMatrix64F(bodyDoFs, 1);

      // compute torques using inverse dynamics calculator
      ScrewTools.setDesiredAccelerations(fullHumanoidRobotModel.getOneDoFJoints(), qddotSolution);
      inverseDynamicsCalculator.compute();

      DenseMatrix64F tmpTauMatrix = new DenseMatrix64F(1, 1);
      for (int i = 0; i < fullHumanoidRobotModel.getOneDoFJoints().length; i++)
      {
         fullHumanoidRobotModel.getOneDoFJoints()[i].getTauMatrix(tmpTauMatrix);
         inverseDynamicsTauSolution.set(i, tmpTauMatrix.get(0, 0));
      }

      // compute torques using dynamics matrix calculator
      dynamicsMatrixCalculator.compute();
      dynamicsMatrixCalculator.computeJointTorques(dynamicsMatrixTauSolution, qddotSolution, rhoSolution);

      JUnitTools.assertMatrixEquals(inverseDynamicsTauSolution, dynamicsMatrixTauSolution, 1.0);
   }
}
