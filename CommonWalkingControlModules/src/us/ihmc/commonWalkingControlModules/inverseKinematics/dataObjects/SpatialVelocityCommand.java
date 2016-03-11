package us.ihmc.commonWalkingControlModules.inverseKinematics.dataObjects;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import us.ihmc.robotics.geometry.FrameVector;
import us.ihmc.robotics.referenceFrames.ReferenceFrame;
import us.ihmc.robotics.screwTheory.RigidBody;
import us.ihmc.robotics.screwTheory.Twist;

public class SpatialVelocityCommand extends InverseKinematicsCommand<SpatialVelocityCommand>
{
   private boolean hasWeight;
   private double weight;
   private final Twist spatialVelocity = new Twist();
   private final DenseMatrix64F selectionMatrix = CommonOps.identity(Twist.SIZE);

   private RigidBody base;
   private RigidBody endEffector;

   private String baseName;
   private String endEffectorName;

   public SpatialVelocityCommand()
   {
      super(InverseKinematicsCommandType.TASKSPACE);
      removeWeight();
   }

   public void set(RigidBody base, RigidBody endEffector)
   {
      setBase(base);
      setEndEffector(endEffector);
   }

   public void setBase(RigidBody base)
   {
      this.base = base;
      baseName = base.getName();
   }

   public void setEndEffector(RigidBody endEffector)
   {
      this.endEffector = endEffector;
      endEffectorName = endEffector.getName();
   }

   public void set(Twist spatialVelocity)
   {
      this.spatialVelocity.set(spatialVelocity);
      setSelectionMatrixToIdentity();
   }

   public void set(Twist spatialVelocity, DenseMatrix64F selectionMatrix)
   {
      this.spatialVelocity.set(spatialVelocity);
      setSelectionMatrix(selectionMatrix);
   }

   public void setAngularVelocity(ReferenceFrame bodyFrame, ReferenceFrame baseFrame, FrameVector desiredAngularVelocity)
   {
      spatialVelocity.setToZero(bodyFrame, baseFrame, desiredAngularVelocity.getReferenceFrame());
      spatialVelocity.setAngularPart(desiredAngularVelocity.getVector());

      selectionMatrix.reshape(3, Twist.SIZE);
      selectionMatrix.set(0, 0, 1.0);
      selectionMatrix.set(1, 1, 1.0);
      selectionMatrix.set(2, 2, 1.0);
   }

   public void setLinearVelocity(ReferenceFrame bodyFrame, ReferenceFrame baseFrame, FrameVector desiredLinearVelocity)
   {
      spatialVelocity.setToZero(bodyFrame, baseFrame, desiredLinearVelocity.getReferenceFrame());
      spatialVelocity.setLinearPart(desiredLinearVelocity.getVector());
      spatialVelocity.changeFrame(bodyFrame);

      selectionMatrix.reshape(3, Twist.SIZE);
      selectionMatrix.set(0, 3, 1.0);
      selectionMatrix.set(1, 4, 1.0);
      selectionMatrix.set(2, 5, 1.0);
   }

   @Override
   public void set(SpatialVelocityCommand other)
   {
      hasWeight = other.hasWeight;
      weight = other.weight;

      spatialVelocity.set(other.getSpatialVelocity());
      selectionMatrix.set(other.getSelectionMatrix());
      base = other.getBase();
      endEffector = other.getEndEffector();
      baseName = other.baseName;
      endEffectorName = other.endEffectorName;
   }

   private void setSelectionMatrixToIdentity()
   {
      selectionMatrix.reshape(Twist.SIZE, Twist.SIZE);
      CommonOps.setIdentity(selectionMatrix);
   }

   public void setSelectionMatrix(DenseMatrix64F selectionMatrix)
   {
      if (selectionMatrix.getNumRows() > Twist.SIZE)
         throw new RuntimeException("Unexpected number of rows: " + selectionMatrix.getNumRows());
      if (selectionMatrix.getNumCols() != Twist.SIZE)
         throw new RuntimeException("Unexpected number of columns: " + selectionMatrix.getNumCols());

      this.selectionMatrix.set(selectionMatrix);
   }

   public boolean getHasWeight()
   {
      return hasWeight;
   }

   public double getWeight()
   {
      return weight;
   }

   public Twist getSpatialVelocity()
   {
      return spatialVelocity;
   }

   public DenseMatrix64F getSelectionMatrix()
   {
      return selectionMatrix;
   }

   public RigidBody getBase()
   {
      return base;
   }

   public String getBaseName()
   {
      return baseName;
   }

   public RigidBody getEndEffector()
   {
      return endEffector;
   }

   public String getEndEffectorName()
   {
      return endEffectorName;
   }

   public void setWeight(double weight)
   {
      this.weight = weight;
      hasWeight = weight != Double.POSITIVE_INFINITY;
   }

   public void removeWeight()
   {
      setWeight(Double.POSITIVE_INFINITY);
   }

   @Override
   public String toString()
   {
      String ret = getClass().getSimpleName() + ": base = " + base.getName() + "endEffector = " + endEffector.getName() + ", spatialAcceleration = " + spatialVelocity;
      return ret;
   }
}
