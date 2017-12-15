package us.ihmc.robotics.math.frames;

import us.ihmc.euclid.referenceFrame.FrameGeometryObject;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple2D.interfaces.Point2DBasics;
import us.ihmc.robotics.math.YoPoint2D;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoDouble;

public class YoFramePoint2D extends FrameGeometryObject<YoFramePoint2D, YoPoint2D> implements Point2DBasics
{
   /** YoPoint used to perform the operations. */
   private final YoPoint2D point;

   /**
    * Creates a new yo frame point, initializes its coordinates to zero and its reference frame to
    * {@link ReferenceFrame#getWorldFrame()}, and registers variables to {@code registry}.
    * 
    * @param namePrefix a unique name string to use as the prefix for child variable names.
    * @param registry the registry to register child variables to.
    */
   public YoFramePoint2D(String namePrefix, YoVariableRegistry registry)
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
   public YoFramePoint2D(String namePrefix, String nameSuffix, YoVariableRegistry registry)
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
   public YoFramePoint2D(ReferenceFrame referenceFrame, String namePrefix, YoVariableRegistry registry)
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
   public YoFramePoint2D(ReferenceFrame referenceFrame, String namePrefix, String nameSuffix, YoVariableRegistry 
         registry)
   {
      this(referenceFrame, new YoPoint2D(namePrefix, nameSuffix, registry));
   }

   /**
    * Creates a new yo frame point using the given yo variables and sets its reference frame to
    * {@link ReferenceFrame#getWorldFrame()}.
    *
    * @param xVariable an existing variable representing the x value of this yo frame point.
    * @param yVariable an existing variable representing the y value of this yo frame point.
    */
   public YoFramePoint2D(YoDouble xVariable, YoDouble yVariable)
   {
      this(ReferenceFrame.getWorldFrame(), xVariable, yVariable);
   }
   
   /**
    * Creates a new yo frame point using the given yo variables and sets its reference frame to
    * {@code referenceFrame}.
    *
    * @param referenceFrame the reference frame for this yo frame point.
    * @param xVariable an existing variable representing the x value of this yo frame point.
    * @param yVariable an existing variable representing the y value of this yo frame point.
    */
   public YoFramePoint2D(ReferenceFrame referenceFrame, YoDouble xVariable, YoDouble yVariable)
   {
      this(referenceFrame, new YoPoint2D(xVariable, yVariable));
   }

   /**
    * Creates a new yo frame point using the given yo point and sets its reference frame to
    * {@code referenceFrame}.
    *
    * @param referenceFrame the reference frame for this yo frame point.
    * @param yoPoint2D an existing yo point.
    */
   public YoFramePoint2D(ReferenceFrame referenceFrame, YoPoint2D yoPoint2D)
   {
      super(referenceFrame, yoPoint2D);
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
}
