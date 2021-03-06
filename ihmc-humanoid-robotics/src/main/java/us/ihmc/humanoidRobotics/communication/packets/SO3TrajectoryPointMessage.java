package us.ihmc.humanoidRobotics.communication.packets;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import us.ihmc.commons.RandomNumbers;
import us.ihmc.communication.packets.Packet;
import us.ihmc.communication.ros.generators.RosExportedField;
import us.ihmc.communication.ros.generators.RosMessagePacket;
import us.ihmc.euclid.interfaces.Transformable;
import us.ihmc.euclid.transform.interfaces.Transform;
import us.ihmc.euclid.tuple3D.Vector3D;
import us.ihmc.euclid.tuple3D.interfaces.Vector3DBasics;
import us.ihmc.euclid.tuple3D.interfaces.Vector3DReadOnly;
import us.ihmc.euclid.tuple4D.Quaternion;
import us.ihmc.euclid.tuple4D.interfaces.QuaternionBasics;
import us.ihmc.euclid.tuple4D.interfaces.QuaternionReadOnly;
import us.ihmc.commons.MathTools;
import us.ihmc.robotics.random.RandomGeometry;

@RosMessagePacket(documentation =
      "This class is used to build trajectory messages in taskspace. It holds the only the rotational information for one trajectory point (orientation & angular velocity). "
      + "Feel free to look at EuclideanTrajectoryPointMessage (translational) and SE3TrajectoryPointMessage (rotational AND translational)",
      rosPackage = RosMessagePacket.CORE_IHMC_PACKAGE)
public class SO3TrajectoryPointMessage extends Packet<SO3TrajectoryPointMessage> implements Transformable
      //implements SO3TrajectoryPointInterface<SO3TrajectoryPointMessage>, TransformableDataObject<SO3TrajectoryPointMessage>
{
   @RosExportedField(documentation = "Time at which the trajectory point has to be reached. The time is relative to when the trajectory starts.")
   public double time;
   @RosExportedField(documentation = "Define the desired 3D orientation to be reached at this trajectory point.")
   public Quaternion orientation;
   @RosExportedField(documentation = "Define the desired 3D angular velocity to be reached at this trajectory point.")
   public Vector3D angularVelocity;

   /**
    * Empty constructor for serialization.
    */
   public SO3TrajectoryPointMessage()
   {
   }

   public SO3TrajectoryPointMessage(Random random)
   {
      time = RandomNumbers.nextDoubleWithEdgeCases(random, 0.01);
      orientation = RandomGeometry.nextQuaternion(random);
      angularVelocity = RandomGeometry.nextVector3D(random);
   }

   public SO3TrajectoryPointMessage(SO3TrajectoryPointMessage trajectoryPoint)
   {
      time = trajectoryPoint.time;
      if (trajectoryPoint.orientation != null)
         orientation = new Quaternion(trajectoryPoint.orientation);
      if (trajectoryPoint.angularVelocity != null)
         angularVelocity = new Vector3D(trajectoryPoint.angularVelocity);
   }

   public SO3TrajectoryPointMessage(double time, QuaternionReadOnly orientation, Vector3DReadOnly angularVelocity)
   {
      this.time = time;
      this.orientation = new Quaternion(orientation);
      this.angularVelocity = new Vector3D(angularVelocity);
   }

   public void set(SO3TrajectoryPointMessage other)
   {
      time = other.time;
      if (other.orientation != null)
         orientation.set(other.orientation);
      else
         orientation.setToZero();
      if (other.angularVelocity != null)
         angularVelocity.set(other.angularVelocity);
      else
         angularVelocity.setToZero();
   }

   public double getTime()
   {
      return time;
   }

   public void setTime(double time)
   {
      this.time = time;
   }

   public void getOrientation(QuaternionBasics orientationToPack)
   {
      orientationToPack.set(orientation);
   }

   public void setOrientation(QuaternionReadOnly orientation)
   {
      this.orientation.set(orientation);
   }

   public void getAngularVelocity(Vector3DBasics angularVelocityToPack)
   {
      angularVelocityToPack.set(angularVelocity);
   }

   public void setAngularVelocity(Vector3DReadOnly angularVelocity)
   {
      this.angularVelocity.set(angularVelocity);
   }

   @Override
   public boolean epsilonEquals(SO3TrajectoryPointMessage other, double epsilon)
   {
      if (orientation == null && other.orientation != null)
         return false;
      if (orientation != null && other.orientation == null)
         return false;

      if (angularVelocity == null && other.angularVelocity != null)
         return false;
      if (angularVelocity != null && other.angularVelocity == null)
         return false;

      if (!MathTools.epsilonCompare(time, other.time, epsilon))
         return false;
      if (!orientation.epsilonEquals(other.orientation, epsilon))
         return false;
      if (!angularVelocity.epsilonEquals(other.angularVelocity, epsilon))
         return false;

      return true;
   }

   @Override
   public void applyTransform(Transform transform)
   {
      transform.transform(orientation);
      transform.transform(angularVelocity);
   }

   @Override
   public void applyInverseTransform(Transform transform)
   {
      transform.inverseTransform(orientation);
      transform.inverseTransform(angularVelocity);
   }

   @Override
   public String toString()
   {
      NumberFormat doubleFormat = new DecimalFormat(" 0.00;-0.00");
      String qxToString = doubleFormat.format(orientation.getX());
      String qyToString = doubleFormat.format(orientation.getY());
      String qzToString = doubleFormat.format(orientation.getZ());
      String qsToString = doubleFormat.format(orientation.getS());
      String wxToString = doubleFormat.format(angularVelocity.getX());
      String wyToString = doubleFormat.format(angularVelocity.getY());
      String wzToString = doubleFormat.format(angularVelocity.getZ());

      String timeToString = "time = " + doubleFormat.format(time);
      String orientationToString = "orientation = (" + qxToString + ", " + qyToString + ", " + qzToString + ", " + qsToString + ")";
      String angularVelocityToString = "angular velocity = (" + wxToString + ", " + wyToString + ", " + wzToString + ")";

      return "SO3 trajectory point: (" + timeToString + ", " + orientationToString + ", " + angularVelocityToString + ")";
   }
}
