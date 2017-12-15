package us.ihmc.robotics.math.frames;

import us.ihmc.euclid.referenceFrame.FrameGeometryObject;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple3D.interfaces.Point3DBasics;
import us.ihmc.robotics.math.YoPoint3D;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoDouble;

public class YoFramePoint3D extends FrameGeometryObject<YoFramePoint3D, YoPoint3D> implements Point3DBasics
{
   private final YoPoint3D point;

   public YoFramePoint3D(String namePrefix, YoVariableRegistry registry)
   {
      this(namePrefix, "", registry);
   }

   public YoFramePoint3D(String namePrefix, String nameSuffix, YoVariableRegistry registry)
   {
      this(ReferenceFrame.getWorldFrame(), namePrefix, nameSuffix, registry);
   }

   public YoFramePoint3D(ReferenceFrame referenceFrame, String namePrefix, YoVariableRegistry registry)
   {
      this(referenceFrame, namePrefix, "", registry);
   }

   public YoFramePoint3D(ReferenceFrame referenceFrame, String namePrefix, String nameSuffix, YoVariableRegistry
         registry)
   {
      this(referenceFrame, new YoPoint3D(namePrefix, nameSuffix, registry));
   }

   public YoFramePoint3D(YoDouble xVariable, YoDouble yVariable, YoDouble zVariable)
   {
      this(ReferenceFrame.getWorldFrame(), xVariable, yVariable, zVariable);
   }

   public YoFramePoint3D(ReferenceFrame referenceFrame, YoDouble xVariable, YoDouble yVariable, YoDouble zVariable)
   {
      this(referenceFrame, new YoPoint3D(xVariable, yVariable, zVariable));
   }

   public YoFramePoint3D(ReferenceFrame referenceFrame, YoPoint3D yoPoint3D)
   {
      super(referenceFrame, yoPoint3D);
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
   public void setZ(double z)
   {
      point.setZ(z);
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

   @Override
   public double getZ()
   {
      return point.getZ();
   }
}
