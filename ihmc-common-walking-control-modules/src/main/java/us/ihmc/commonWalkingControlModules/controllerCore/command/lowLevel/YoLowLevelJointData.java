package us.ihmc.commonWalkingControlModules.controllerCore.command.lowLevel;

import us.ihmc.robotics.screwTheory.OneDoFJoint;
import us.ihmc.sensorProcessing.outputData.LowLevelJointControlMode;
import us.ihmc.sensorProcessing.outputData.LowLevelJointDataReadOnly;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoBoolean;
import us.ihmc.yoVariables.variable.YoDouble;
import us.ihmc.yoVariables.variable.YoEnum;

public class YoLowLevelJointData implements LowLevelJointDataReadOnly
{
   private final YoEnum<LowLevelJointControlMode> controlMode;
   private final YoDouble desiredTorque;
   private final YoDouble desiredPosition;
   private final YoDouble desiredVelocity;
   private final YoDouble desiredAcceleration;
   private final YoDouble desiredCurrent;
   private final YoBoolean resetIntegrators;

   private final YoDouble kp;
   private final YoDouble kd;

   public YoLowLevelJointData(String namePrefix, YoVariableRegistry registry, String suffixString)
   {
      namePrefix += "LowLevel";

      controlMode = new YoEnum<>(namePrefix + "ControlMode" + suffixString, registry, LowLevelJointControlMode.class, true);
      desiredTorque = new YoDouble(namePrefix + "DesiredTorque" + suffixString, registry);
      desiredPosition = new YoDouble(namePrefix + "DesiredPosition" + suffixString, registry);
      desiredVelocity = new YoDouble(namePrefix + "DesiredVelocity" + suffixString, registry);
      desiredAcceleration = new YoDouble(namePrefix + "DesiredAcceleration" + suffixString, registry);
      desiredCurrent = new YoDouble(namePrefix + "DesiredCurrent" + suffixString, registry);
      resetIntegrators = new YoBoolean(namePrefix + "ResetIntegrators" + suffixString, registry);

      kp = new YoDouble(namePrefix + "Kp" + suffixString, registry);
      kd = new YoDouble(namePrefix + "Kd" + suffixString, registry);

      clear();
   }

   public void clear()
   {
      controlMode.set(null);
      desiredTorque.set(Double.NaN);
      desiredPosition.set(Double.NaN);
      desiredVelocity.set(Double.NaN);
      desiredAcceleration.set(Double.NaN);
      desiredCurrent.set(Double.NaN);
      kp.set(Double.NaN);
      kd.set(Double.NaN);
      resetIntegrators.set(false);
   }

   public void set(LowLevelJointDataReadOnly other)
   {
      clear();
      if (other.hasControlMode())
         controlMode.set(other.getControlMode());
      if (other.hasDesiredTorque())
         desiredTorque.set(other.getDesiredTorque());
      if (other.hasDesiredPosition())
         desiredPosition.set(other.getDesiredPosition());
      if (other.hasDesiredVelocity())
         desiredVelocity.set(other.getDesiredVelocity());
      if (other.hasDesiredAcceleration())
         desiredAcceleration.set(other.getDesiredAcceleration());
      if (other.hasDesiredCurrent())
         desiredCurrent.set(other.getDesiredCurrent());
      if (other.hasKp())
         kp.set(other.getKp());
      if (other.hasKd())
         kd.set(other.getKd());
      resetIntegrators.set(other.peekResetIntegratorsRequest());
   }

   /**
    * Complete the information held in this using other.
    * Does not overwrite the data already set in this.
    */
   public void completeWith(LowLevelJointDataReadOnly other)
   {
      if (!hasControlMode() && other.hasControlMode())
         controlMode.set(other.getControlMode());
      if (!hasDesiredTorque() && other.hasDesiredTorque())
         desiredTorque.set(other.getDesiredTorque());
      if (!hasDesiredPosition() && other.hasDesiredPosition())
         desiredPosition.set(other.getDesiredPosition());
      if (!hasDesiredVelocity() && other.hasDesiredVelocity())
         desiredVelocity.set(other.getDesiredVelocity());
      if (!hasDesiredAcceleration() && other.hasDesiredAcceleration())
         desiredAcceleration.set(other.getDesiredAcceleration());
      if (!hasDesiredCurrent() && other.hasDesiredCurrent())
         desiredCurrent.set(other.getDesiredCurrent());
      if (!hasKp() && other.hasKp())
         kp.set(other.getKp());
      if (!hasKd() && other.hasKd())
         kd.set(other.getKd());
      if (!peekResetIntegratorsRequest())
         resetIntegrators.set(other.peekResetIntegratorsRequest());
   }

   public void setDesiredsFromOneDoFJoint(OneDoFJoint jointToExtractDesiredsFrom)
   {
      setDesiredTorque(jointToExtractDesiredsFrom.getTau());
      setDesiredPosition(jointToExtractDesiredsFrom.getqDesired());
      setDesiredVelocity(jointToExtractDesiredsFrom.getQdDesired());
      setDesiredAcceleration(jointToExtractDesiredsFrom.getQddDesired());
      setResetIntegrators(jointToExtractDesiredsFrom.getResetIntegrator());
      setKp(jointToExtractDesiredsFrom.getKp());
      setKd(jointToExtractDesiredsFrom.getKd());
   }

   public void setControlMode(LowLevelJointControlMode controlMode)
   {
      this.controlMode.set(controlMode);
   }

   public void setDesiredTorque(double tau)
   {
      desiredTorque.set(tau);
   }

   public void setDesiredPosition(double q)
   {
      desiredPosition.set(q);
   }

   public void setDesiredVelocity(double qd)
   {
      desiredVelocity.set(qd);
   }

   public void setDesiredAcceleration(double qdd)
   {
      desiredAcceleration.set(qdd);
   }

   public void setDesiredCurrent(double i)
   {
      desiredCurrent.set(i);
   }

   public void setResetIntegrators(boolean reset)
   {
      resetIntegrators.set(reset);
   }

   @Override
   public boolean hasControlMode()
   {
      return controlMode.getEnumValue() != null;
   }

   @Override
   public boolean hasDesiredTorque()
   {
      return !desiredTorque.isNaN();
   }

   @Override
   public boolean hasDesiredPosition()
   {
      return !desiredPosition.isNaN();
   }

   @Override
   public boolean hasDesiredVelocity()
   {
      return !desiredVelocity.isNaN();
   }

   @Override
   public boolean hasDesiredAcceleration()
   {
      return !desiredAcceleration.isNaN();
   }

   @Override
   public boolean hasDesiredCurrent()
   {
      return !desiredCurrent.isNaN();
   }

   @Override
   public LowLevelJointControlMode getControlMode()
   {
      return controlMode.getEnumValue();
   }

   @Override
   public double getDesiredTorque()
   {
      return desiredTorque.getDoubleValue();
   }

   @Override
   public double getDesiredPosition()
   {
      return desiredPosition.getDoubleValue();
   }

   @Override
   public double getDesiredVelocity()
   {
      return desiredVelocity.getDoubleValue();
   }

   @Override
   public double getDesiredAcceleration()
   {
      return desiredAcceleration.getDoubleValue();
   }

   @Override
   public double getDesiredCurrent()
   {
      return desiredCurrent.getDoubleValue();
   }

   @Override
   public boolean pollResetIntegratorsRequest()
   {
      boolean request = resetIntegrators.getBooleanValue();
      resetIntegrators.set(false);
      return request;
   }

   @Override
   public boolean peekResetIntegratorsRequest()
   {
      return resetIntegrators.getBooleanValue();
   }

   @Override
   public String toString()
   {
      String ret = "controlMode = " + getControlMode() + "\n";
      ret += "desiredTorque = " + getDesiredTorque() + "\n";
      ret += "desiredPosition = " + getDesiredPosition() + "\n";
      ret += "desiredVelocity = " + getDesiredVelocity() + "\n";
      ret += "desiredAcceleration = " + getDesiredAcceleration() + "\n";
      ret += "desiredCurrent = " + getDesiredCurrent() + "\n";
      return ret;
   }

   @Override
   public boolean hasKp()
   {
      return !kp.isNaN();
   }

   @Override
   public boolean hasKd()
   {
      return kd.isNaN();
   }

   @Override
   public double getKp()
   {
      return kp.getValue();
   }

   @Override
   public double getKd()
   {
      return kd.getValue();
   }

   public void setKp(double kp)
   {
      this.kp.set(kp);
   }

   public void setKd(double kd)
   {
      this.kd.set(kd);
   }
}
