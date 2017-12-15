package us.ihmc.robotics.math.frames;

import us.ihmc.euclid.referenceFrame.FrameGeometryObject;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple2D.interfaces.Point2DBasics;
import us.ihmc.robotics.math.YoPoint2D;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoDouble;

public class YoFramePoint2D extends FrameGeometryObject<YoFramePoint2D, YoPoint2D> implements Point2DBasics
{
   private final YoPoint2D point;

   public YoFramePoint2D(String namePrefix, YoVariableRegistry registry)
   {
      this(namePrefix, "", registry);
   }

   public YoFramePoint2D(String namePrefix, String nameSuffix, YoVariableRegistry registry)
   {
      this(ReferenceFrame.getWorldFrame(), namePrefix, nameSuffix, registry);
   }
   
   public YoFramePoint2D(ReferenceFrame referenceFrame, String namePrefix, YoVariableRegistry registry)
   {
      this(referenceFrame, namePrefix, "", registry);
   }
   
   public YoFramePoint2D(ReferenceFrame referenceFrame, String namePrefix, String nameSuffix, YoVariableRegistry 
         registry)
   {
      this(referenceFrame, new YoPoint2D(namePrefix, nameSuffix, registry));
   }

   public YoFramePoint2D(YoDouble xVariable, YoDouble yVariable)
   {
      this(ReferenceFrame.getWorldFrame(), xVariable, yVariable);
   }

   public YoFramePoint2D(ReferenceFrame referenceFrame, YoDouble xVariable, YoDouble yVariable)
   {
      this(referenceFrame, new YoPoint2D(xVariable, yVariable));
   }
   
   public YoFramePoint2D(ReferenceFrame referenceFrame, YoPoint2D yoPoint2D)
   {
      super(referenceFrame, yoPoint2D);
      point = getGeometryObject();
   }

   @Override
   public void setX(double x)
   {
      point.setX(x);
   }

   @Override
   public void setY(double y)
   {
      point.setY(y);
   }

   @Override
   public double getX()
   {
      return point.getX();
   }

   @Override
   public double getY()
   {
      return point.getY();
   }
}
