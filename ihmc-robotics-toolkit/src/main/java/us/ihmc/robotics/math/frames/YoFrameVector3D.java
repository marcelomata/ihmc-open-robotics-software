package us.ihmc.robotics.math.frames;

import us.ihmc.euclid.referenceFrame.FrameGeometryObject;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple3D.interfaces.Vector3DBasics;
import us.ihmc.robotics.math.YoVector3D;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoDouble;

public class YoFrameVector3D extends FrameGeometryObject<YoFrameVector3D, YoVector3D> implements Vector3DBasics
{
   /** YoVector used to perform the operations. */
   private final YoVector3D vector;

   /**
    * Creates a new yo frame vector, initializes its coordinates to zero and its reference frame to
    * {@link ReferenceFrame#getWorldFrame()}, and registers variables to {@code registry}.
    *
    * @param namePrefix a unique name string to use as the prefix for child variable names.
    * @param registry the registry to register child variables to.
    */
   public YoFrameVector3D(String namePrefix, YoVariableRegistry registry)
   {
      this(namePrefix, "", registry);
   }

   /**
    * Creates a new yo frame vector, initializes its coordinates to zero and its reference frame to
    * {@link ReferenceFrame#getWorldFrame()}, and registers variables to {@code registry}.
    *
    * @param namePrefix a unique name string to use as the prefix for child variable names.
    * @param nameSuffix a string to use as the suffix for child variable names.
    * @param registry the registry to register child variables to.
    */
   public YoFrameVector3D(String namePrefix, String nameSuffix, YoVariableRegistry registry)
   {
      this(ReferenceFrame.getWorldFrame(), namePrefix, nameSuffix, registry);
   }

   /**
    * Creates a new yo frame vector, initializes its coordinates to zero and its reference frame to
    * {@code referenceFrame}, and registers variables to {@code registry}.
    *
    * @param referenceFrame the reference frame for this yo frame vector.
    * @param namePrefix a unique name string to use as the prefix for child variable names.
    * @param registry the registry to register child variables to.
    */
   public YoFrameVector3D(ReferenceFrame referenceFrame, String namePrefix, YoVariableRegistry registry)
   {
      this(referenceFrame, namePrefix, "", registry);
   }

   /**
    * Creates a new yo frame vector using the given yo variables and sets its reference frame to
    * {@code referenceFrame}.
    *
    * @param referenceFrame the reference frame for this yo frame vector.
    * @param namePrefix a unique name string to use as the prefix for child variable names.
    * @param nameSuffix a string to use as the suffix for child variable names.
    * @param registry the registry to register child variables to.
    */
   public YoFrameVector3D(ReferenceFrame referenceFrame, String namePrefix, String nameSuffix, YoVariableRegistry
         registry)
   {
      this(referenceFrame, new YoVector3D(namePrefix, nameSuffix, registry));
   }

   /**
    * Creates a new yo frame vector using the given yo variables and sets its reference frame to
    * {@link ReferenceFrame#getWorldFrame()}.
    *
    * @param xVariable an existing variable representing the x value of this yo frame vector.
    * @param yVariable an existing variable representing the y value of this yo frame vector.
    * @param zVariable an existing variable representing the z value of this yo frame vector.
    */
   public YoFrameVector3D(YoDouble xVariable, YoDouble yVariable, YoDouble zVariable)
   {
      this(ReferenceFrame.getWorldFrame(), xVariable, yVariable, zVariable);
   }

   /**
    * Creates a new yo frame vector using the given yo variables and sets its reference frame to
    * {@code referenceFrame}.
    *
    * @param referenceFrame the reference frame for this yo frame vector.
    * @param xVariable an existing variable representing the x value of this yo frame vector.
    * @param yVariable an existing variable representing the y value of this yo frame vector.
    * @param zVariable an existing variable representing the z value of this yo frame vector.
    */
   public YoFrameVector3D(ReferenceFrame referenceFrame, YoDouble xVariable, YoDouble yVariable, YoDouble zVariable)
   {
      this(referenceFrame, new YoVector3D(xVariable, yVariable, zVariable));
   }

   /**
    * Creates a new yo frame vector using the given yo vector and sets its reference frame to
    * {@code referenceFrame}.
    *
    * @param referenceFrame the reference frame for this yo frame vector.
    * @param yoVector3D an existing yo vector.
    */
   public YoFrameVector3D(ReferenceFrame referenceFrame, YoVector3D yoVector3D)
   {
      super(referenceFrame, yoVector3D);
      vector = getGeometryObject();
   }

   /** {@inheritDoc} */
   @Override
   public void setX(double x)
   {
      vector.setX(x);
   }

   /** {@inheritDoc} */
   @Override
   public void setY(double y)
   {
      vector.setY(y);
   }

   /** {@inheritDoc} */
   @Override
   public void setZ(double z)
   {
      vector.setZ(z);
   }

   /** {@inheritDoc} */
   @Override
   public double getX()
   {
      return vector.getX();
   }

   /** {@inheritDoc} */
   @Override
   public double getY()
   {
      return vector.getY();
   }

   /** {@inheritDoc} */
   @Override
   public double getZ()
   {
      return vector.getZ();
   }
}
