package us.ihmc.robotics.math.frames;

import us.ihmc.euclid.referenceFrame.FrameGeometryObject;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple2D.interfaces.Vector2DBasics;
import us.ihmc.robotics.math.YoVector2D;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoDouble;

public class YoFrameVector2D extends FrameGeometryObject<YoFrameVector2D, YoVector2D> implements Vector2DBasics
{
   /** YoVector used to perform the operations. */
   private final YoVector2D vector;

   /**
    * Creates a new yo frame vector, initializes its coordinates to zero and its reference frame to
    * {@link ReferenceFrame#getWorldFrame()}, and registers variables to {@code registry}.
    *
    * @param namePrefix a unique name string to use as the prefix for child variable names.
    * @param registry the registry to register child variables to.
    */
   public YoFrameVector2D(String namePrefix, YoVariableRegistry registry)
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
   public YoFrameVector2D(String namePrefix, String nameSuffix, YoVariableRegistry registry)
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
   public YoFrameVector2D(ReferenceFrame referenceFrame, String namePrefix, YoVariableRegistry registry)
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
   public YoFrameVector2D(ReferenceFrame referenceFrame, String namePrefix, String nameSuffix, YoVariableRegistry
         registry)
   {
      this(referenceFrame, new YoVector2D(namePrefix, nameSuffix, registry));
   }
   
   /**
    * Creates a new yo frame vector using the given yo variables and sets its reference frame to
    * {@link ReferenceFrame#getWorldFrame()}.
    *
    * @param xVariable an existing variable representing the x value of this yo frame vector.
    * @param yVariable an existing variable representing the y value of this yo frame vector.
    */
   public YoFrameVector2D(YoDouble xVariable, YoDouble yVariable)
   {
      this(ReferenceFrame.getWorldFrame(), xVariable, yVariable);
   }

   /**
    * Creates a new yo frame vector using the given yo variables and sets its reference frame to
    * {@code referenceFrame}.
    *
    * @param referenceFrame the reference frame for this yo frame vector.
    * @param xVariable an existing variable representing the x value of this yo frame vector.
    * @param yVariable an existing variable representing the y value of this yo frame vector.
    */
   public YoFrameVector2D(ReferenceFrame referenceFrame, YoDouble xVariable, YoDouble yVariable)
   {
      this(referenceFrame, new YoVector2D(xVariable, yVariable));
   }

   /**
    * Creates a new yo frame vector using the given yo vector and sets its reference frame to
    * {@code referenceFrame}.
    *
    * @param referenceFrame the reference frame for this yo frame vector.
    * @param yoVector2D an existing yo vector.
    */
   public YoFrameVector2D(ReferenceFrame referenceFrame, YoVector2D yoVector2D)
   {
      super(referenceFrame, yoVector2D);
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
}
