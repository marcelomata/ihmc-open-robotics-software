package us.ihmc.darpaRoboticsChallenge.testTools;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import us.ihmc.communication.net.PacketCommunicator;
import us.ihmc.communication.packetCommunicator.KryoPacketCommunicator;
import us.ihmc.communication.packets.behaviors.HumanoidBehaviorControlModePacket;
import us.ihmc.communication.packets.behaviors.HumanoidBehaviorType;
import us.ihmc.communication.packets.behaviors.HumanoidBehaviorTypePacket;
import us.ihmc.communication.subscribers.RobotDataReceiver;
import us.ihmc.darpaRoboticsChallenge.DRCStartingLocation;
import us.ihmc.darpaRoboticsChallenge.drcRobot.DRCRobotModel;
import us.ihmc.darpaRoboticsChallenge.environment.CommonAvatarEnvironmentInterface;
import us.ihmc.darpaRoboticsChallenge.environment.DRCDemo01NavigationEnvironment;
import us.ihmc.humanoidBehaviors.behaviors.BehaviorInterface;
import us.ihmc.humanoidBehaviors.communication.BehaviorCommunicationBridge;
import us.ihmc.humanoidBehaviors.dispatcher.BehaviorDisptacher;
import us.ihmc.humanoidBehaviors.dispatcher.HumanoidBehaviorControlModeSubscriber;
import us.ihmc.humanoidBehaviors.dispatcher.HumanoidBehaviorTypeSubscriber;
import us.ihmc.robotDataCommunication.YoVariableServer;
import us.ihmc.simulationconstructionset.bambooTools.SimulationTestingParameters;
import us.ihmc.simulationconstructionset.util.simulationRunner.BlockingSimulationRunner.SimulationExceededMaximumTimeException;
import us.ihmc.utilities.humanoidRobot.frames.ReferenceFrames;
import us.ihmc.utilities.humanoidRobot.model.ForceSensorDataHolder;
import us.ihmc.utilities.humanoidRobot.model.FullRobotModel;
import us.ihmc.yoUtilities.dataStructure.registry.YoVariableRegistry;
import us.ihmc.yoUtilities.dataStructure.variable.DoubleYoVariable;
import us.ihmc.yoUtilities.graphics.YoGraphicsListRegistry;

public class DRCBehaviorTestHelper extends DRCSimulationTestHelper
{
   private final YoVariableRegistry registry = new YoVariableRegistry(getClass().getSimpleName());
   private DoubleYoVariable yoTime = new DoubleYoVariable("yoTime", registry);

   private final KryoPacketCommunicator networkObjectCommunicator;
   private final BehaviorCommunicationBridge behaviorCommunicationBridge;
   private final RobotDataReceiver robotDataReceiver;
   private final BehaviorDisptacher behaviorDispatcher;
   private final ReferenceFrames referenceFrames;
   private final FullRobotModel fullRobotModel;
   
   public DRCBehaviorTestHelper(String name, String scriptFileName, DRCStartingLocation selectedLocation, SimulationTestingParameters simulationTestingParameters, DRCRobotModel robotModel, FullRobotModel fullRobotModel, KryoPacketCommunicator networkObjectCommunicator,
         PacketCommunicator controllerCommunicator)
   {
      this(new DRCDemo01NavigationEnvironment(), networkObjectCommunicator, name, scriptFileName, selectedLocation, simulationTestingParameters,
            false, robotModel, fullRobotModel, controllerCommunicator);
   }

   public DRCBehaviorTestHelper(CommonAvatarEnvironmentInterface commonAvatarEnvironmentInterface, String name, String scriptFileName,
         DRCStartingLocation selectedLocation, SimulationTestingParameters simulationTestingParameters, DRCRobotModel robotModel,
         FullRobotModel fullRobotModel, KryoPacketCommunicator networkObjectCommunicator, PacketCommunicator controllerCommunicator)
   {
      this(commonAvatarEnvironmentInterface, networkObjectCommunicator, name, scriptFileName, selectedLocation, simulationTestingParameters,
            false, robotModel, fullRobotModel, controllerCommunicator);
   }

   public DRCBehaviorTestHelper(CommonAvatarEnvironmentInterface commonAvatarEnvironmentInterface, KryoPacketCommunicator networkObjectCommunicator, String name,
         String scriptFileName, DRCStartingLocation selectedLocation, SimulationTestingParameters simulationTestingParameters, boolean startNetworkProcessor, DRCRobotModel robotModel, FullRobotModel fullRobotModel, PacketCommunicator controllerCommunicator)
   {
      super(commonAvatarEnvironmentInterface, controllerCommunicator, name, scriptFileName, selectedLocation, simulationTestingParameters,
            startNetworkProcessor, robotModel);

      this.fullRobotModel = fullRobotModel;
      YoGraphicsListRegistry yoGraphicsListRegistry = new YoGraphicsListRegistry();

      ForceSensorDataHolder forceSensorDataHolder = new ForceSensorDataHolder(Arrays.asList(fullRobotModel.getForceSensorDefinitions()));
      robotDataReceiver = new RobotDataReceiver(fullRobotModel, forceSensorDataHolder);
      behaviorCommunicationBridge = new BehaviorCommunicationBridge(networkObjectCommunicator, controllerCommunicator, registry);

      this.networkObjectCommunicator = networkObjectCommunicator;
      behaviorDispatcher = setupBehaviorDispatcher(fullRobotModel, networkObjectCommunicator, controllerCommunicator, robotDataReceiver, yoGraphicsListRegistry);
      
      referenceFrames = robotDataReceiver.getReferenceFrames();
   }
   
   public FullRobotModel getFullRobotModel()
   {
      return fullRobotModel;
   }

   public ReferenceFrames getReferenceFrames()
   {
      return referenceFrames;
   }
   
   public BehaviorDisptacher getBehaviorDisptacher()
   {
      return behaviorDispatcher;
   }

   public RobotDataReceiver getRobotDataReceiver()
   {
      return robotDataReceiver;
   }

   public BehaviorCommunicationBridge getBehaviorCommunicationBridge()
   {
      return behaviorCommunicationBridge;
   }
   
   public void updateRobotModel()
   {
      robotDataReceiver.updateRobotModel();
   }

   public void dispatchBehavior(BehaviorInterface behaviorToTest) throws SimulationExceededMaximumTimeException
   {
      HumanoidBehaviorType testBehaviorType = HumanoidBehaviorType.TEST;
      behaviorDispatcher.addHumanoidBehavior(testBehaviorType, behaviorToTest);

      Thread dispatcherThread = new Thread(behaviorDispatcher, "BehaviorDispatcher");
      dispatcherThread.start();
      
      HumanoidBehaviorTypePacket requestTestBehaviorPacket = new HumanoidBehaviorTypePacket(testBehaviorType);
      networkObjectCommunicator.send(requestTestBehaviorPacket);

      boolean success = simulateAndBlockAndCatchExceptions(1.0);
      assertTrue(success);
   }

   private BehaviorDisptacher setupBehaviorDispatcher(FullRobotModel fullRobotModel, PacketCommunicator networkObjectCommunicator,
         PacketCommunicator controllerCommunicator, RobotDataReceiver robotDataReceiver, YoGraphicsListRegistry yoGraphicsListRegistry)
   {
      HumanoidBehaviorControlModeSubscriber desiredBehaviorControlSubscriber = new HumanoidBehaviorControlModeSubscriber();
      networkObjectCommunicator.attachListener(HumanoidBehaviorControlModePacket.class, desiredBehaviorControlSubscriber);

      HumanoidBehaviorTypeSubscriber desiredBehaviorSubscriber = new HumanoidBehaviorTypeSubscriber();
      networkObjectCommunicator.attachListener(HumanoidBehaviorTypePacket.class, desiredBehaviorSubscriber);

      YoVariableServer yoVariableServer = null;
      yoGraphicsListRegistry.setYoGraphicsUpdatedRemotely(false);

      BehaviorDisptacher ret = new BehaviorDisptacher(yoTime, robotDataReceiver, desiredBehaviorControlSubscriber, desiredBehaviorSubscriber,
            behaviorCommunicationBridge, yoVariableServer, registry, yoGraphicsListRegistry);

      return ret;
   }

   public void closeAndDispose()
   {
      if (networkObjectCommunicator != null)
      {
         networkObjectCommunicator.close();
         networkObjectCommunicator.closeAndDispose();
//         networkObjectCommunicator = null;
      }
      
      if (behaviorCommunicationBridge != null)
      {
         behaviorCommunicationBridge.closeAndDispose();
//         behaviorCommunicationBridge = null;
      }
      
      super.destroySimulation();
   }
   
   public boolean executeBehaviorSimulateAndBlockAndCatchExceptions(final BehaviorInterface behavior, double simulationRunTime) throws SimulationExceededMaximumTimeException
   {
      BehaviorRunner behaviorRunner = new BehaviorRunner(behavior);
      Thread behaviorThread = new Thread(behaviorRunner);
      behaviorThread.start();

      boolean ret = simulateAndBlockAndCatchExceptions(simulationRunTime);
      behaviorRunner.closeAndDispose();

      return ret;
   }
   
   private class BehaviorRunner implements Runnable
   {
      private boolean isRunning = true;
      private final BehaviorInterface behavior;

      public BehaviorRunner(BehaviorInterface behavior)
      {
         this.behavior = behavior;
      }

      public void run()
      {
         while (isRunning)
         {
            behavior.doControl();
         }
      }

      public void closeAndDispose()
      {
         isRunning = false;
      }
   }

}
