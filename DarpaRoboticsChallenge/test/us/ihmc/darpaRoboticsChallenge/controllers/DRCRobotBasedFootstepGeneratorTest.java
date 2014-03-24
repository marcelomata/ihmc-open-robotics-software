package us.ihmc.darpaRoboticsChallenge.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import us.ihmc.SdfLoader.JaxbSDFLoader;
import us.ihmc.atlas.AtlasRobotModel;
import us.ihmc.commonWalkingControlModules.bipedSupportPolygons.ContactablePlaneBody;
import us.ihmc.commonWalkingControlModules.configurations.WalkingControllerParameters;
import us.ihmc.commonWalkingControlModules.desiredFootStep.Footstep;
import us.ihmc.commonWalkingControlModules.desiredFootStep.FootstepGeneratorVisualizer;
import us.ihmc.commonWalkingControlModules.desiredFootStep.FootstepUtils;
import us.ihmc.commonWalkingControlModules.desiredFootStep.FootstepValidityMetric;
import us.ihmc.commonWalkingControlModules.desiredFootStep.SemiCircularStepValidityMetric;
import us.ihmc.commonWalkingControlModules.desiredFootStep.footstepGenerator.TurningThenStraightFootstepGenerator;
import us.ihmc.commonWalkingControlModules.dynamics.FullRobotModel;
import us.ihmc.commonWalkingControlModules.referenceFrames.ReferenceFrames;
import us.ihmc.darpaRoboticsChallenge.DRCRobotModel;
import us.ihmc.darpaRoboticsChallenge.DRCRobotSDFLoader;
import us.ihmc.darpaRoboticsChallenge.drcRobot.DRCRobotJointMap;
import us.ihmc.darpaRoboticsChallenge.userInterface.DRCOperatorUserInterface;
import us.ihmc.robotSide.RobotSide;
import us.ihmc.robotSide.SideDependentList;
import us.ihmc.utilities.MemoryTools;
import us.ihmc.utilities.ThreadTools;
import us.ihmc.utilities.math.geometry.FramePoint2d;
import us.ihmc.utilities.math.geometry.FramePose2d;
import us.ihmc.utilities.math.geometry.ReferenceFrame;
import us.ihmc.utilities.math.overheadPath.TurnThenStraightOverheadPath;

import com.yobotics.simulationconstructionset.Robot;

public class DRCRobotBasedFootstepGeneratorTest
{
   private static final double eps = 1e-7;
   private final static boolean VISUALIZE = false;
   private final static boolean SIDESTEP = false;
   public static final ReferenceFrame WORLD_FRAME = ReferenceFrames.getWorldFrame();
   private List<Footstep> footSteps = new ArrayList<Footstep>();
   private FullRobotModel fullRobotModel;
   private ReferenceFrames referenceFrames;
   private SideDependentList<ContactablePlaneBody> bipedFeet;
   private WalkingControllerParameters walkingParamaters;


   @Before
   public void showMemoryUsageBeforeTest()
   {
      MemoryTools.printCurrentMemoryUsageAndReturnUsedMemoryInMB(getClass().getSimpleName() + " before test.");
   }

   @After
   public void showMemoryUsageAfterTest()
   {
      MemoryTools.printCurrentMemoryUsageAndReturnUsedMemoryInMB(getClass().getSimpleName() + " after test.");
   }


   @Test
   public void testStraightLinePath()
   {
      Point3d destination = new Point3d(5.0, 0.0, 0.0);
      testPathToDestination(destination);
   }

   @Test
   public void testAngledPaths()
   {
      double maxAngle = Math.PI;
      double angle = 0;
      int n = 10;
      for (int i = -n; i <= n; i++)
      {
         angle = (maxAngle * i) / n;
         Point3d destination = new Point3d(5.0 * Math.cos(angle), 5.0 * Math.sin(angle), 0.0);
         testPathToDestination(destination);
      }
   }

   private void testPathToDestination(Point3d destination)
   {
      setupRobotParameters();
      TurnThenStraightOverheadPath pathToDestination = new TurnThenStraightOverheadPath(new FramePose2d(WORLD_FRAME),
                                                          new FramePoint2d(WORLD_FRAME, destination.x, destination.y), SIDESTEP ? Math.PI / 2 : 0.0);
      generateFootstepsUsingPath(pathToDestination);
      FootstepValidityMetric footstepValidityMetric = new SemiCircularStepValidityMetric(fullRobotModel.getFoot(RobotSide.LEFT), 0.00, 1.2, 1.5);
      assertAllStepsLevelAndZeroHeight();
      assertAllStepsValid(footstepValidityMetric);
      assertLastStepIsPointingCorrectly(footSteps.get(footSteps.size() - 1), destination);
      if (VISUALIZE)
         FootstepGeneratorVisualizer.visualizeFootsteps(new Robot("null"), footSteps);
      if (VISUALIZE)
         ThreadTools.sleepForever();
   }

   public void generateFootstepsUsingPath(TurnThenStraightOverheadPath pathToDestination)
   {
      TurningThenStraightFootstepGenerator footstepGenerator = new TurningThenStraightFootstepGenerator(bipedFeet, pathToDestination, RobotSide.RIGHT);
      footstepGenerator.setTurningStepsHipOpeningStepAngle(Math.PI / 6);
      footstepGenerator.setStraightWalkingStepLength(0.4);
      footstepGenerator.setStraightWalkingStepWidth(0.2);
      footstepGenerator.setTurningStepsStepWidth(0.35);

      footSteps = footstepGenerator.generateDesiredFootstepList();
   }

   private static void assertLastStepIsPointingCorrectly(Footstep footstep, Point3d destination)
   {
      Vector3d footstepOrientation = SIDESTEP ? new Vector3d(0.0, -1.0, 0.0) : new Vector3d(1.0, 0.0, 0.0);
      footstep.getPoseCopy().getOrientationMatrix3d().transform(footstepOrientation);
      footstepOrientation.normalize();
      Vector3d pathOrientation = new Vector3d(destination);
      pathOrientation.normalize();
      assertEquals(pathOrientation.getX(), footstepOrientation.getX(), 1e-1);
      assertEquals(pathOrientation.getY(), footstepOrientation.getY(), 1e-1);
      assertEquals(pathOrientation.getZ(), footstepOrientation.getZ(), 1e-1);
   }

   private void assertAllStepsValid(FootstepValidityMetric footstepValidityMetric)
   {
      ArrayList<Footstep> testableFootstepQueue = prependStanceToFootstepQueue();
      for (int i = 0; i < testableFootstepQueue.size() - 2; i++)
      {
         Footstep swingStart = testableFootstepQueue.get(i);
         Footstep stance = testableFootstepQueue.get(i + 1);
         Footstep swingEnd = testableFootstepQueue.get(i + 2);
         assertTrue(footstepValidityMetric.assertValid(swingStart, stance, swingEnd));
      }
   }

   private void assertAllStepsLevelAndZeroHeight()
   {
      for (Footstep footstep : footSteps)
      {
         assertEquals(walkingParamaters.getAnkle_height(), footstep.getPoseCopy().getZ(), eps);
      }
   }

   private ArrayList<Footstep> prependStanceToFootstepQueue()
   {
      SideDependentList<Footstep> currentFootLocations = new SideDependentList<Footstep>();
      for (RobotSide side : RobotSide.values)
      {
         currentFootLocations.put(side, FootstepUtils.generateStandingFootstep(side, bipedFeet));
      }

      boolean firstStepIsLeft = footSteps.get(0).getBody().getRigidBody() == fullRobotModel.getFoot(RobotSide.LEFT);
      RobotSide firstStepSide = firstStepIsLeft ? RobotSide.LEFT : RobotSide.RIGHT;
      ArrayList<Footstep> footstepQueue = new ArrayList<Footstep>();
      footstepQueue.add(currentFootLocations.get(firstStepSide));
      footstepQueue.add(currentFootLocations.get(firstStepSide.getOppositeSide()));
      footstepQueue.addAll(footSteps);

      return footstepQueue;
   }

   private void setupRobotParameters()
   {
      DRCRobotModel robotModel = new AtlasRobotModel();
      DRCRobotJointMap jointMap = robotModel.getJointMap(false, false);
      JaxbSDFLoader jaxbSDFLoader = DRCRobotSDFLoader.loadDRCRobot(jointMap);
      walkingParamaters = robotModel.getWalkingControlParamaters();
      fullRobotModel = jaxbSDFLoader.createFullRobotModel(jointMap);
      referenceFrames = new ReferenceFrames(fullRobotModel, jointMap, jointMap.getAnkleHeight());
      bipedFeet = DRCOperatorUserInterface.setupBipedFeet(referenceFrames, fullRobotModel, walkingParamaters);
   }


}
