package us.ihmc.robotics.math.frames;

import us.ihmc.euclid.referenceFrame.FrameGeometryObject;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple3D.interfaces.Point3DBasics;
import us.ihmc.robotics.math.YoPoint3D;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoDouble;

public class YoFramePoint3D extends FrameGeometryObject<YoFramePoint3D, YoPoint3D> implements Point3DBasics
{
   /** YoPoint used to perform the operations. */
   private final YoPoint3D point;

   /**
    * Creates a new yo frame point, initializes its coordinates to zero and its reference frame to
    * {@link ReferenceFrame#getWorldFrame()}, and registers variables to {@code registry}.
    *
    * @param namePrefix a unique name string to use as the prefix for child variable names.
    * @param registry the registry to register child variables to.
    */
   public YoFramePoint3D(String namePrefix, YoVariableRegistry registry)
   {
      this(namePrefix, "", registry);
   }

   /**
    * Creates a new yo frame point, initializes its coordinates to zero and its reference frame to
    * {@link ReferenceFrame#getWorldFrame()}, and registers variables to {@code registry}.
    *
    * @param namePrefix a unique name string to use as the prefix for child variable names.
    * @param nameSuffix a string to use as the suffix for child variable names.
    * @param registry the registry to register child variables to.
    */
   public YoFramePoint3D(String namePrefix, String nameSuffix, YoVariableRegistry registry)
   {
      this(ReferenceFrame.getWorldFrame(), namePrefix, nameSuffix, registry);
   }

   /**
    * Creates a new yo frame point, initializes its coordinates to zero and its reference frame to
    * {@code referenceFrame}, and registers variables to {@code registry}.
    *
    * @param referenceFrame the reference frame for this yo frame point.
    * @param namePrefix a unique name string to use as the prefix for child variable names.
    * @param registry the registry to register child variables to.
    */
   public YoFramePoint3D(ReferenceFrame referenceFrame, String namePrefix, YoVariableRegistry registry)
   {
      this(referenceFrame, namePrefix, "", registry);
   }

   /**
    * Creates a new yo frame point, initializes its coordinates to zero and its reference frame to
    * {@code referenceFrame}, and registers variables to {@code registry}.
    *
    * @param referenceFrame the reference frame for this yo frame point.
    * @param namePrefix a unique name string to use as the prefix for child variable names.
    * @param nameSuffix a string to use as the suffix for child variable names.
    * @param registry the registry to register child variables to.
    */
   public YoFramePoint3D(ReferenceFrame referenceFrame, String namePrefix, String nameSuffix, YoVariableRegistry
         registry)
   {
      this(referenceFrame, new YoPoint3D(namePrefix, nameSuffix, registry));
   }

   /**
    * Creates a new yo frame point using the given yo variables and sets its reference frame to
    * {@link ReferenceFrame#getWorldFrame()}.
    *
    * @param xVariable an existing variable representing the x value of this yo frame point.
    * @param yVariable an existing variable representing the y value of this yo frame point.
    * @param zVariable an existing variable representing the z value of this yo frame point.
    */
   public YoFramePoint3D(YoDouble xVariable, YoDouble yVariable, YoDouble zVariable)
   {
      this(ReferenceFrame.getWorldFrame(), xVariable, yVariable, zVariable);
   }

   /**
    * Creates a new yo frame point using the given yo variables and sets its reference frame to
    * {@code referenceFrame}.
    *
    * @param referenceFrame the reference frame for this yo frame point.
    * @param xVariable an existing variable representing the x value of this yo frame point.
    * @param yVariable an existing variable representing the y value of this yo frame point.
    * @param zVariable an existing variable representing the z value of this yo frame point.
    */
   public YoFramePoint3D(ReferenceFrame referenceFrame, YoDouble xVariable, YoDouble yVariable, YoDouble zVariable)
   {
      this(referenceFrame, new YoPoint3D(xVariable, yVariable, zVariable));
   }

   /**
    * Creates a new yo frame point using the given yo point and sets its reference frame to
    * {@code referenceFrame}.
    *
    * @param referenceFrame the reference frame for this yo frame point.
    * @param yoPoint3D an existing yo point.
    */
   public YoFramePoint3D(ReferenceFrame referenceFrame, YoPoint3D yoPoint3D)
   {
      super(referenceFrame, yoPoint3D);
      point = getGeometryObject();
   }
   
   /** {@inheritDoc} */
   @Override
   public void setX(double x)
   {
      point.setX(x);
   }

   /** {@inheritDoc} */
   @Override
   public void setY(double y)
   {
      point.setY(y);
   }

   /** {@inheritDoc} */
   @Override
   public void setZ(double z)
   {
      point.setZ(z);
   }

   /** {@inheritDoc} */
   @Override
   public double getX()
   {
      return point.getX();
   }

   /** {@inheritDoc} */
   @Override
   public double getY()
   {
      return point.getY();
   }

   /** {@inheritDoc} */
   @Override
   public double getZ()
   {
      return point.getZ();
   }
}
