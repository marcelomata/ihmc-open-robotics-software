package us.ihmc.robotics.math.frames;

import org.apache.commons.lang3.StringUtils;
import us.ihmc.euclid.interfaces.GeometryObject;
import us.ihmc.euclid.tools.EuclidCoreIOTools;
import us.ihmc.euclid.tuple4D.interfaces.QuaternionBasics;
import us.ihmc.euclid.tuple4D.interfaces.QuaternionReadOnly;
import us.ihmc.yoVariables.listener.VariableChangedListener;
import us.ihmc.yoVariables.registry.YoVariableRegistry;
import us.ihmc.yoVariables.variable.YoDouble;

import static us.ihmc.robotics.math.frames.YoFrameVariableNameTools.*;

public class YoQuaternion implements QuaternionBasics, GeometryObject<YoQuaternion>
{
   private final String namePrefix;
   private final String nameSuffix;
   
   private final YoDouble qx, qy, qz, qs;
   
   public YoQuaternion(String namePrefix, YoVariableRegistry registry)
   {
      this(namePrefix, "", registry);
   }
   
   public YoQuaternion(String namePrefix, String nameSuffix, YoVariableRegistry registry)
   {
      this.namePrefix = namePrefix;
      this.nameSuffix = nameSuffix;
      
      qx = new YoDouble(createQxName(namePrefix, nameSuffix), registry);
      qy = new YoDouble(createQyName(namePrefix, nameSuffix), registry);
      qz = new YoDouble(createQzName(namePrefix, nameSuffix), registry);
      qs = new YoDouble(createQsName(namePrefix, nameSuffix), registry);
      
      qs.set(1.0);
   }
   
   public YoQuaternion(YoDouble qx, YoDouble qy, YoDouble qz, YoDouble qs)
   {
      this.namePrefix = StringUtils.getCommonPrefix(qx.getName(), qy.getName(), qz.getName(), qs.getName());
      this.nameSuffix = YoFrameVariableNameTools.getCommonSuffix(qx.getName(), qy.getName(), qz.getName(), qs.getName());
      
      this.qx = qx;
      this.qy = qy;
      this.qz = qz;
      this.qs = qs;
   }

   @Override
   public void set(YoQuaternion other)
   {
      set(other, true);
   }

   public void set(QuaternionReadOnly quaternion, boolean notifyListeners)
   {
      setUnsafe(quaternion.getX(), quaternion.getY(), quaternion.getZ(), quaternion.getS(), notifyListeners);
   }
   
   @Override
   public void setUnsafe(double qx, double qy, double qz, double qs)
   {
      setUnsafe(qx, qy, qz, qs, true);
   }
   
   public void setUnsafe(double qx, double qy, double qz, double qs, boolean notifyListeners)
   {
      this.qx.set(qx, notifyListeners);
      this.qy.set(qy, notifyListeners);
      this.qz.set(qz, notifyListeners);
      this.qs.set(qs, notifyListeners);
   }
   
   public YoDouble getYoQx()
   {
      return qx;
   }
   
   public YoDouble getYoQy()
   {
      return qy;
   }
   
   public YoDouble getYoQz()
   {
      return qz;
   }
   
   public YoDouble getYoQs()
   {
      return qs;
   }

   @Override
   public double getX()
   {
      return qx.getDoubleValue();
   }

   @Override
   public double getY()
   {
      return qy.getDoubleValue();
   }

   @Override
   public double getZ()
   {
      return qz.getDoubleValue();
   }

   @Override
   public double getS()
   {
      return qs.getDoubleValue();
   }
   
   public void addVariableChangedListener(VariableChangedListener variableChangedListener)
   {
      qx.addVariableChangedListener(variableChangedListener);
      qy.addVariableChangedListener(variableChangedListener);
      qz.addVariableChangedListener(variableChangedListener);
      qs.addVariableChangedListener(variableChangedListener);
   }
   
   public String getNamePrefix()
   {
      return namePrefix;
   }
   
   public String getNameSuffix()
   {
      return nameSuffix;
   }
   
   @Override
   public String toString()
   {
      return EuclidCoreIOTools.getTuple4DString(this);
   }

   @Override
   public boolean epsilonEquals(YoQuaternion other, double epsilon)
   {
      return QuaternionBasics.super.epsilonEquals(other, epsilon);
   }

   @Override
   public boolean geometricallyEquals(YoQuaternion other, double epsilon)
   {
      return QuaternionBasics.super.geometricallyEquals(other, epsilon);
   }
}
