package us.ihmc.simulationconstructionset.physics.collision.simple;

import java.util.ArrayList;

import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

import us.ihmc.convexOptimization.quadraticProgram.OASESConstrainedQPSolver;
import us.ihmc.robotics.geometry.RigidBodyTransform;
import us.ihmc.simulationconstructionset.Link;
import us.ihmc.simulationconstructionset.physics.CollisionShape;
import us.ihmc.simulationconstructionset.physics.CollisionShapeDescription;
import us.ihmc.simulationconstructionset.physics.CollisionShapeFactory;
import us.ihmc.simulationconstructionset.physics.ScsCollisionDetector;
import us.ihmc.simulationconstructionset.physics.collision.CollisionDetectionResult;
import us.ihmc.tools.exceptions.NoConvergenceException;

public class SimpleCollisionDetector implements ScsCollisionDetector
{
// private final SimpleActiveSetQPStandaloneSolver solver = new SimpleActiveSetQPStandaloneSolver();
// private final QuadProgSolver solver = new QuadProgSolver(16, 2, 16);
 private final OASESConstrainedQPSolver solver = new OASESConstrainedQPSolver(null);
//   private final OJAlgoConstrainedQPSolver solver = new OJAlgoConstrainedQPSolver();

//   private final SimpleEfficientActiveSetQPSolver solver = new SimpleEfficientActiveSetQPSolver();
   
   private final ArrayList<CollisionShape> collisionObjects = new ArrayList<CollisionShape>();

   // Temporary variables for computation:
   private final RigidBodyTransform transformOne = new RigidBodyTransform();
   private final RigidBodyTransform transformTwo = new RigidBodyTransform();
   private final Point3d centerOne = new Point3d();
   private final Point3d centerTwo = new Point3d();
   private final Vector3d centerToCenterVector = new Vector3d();

   private final Vector3d normalVector = new Vector3d();
   private final Vector3d tempVector = new Vector3d();

   private double boxToBoxMargin = 0.002;

   public double getBoxToBoxMargin()
   {
      return boxToBoxMargin;
   }

   public void setBoxToBoxMargin(double boxToBoxMargin)
   {
      this.boxToBoxMargin = boxToBoxMargin;
   }

   @Override
   public void initialize()
   {
   }

   @Override
   public CollisionShapeFactory getShapeFactory()
   {
      return new SimpleCollisionShapeFactory(this);
   }

   @Override
   public void removeShape(Link link)
   {
   }

   @Override
   public CollisionShape lookupCollisionShape(Link link)
   {
      return null;
   }

   @Override
   public void performCollisionDetection(CollisionDetectionResult result)
   {
      int numberOfObjects = collisionObjects.size();

      for (int i = 0; i < numberOfObjects; i++)
      {
         CollisionShape objectOne = collisionObjects.get(i);

         for (int j = i + 1; j < numberOfObjects; j++)
         {
            CollisionShape objectTwo = collisionObjects.get(j);

            objectOne.getTransformToWorld(transformOne);
            objectTwo.getTransformToWorld(transformTwo);

            CollisionShapeDescription descriptionOne = objectOne.getDescription();
            CollisionShapeDescription descriptionTwo = objectTwo.getDescription();

            if ((descriptionOne instanceof SphereShapeDescription) && (descriptionTwo instanceof SphereShapeDescription))
            {
               doSphereSphereCollisionDetection(objectOne, (SphereShapeDescription) descriptionOne, objectTwo, (SphereShapeDescription) descriptionTwo, result);
            }
            else if ((descriptionOne instanceof BoxShapeDescription) && (descriptionTwo instanceof BoxShapeDescription))
            {
               doBoxBoxCollisionDetection(objectOne, (BoxShapeDescription) descriptionOne, objectTwo, (BoxShapeDescription) descriptionTwo, result);
            }
         }
      }
   }

   private final DenseMatrix64F GMatrix = new DenseMatrix64F(3, 8);
   private final DenseMatrix64F HMatrix = new DenseMatrix64F(3, 8);

   private final DenseMatrix64F GTransposeMatrix = new DenseMatrix64F(8, 3);
   private final DenseMatrix64F HTransposeMatrix = new DenseMatrix64F(8, 3);

   private final DenseMatrix64F GTransposeGMatrix = new DenseMatrix64F(8, 8);
   private final DenseMatrix64F HTransposeHMatrix = new DenseMatrix64F(8, 8);

   private final DenseMatrix64F HTransposeGMatrix = new DenseMatrix64F(8, 8);
   private final DenseMatrix64F GTransposeHMatrix = new DenseMatrix64F(8, 8);

   private final DenseMatrix64F quadraticCostGMatrix = new DenseMatrix64F(16, 16);
   private final DenseMatrix64F quadraticCostFVector = new DenseMatrix64F(16, 1);

   private final DenseMatrix64F linearEqualityConstraintA = new DenseMatrix64F(2, 16);
   private final DenseMatrix64F linearEqualityConstraintB = new DenseMatrix64F(2, 1);

   private final DenseMatrix64F linearInequalityConstraintA = new DenseMatrix64F(16, 16);
   private final DenseMatrix64F linearInequalityConstraintB = new DenseMatrix64F(16, 1);
   private final boolean[] linearInequalityActiveSet = new boolean[16];
   private final DenseMatrix64F solutionVector = new DenseMatrix64F(16, 1);
   private final Point3d vertex = new Point3d();

   private void doBoxBoxCollisionDetection(CollisionShape objectOne, BoxShapeDescription descriptionOne, CollisionShape objectTwo, BoxShapeDescription descriptionTwo, CollisionDetectionResult result)
   {
      objectOne.getTransformToWorld(transformOne);
      objectTwo.getTransformToWorld(transformTwo);

      populateMatrixWithBoxVertices(transformOne, descriptionOne, GMatrix);
      populateMatrixWithBoxVertices(transformTwo, descriptionTwo, HMatrix);

      CommonOps.transpose(GMatrix, GTransposeMatrix);
      CommonOps.transpose(HMatrix, HTransposeMatrix);

      CommonOps.mult(GTransposeMatrix, GMatrix, GTransposeGMatrix);
      CommonOps.mult(HTransposeMatrix, HMatrix, HTransposeHMatrix);
      CommonOps.mult(HTransposeMatrix, GMatrix, HTransposeGMatrix);

      CommonOps.transpose(HTransposeGMatrix, GTransposeHMatrix);

      for (int i = 0; i < 8; i++)
      {
         for (int j = 0; j < 8; j++)
         {
            quadraticCostGMatrix.set(i, j, GTransposeGMatrix.get(i, j));
            quadraticCostGMatrix.set(i + 8, j + 8, HTransposeHMatrix.get(i, j));

            quadraticCostGMatrix.set(i + 8, j, -HTransposeGMatrix.get(i, j));
            quadraticCostGMatrix.set(i, j + 8, -GTransposeHMatrix.get(i, j));

         }

         linearInequalityActiveSet[i] = false;
         linearInequalityActiveSet[i + 8] = false;

         linearEqualityConstraintA.set(0, i, 1.0);
         linearEqualityConstraintA.set(1, i + 8, 1.0);

         linearInequalityConstraintA.set(i, i, -1.0);
         linearInequalityConstraintB.set(i, 0, 0.0);
         linearInequalityConstraintA.set(i + 8, i + 8, -1.0);
         linearInequalityConstraintB.set(i + 8, 0, 0.0);
      }

      linearEqualityConstraintB.set(0, 0, 1.0);
      linearEqualityConstraintB.set(1, 0, 1.0);

      boolean initialize = true;
      try
      {
//         boolean[] activeSet = new boolean[16];
//         solver.setQuadraticCostFunction(quadraticCostGMatrix, quadraticCostFVector, 0.0);
//         solver.setLinearEqualityConstraints(linearEqualityConstraintA, linearEqualityConstraintB);
//         solver.setLinearInequalityConstraints(linearInequalityConstraintA, linearInequalityConstraintB);
//         solver.solve(solutionVector);
         
//         solver.solve(quadraticCostGMatrix, quadraticCostFVector, linearEqualityConstraintA, linearEqualityConstraintB, linearInequalityConstraintA, linearInequalityConstraintB, activeSet , solutionVector);
         solver.solve(quadraticCostGMatrix, quadraticCostFVector, linearEqualityConstraintA, linearEqualityConstraintB, linearInequalityConstraintA, linearInequalityConstraintB, solutionVector, initialize);
//         System.out.println("solutionVector = " + solutionVector);
      }
      catch(NoConvergenceException exception)
      {
         return;
      }

      Point3d pointOnA = new Point3d();
      Point3d pointOnB = new Point3d();

      getVectorFromBoxVertices(descriptionOne, transformOne, solutionVector, true, pointOnA);
      getVectorFromBoxVertices(descriptionTwo, transformTwo, solutionVector, false, pointOnB);

      double distance = pointOnA.distance(pointOnB);

      Vector3d normalA = new Vector3d();
      normalA.sub(pointOnB, pointOnA);

      //TODO: Figure out the answer if they penetrate the boundary layer...
      if (distance > 1e-8)
      {
         normalA.normalize();
      }
      else
      {
         normalA.set(1.0, 0.0, 0.0);
      }

      if (distance < boxToBoxMargin)
      {
         SimpleContactWrapper contacts = new SimpleContactWrapper(objectOne, objectTwo);

         contacts.addContact(pointOnA, pointOnB, normalA, distance);
         result.addContact(contacts);
      }
   }

   private void populateMatrixWithBoxVertices(RigidBodyTransform transform, BoxShapeDescription description, DenseMatrix64F matrix)
   {
      double halfLengthXOne = description.getHalfLengthX();
      double halfWidthYOne = description.getHalfWidthY();
      double halfHeightZOne = description.getHalfHeightZ();

      int index = 0;

      for (double multX = -1.0; multX < 1.1; multX += 2.0)
      {
         for (double multY = -1.0; multY < 1.1; multY += 2.0)
         {
            for (double multZ = -1.0; multZ < 1.1; multZ += 2.0)
            {
               vertex.set(halfLengthXOne * multX, halfWidthYOne * multY, halfHeightZOne * multZ);
               transform.transform(vertex);
               matrix.set(0, index, vertex.getX());
               matrix.set(1, index, vertex.getY());
               matrix.set(2, index, vertex.getZ());

               index++;
            }
         }
      }
   }

   private void getVectorFromBoxVertices(BoxShapeDescription description, RigidBodyTransform transform, DenseMatrix64F solution, boolean firstPoint, Point3d solutionPoint)
   {
      solutionPoint.set(0.0, 0.0, 0.0);

      double halfLengthXOne = description.getHalfLengthX();
      double halfWidthYOne = description.getHalfWidthY();
      double halfHeightZOne = description.getHalfHeightZ();

      int index = 0;
      if (firstPoint == false)
      {
         index = 8;
      }

      for (double multX = -1.0; multX < 1.1; multX += 2.0)
      {
         for (double multY = -1.0; multY < 1.1; multY += 2.0)
         {
            for (double multZ = -1.0; multZ < 1.1; multZ += 2.0)
            {
               vertex.set(halfLengthXOne * multX, halfWidthYOne * multY, halfHeightZOne * multZ);
               transform.transform(vertex);

               double factor = solution.get(index, 0);

               solutionPoint.setX(solutionPoint.getX() + factor * vertex.getX());
               solutionPoint.setY(solutionPoint.getY() + factor * vertex.getY());
               solutionPoint.setZ(solutionPoint.getZ() + factor * vertex.getZ());

               index++;
            }
         }
      }
   }

   private void doSphereSphereCollisionDetection(CollisionShape objectOne, SphereShapeDescription descriptionOne, CollisionShape objectTwo, SphereShapeDescription descriptionTwo, CollisionDetectionResult result)
   {
      double radiusOne = descriptionOne.getRadius();
      double radiusTwo = descriptionTwo.getRadius();

      transformOne.getTranslation(centerOne);
      transformTwo.getTranslation(centerTwo);

      double distanceSquared = centerOne.distanceSquared(centerTwo);

      if (distanceSquared <= (radiusOne + radiusTwo) * (radiusOne + radiusTwo))
      {
         centerToCenterVector.sub(centerTwo, centerOne);

         Point3d pointOnOne = new Point3d(centerOne);
         normalVector.set(centerToCenterVector);
         normalVector.normalize();

         tempVector.set(normalVector);
         tempVector.scale(radiusOne);
         pointOnOne.add(tempVector);

         Point3d pointOnTwo = new Point3d(centerTwo);
         tempVector.set(normalVector);
         tempVector.scale(-radiusTwo);
         pointOnTwo.add(tempVector);

         double distance = Math.sqrt(distanceSquared) - radiusOne - radiusTwo;

         SimpleContactWrapper contacts = new SimpleContactWrapper(objectOne, objectTwo);
         contacts.addContact(pointOnOne, pointOnTwo, normalVector, distance);

         result.addContact(contacts);
      }
   }

   public void addShape(CollisionShape collisionShape)
   {
      collisionObjects.add(collisionShape);
   }

}
