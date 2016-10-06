package us.ihmc.atlas;

import org.junit.Assume;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.internal.AssumptionViolatedException;

import us.ihmc.darpaRoboticsChallenge.DRCFlatGroundWalkingTest;
import us.ihmc.darpaRoboticsChallenge.drcRobot.DRCRobotModel;
import us.ihmc.simulationconstructionset.bambooTools.BambooTools;
import us.ihmc.simulationconstructionset.util.simulationRunner.BlockingSimulationRunner.SimulationExceededMaximumTimeException;
import us.ihmc.simulationconstructionset.util.simulationRunner.ControllerFailureException;
import us.ihmc.tools.testing.TestPlanAnnotations.ContinuousIntegrationPlan;
import us.ihmc.tools.testing.TestPlanAnnotations.ContinuousIntegrationTest;
import us.ihmc.tools.testing.TestPlanTarget;

// This test is slow but very important, let's keep it in the FAST build please. (Sylvain)
@ContinuousIntegrationPlan(targets = {TestPlanTarget.Fast, TestPlanTarget.Video})
public class AtlasFlatGroundWalkingTest extends DRCFlatGroundWalkingTest
{
   private DRCRobotModel robotModel;

   @ContinuousIntegrationTest(estimatedDuration = 189.1)
   @Test(timeout = 950000)
   public void testAtlasFlatGroundWalking() throws SimulationExceededMaximumTimeException, ControllerFailureException
   {
      robotModel = new AtlasRobotModel(AtlasRobotVersion.ATLAS_UNPLUGGED_V5_NO_HANDS, DRCRobotModel.RobotTarget.SCS, false);
      super.testFlatGroundWalking(robotModel, true);
   }

	@ContinuousIntegrationTest(estimatedDuration = 0.3)
	@Test(timeout = 30000)
	@Ignore // Not working because of multithreading. Should be switched over to use the DRCSimulationTestHelper.
   public void testFlatGroundWalkingRunsSameWayTwice() throws SimulationExceededMaximumTimeException, ControllerFailureException
   {
      try
      {
         Assume.assumeTrue(BambooTools.isNightlyBuild());
         BambooTools.reportTestStartedMessage(getSimulationTestingParameters().getShowWindows());

         robotModel = new AtlasRobotModel(AtlasRobotVersion.ATLAS_UNPLUGGED_V5_NO_HANDS, DRCRobotModel.RobotTarget.SCS, false);

         setupAndTestFlatGroundSimulationTrackTwice(robotModel);
      }
      catch(AssumptionViolatedException e)
      {
         System.out.println("Not Nightly Build, skipping AtlasFlatGroundWalkingTest.testFlatGroundWalkingRunsSameWayTwice");
      }
   }

   @Override
   public DRCRobotModel getRobotModel()
   {
      return robotModel;
   }

   @Override
   public String getSimpleRobotName()
   {
      return BambooTools.getSimpleRobotNameFor(BambooTools.SimpleRobotNameKeys.ATLAS);
   }
}
