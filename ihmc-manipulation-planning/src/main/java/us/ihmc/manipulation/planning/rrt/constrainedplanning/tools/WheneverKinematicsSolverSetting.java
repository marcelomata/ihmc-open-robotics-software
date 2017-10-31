package us.ihmc.manipulation.planning.rrt.constrainedplanning.tools;

import us.ihmc.commonWalkingControlModules.momentumBasedController.optimization.ControllerCoreOptimizationSettings;
import us.ihmc.convexOptimization.quadraticProgram.JavaQuadProgSolver;
import us.ihmc.convexOptimization.quadraticProgram.SimpleActiveSetQPSolverInterface;
import us.ihmc.convexOptimization.quadraticProgram.SimpleEfficientActiveSetQPSolver;
import us.ihmc.euclid.tuple2D.Vector2D;

public class WheneverKinematicsSolverSetting implements ControllerCoreOptimizationSettings
{
   /** @inheritDoc */
   @Override
   public double getJointVelocityWeight()
   {
      //return 10.0;
      //return 3.0;
      return 0.00001;
   }

   /** @inheritDoc */
   @Override
   public double getJointAccelerationWeight()
   {
      //return 1.0;
      //return 0.5;
      return 0.0;
   }

   @Override
   public boolean areJointVelocityLimitsConsidered()
   {
      return false;
   }

   /** @inheritDoc */
   @Override
   public double getJointJerkWeight()
   {
      return 0;
   }

   /** @inheritDoc */
   @Override
   public double getRhoWeight()
   {
      return 0;
   }

   /** @inheritDoc */
   @Override
   public double getRhoMin()
   {
      return 0;
   }

   /** @inheritDoc */
   @Override
   public double getRhoRateDefaultWeight()
   {
      return 0;
   }

   /** @inheritDoc */
   @Override
   public double getRhoRateHighWeight()
   {
      return 0;
   }

   /** @inheritDoc */
   @Override
   public Vector2D getCoPWeight()
   {
      return null;
   }

   /** @inheritDoc */
   @Override
   public Vector2D getCoPRateDefaultWeight()
   {
      return null;
   }

   /** @inheritDoc */
   @Override
   public Vector2D getCoPRateHighWeight()
   {
      return null;
   }

   /** @inheritDoc */
   @Override
   public int getNumberOfBasisVectorsPerContactPoint()
   {
      return 0;
   }

   /** @inheritDoc */
   @Override
   public int getNumberOfContactPointsPerContactableBody()
   {
      return 0;
   }

   /** @inheritDoc */
   @Override
   public int getNumberOfContactableBodies()
   {
      return 0;
   }

   /** @inheritDoc */
   @Override
   public int getRhoSize()
   {
      return 0;
   }

   @Override
   public SimpleActiveSetQPSolverInterface getActiveSetQPSolver()
   {
      return new JavaQuadProgSolver();
   }
}