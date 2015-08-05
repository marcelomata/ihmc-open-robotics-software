package us.ihmc.sensorProcessing.simulatedSensors;

import us.ihmc.sensorProcessing.sensors.RawJointSensorDataHolderMap;
import us.ihmc.robotics.sensors.IMUDefinition;
import us.ihmc.robotics.sensors.ContactSensorHolder;
import us.ihmc.humanoidRobotics.model.DesiredJointDataHolder;
import us.ihmc.robotics.sensors.ForceSensorDefinition;
import us.ihmc.robotics.screwTheory.SixDoFJoint;
import us.ihmc.yoUtilities.dataStructure.registry.YoVariableRegistry;

public interface SensorReaderFactory
{
   public abstract void build(SixDoFJoint rootJoint, IMUDefinition[] imuDefinitions, ForceSensorDefinition[] forceSensorDefinitions,
         ContactSensorHolder contactSensorHolder, RawJointSensorDataHolderMap rawJointSensorDataHolderMap,
         DesiredJointDataHolder estimatorDesiredJointDataHolder, YoVariableRegistry parentRegistry);

   public abstract SensorReader getSensorReader();

   public abstract StateEstimatorSensorDefinitions getStateEstimatorSensorDefinitions();

   public abstract boolean useStateEstimator();

}