package us.ihmc.robotics.math.frames;

import us.ihmc.euclid.axisAngle.AxisAngle;
import us.ihmc.euclid.interfaces.Clearable;
import us.ihmc.euclid.interfaces.EpsilonComparable;
import us.ihmc.euclid.interfaces.Transformable;
import us.ihmc.euclid.matrix.RotationMatrix;
import us.ihmc.euclid.referenceFrame.FrameGeometryObject;
import us.ihmc.euclid.referenceFrame.FrameQuaternion;
import us.ihmc.euclid.referenceFrame.FrameVector3D;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.referenceFrame.exceptions.ReferenceFrameMismatchException;
import us.ihmc.euclid.referenceFrame.interfaces.FrameQuaternionReadOnly;
import us.ihmc.euclid.referenceFrame.interfaces.FrameVector3DReadOnly;
import us.ihmc.euclid.tuple4D.interfaces.QuaternionBasics;
import us.ihmc.yoVariables.listener.VariableChangedListener;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoDouble;

// Note: You should only make these once at the initialization of a controller. You shouldn't make
// any on the fly since they contain YoVariables.
public class YoFrameQuaternion extends FrameGeometryObject<YoFrameQuaternion, YoQuaternion> implements FrameQuaternionReadOnly, QuaternionBasics, Transformable, Clearable, EpsilonComparable<YoFrameQuaternion>
{
   private final YoQuaternion quaternion;

   public YoFrameQuaternion(String namePrefix, ReferenceFrame referenceFrame, YoVariableRegistry registry)
   {
      this(namePrefix, "", referenceFrame, registry);
   }

   public YoFrameQuaternion(String namePrefix, String nameSuffix, ReferenceFrame referenceFrame, YoVariableRegistry registry)
   {
      super(referenceFrame, new YoQuaternion(namePrefix, nameSuffix, registry));
      
      this.quaternion = getGeometryObject();
   }

   public YoFrameQuaternion(YoDouble qx, YoDouble qy, YoDouble qz, YoDouble qs, ReferenceFrame referenceFrame)
   {
      super(referenceFrame, new YoQuaternion(qx, qy, qz, qs));
      
      this.quaternion = getGeometryObject();
   }

   public void set(FrameQuaternionReadOnly other)
   {
      set(other, true);
   }

   public void set(FrameQuaternionReadOnly frameQuaternion, boolean notifyListeners)
   {
      checkReferenceFrameMatch(frameQuaternion);
      quaternion.set(frameQuaternion, notifyListeners);
   }

   public void setIncludingFrame(FrameQuaternionReadOnly frameQuaternion)
   {
      setIncludingFrame(frameQuaternion, true);
   }

   public void setIncludingFrame(FrameQuaternionReadOnly frameQuaternion, boolean notifyListeners)
   {
      setToZero(frameQuaternion.getReferenceFrame());
      quaternion.set(frameQuaternion, notifyListeners);
   }

   @Override
   public void setUnsafe(double qx, double qy, double qz, double qs)
   {
      setUnsafe(qx, qy, qz, qs, true);
   }

   public void setUnsafe(double qx, double qy, double qz, double qs, boolean notifyListeners)
   {
      quaternion.setUnsafe(qx, qy, qz, qs, notifyListeners);
   }

   @Override
   public double getX()
   {
      return quaternion.getX();
   }

   @Override
   public double getY()
   {
      return quaternion.getY();
   }

   @Override
   public double getZ()
   {
      return quaternion.getZ();
   }

   @Override
   public double getS()
   {
      return quaternion.getS();
   }

   public void get(YoFrameQuaternion yoFrameQuaternionToPack)
   {
      yoFrameQuaternionToPack.set(this);
   }

   public void getIncludingFrame(YoFrameQuaternion yoFrameQuaternionToPack)
   {
      yoFrameQuaternionToPack.setIncludingFrame(this);
   }

   public void get(RotationMatrix matrixToPack)
   {
      matrixToPack.set(this);
   }

   public void get(AxisAngle axisAngleToPack)
   {
      axisAngleToPack.set(this);
   }

   public void get(QuaternionBasics quaternionToPack)
   {
      quaternionToPack.set(quaternion);
   }

   public void get(FrameQuaternion quaternionToPack)
   {
      quaternionToPack.set(quaternion);
   }

   public void getIncludingFrame(FrameQuaternion quaternionToPack)
   {
      quaternionToPack.setIncludingFrame(getReferenceFrame(), quaternion);
   }

   /**
    * Computes and packs the orientation described by this {@code YoFrameQuaternion} as a rotation
    * vector.
    * <p>
    * WARNING: a rotation vector is different from a yaw-pitch-roll or Euler angles representation.
    * A rotation vector is equivalent to the axis of an axis-angle that is multiplied by the angle
    * of the same axis-angle.
    * </p>
    *
    * @param frameRotationVectorToPack the vector in which the rotation vector and the reference
    *           frame it is expressed in are stored. Modified.
    * @throws ReferenceFrameMismatchException if the argument is not expressed in
    *            {@code this.referenceFrame}.
    */
   public void getRotationVector(FrameVector3D frameRotationVectorToPack)
   {
      checkReferenceFrameMatch(frameRotationVectorToPack);
      quaternion.get(frameRotationVectorToPack);
   }
   
   public void setRotationVector(FrameVector3DReadOnly rotationVector)
   {
      checkReferenceFrameMatch(rotationVector);
      quaternion.set(rotationVector);
   }
   
   public void setRotationVector(YoFrameVector rotationVector)
   {
      checkReferenceFrameMatch(rotationVector);
      quaternion.set(rotationVector.getFrameTuple());
   }

   public YoDouble getYoQx()
   {
      return quaternion.getYoQx();
   }

   public YoDouble getYoQy()
   {
      return quaternion.getYoQy();
   }

   public YoDouble getYoQz()
   {
      return quaternion.getYoQz();
   }

   public YoDouble getYoQs()
   {
      return quaternion.getYoQs();
   }

   public void checkQuaternionIsUnitMagnitude()
   {
      checkIfUnitary();
   }

   public void attachVariableChangedListener(VariableChangedListener variableChangedListener)
   {
      quaternion.addVariableChangedListener(variableChangedListener);
   }

   public String getNamePrefix()
   {
      return quaternion.getNamePrefix();
   }

   public String getNameSuffix()
   {
      return quaternion.getNameSuffix();
   }

   @Override
   public String toString()
   {
      return quaternion.toString() + "-" + getReferenceFrame();
   }
}
