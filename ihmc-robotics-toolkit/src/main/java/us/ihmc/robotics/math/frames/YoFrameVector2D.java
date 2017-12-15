package us.ihmc.robotics.math.frames;

import us.ihmc.euclid.referenceFrame.FrameGeometryObject;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple2D.interfaces.Vector2DBasics;
import us.ihmc.robotics.math.YoVector2D;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoDouble;

public class YoFrameVector2D extends FrameGeometryObject<YoFrameVector2D, YoVector2D> implements Vector2DBasics
{
   private final YoVector2D vector;

   public YoFrameVector2D(String namePrefix, YoVariableRegistry registry)
   {
      this(namePrefix, "", registry);
   }

   public YoFrameVector2D(String namePrefix, String nameSuffix, YoVariableRegistry registry)
   {
      this(ReferenceFrame.getWorldFrame(), namePrefix, nameSuffix, registry);
   }

   public YoFrameVector2D(ReferenceFrame referenceFrame, String namePrefix, YoVariableRegistry registry)
   {
      this(referenceFrame, namePrefix, "", registry);
   }

   public YoFrameVector2D(ReferenceFrame referenceFrame, String namePrefix, String nameSuffix, YoVariableRegistry
         registry)
   {
      this(referenceFrame, new YoVector2D(namePrefix, nameSuffix, registry));
   }

   public YoFrameVector2D(YoDouble xVariable, YoDouble yVariable)
   {
      this(ReferenceFrame.getWorldFrame(), xVariable, yVariable);
   }

   public YoFrameVector2D(ReferenceFrame referenceFrame, YoDouble xVariable, YoDouble yVariable)
   {
      this(referenceFrame, new YoVector2D(xVariable, yVariable));
   }

   public YoFrameVector2D(ReferenceFrame referenceFrame, YoVector2D yoVector2D)
   {
      super(referenceFrame, yoVector2D);
      vector = getGeometryObject();
   }
   
   @Override
   public void setX(double x)
   {
      vector.setX(x);
   }

   @Override
   public void setY(double y)
   {
      vector.setY(y);
   }

   @Override
   public double getX()
   {
      return vector.getX();
   }

   @Override
   public double getY()
   {
      return vector.getY();
   }
}
