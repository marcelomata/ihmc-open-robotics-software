package us.ihmc.robotics.math.frames;

import us.ihmc.euclid.referenceFrame.FrameGeometryObject;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple3D.interfaces.Vector3DBasics;
import us.ihmc.robotics.math.YoVector3D;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoDouble;

public class YoFrameVector3D extends FrameGeometryObject<YoFrameVector3D, YoVector3D> implements Vector3DBasics
{
   private final YoVector3D vector;

   public YoFrameVector3D(String namePrefix, YoVariableRegistry registry)
   {
      this(namePrefix, "", registry);
   }

   public YoFrameVector3D(String namePrefix, String nameSuffix, YoVariableRegistry registry)
   {
      this(ReferenceFrame.getWorldFrame(), namePrefix, nameSuffix, registry);
   }

   public YoFrameVector3D(ReferenceFrame referenceFrame, String namePrefix, YoVariableRegistry registry)
   {
      this(referenceFrame, namePrefix, "", registry);
   }

   public YoFrameVector3D(ReferenceFrame referenceFrame, String namePrefix, String nameSuffix, YoVariableRegistry
         registry)
   {
      this(referenceFrame, new YoVector3D(namePrefix, nameSuffix, registry));
   }

   public YoFrameVector3D(YoDouble xVariable, YoDouble yVariable, YoDouble zVariable)
   {
      this(ReferenceFrame.getWorldFrame(), xVariable, yVariable, zVariable);
   }

   public YoFrameVector3D(ReferenceFrame referenceFrame, YoDouble xVariable, YoDouble yVariable, YoDouble zVariable)
   {
      this(referenceFrame, new YoVector3D(xVariable, yVariable, zVariable));
   }

   public YoFrameVector3D(ReferenceFrame referenceFrame, YoVector3D yoVector3D)
   {
      super(referenceFrame, yoVector3D);
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
   public void setZ(double z)
   {
      vector.setZ(z);
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

   @Override
   public double getZ()
   {
      return vector.getZ();
   }
}
