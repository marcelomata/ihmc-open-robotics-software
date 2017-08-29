package us.ihmc.robotics.geometry;

import java.io.Serializable;

import org.ejml.data.DenseMatrix64F;

import us.ihmc.euclid.interfaces.GeometryObject;
import us.ihmc.euclid.tuple2D.Point2D;
import us.ihmc.euclid.tuple2D.Vector2D;
import us.ihmc.euclid.tuple2D.interfaces.Tuple2DBasics;
import us.ihmc.euclid.tuple2D.interfaces.Tuple2DReadOnly;
import us.ihmc.euclid.tuple3D.interfaces.Tuple3DBasics;
import us.ihmc.robotics.referenceFrames.ReferenceFrame;

public abstract class FrameTuple2D<S extends FrameTuple2D<S, T>, T extends Tuple2DBasics & GeometryObject<T>> extends AbstractFrameObject<S, T>
      implements Serializable
{
   private static final long serialVersionUID = 6275308250031489785L;

   private static final boolean DEBUG = false;

   protected final T tuple;
   protected String name;

   public FrameTuple2D(ReferenceFrame referenceFrame, T tuple, String name)
   {
      super(referenceFrame, tuple);

      if (DEBUG)
      {
         if (referenceFrame == null)
         {
            String errorMsg = "FrameTuple2d: created a " + getClass().getSimpleName() + " with a null reference frame.";
            System.err.println(errorMsg);
         }
      }

      this.tuple = getGeometryObject();
      this.name = name;
   }

   public final void setName(String name)
   {
      this.name = name;
   }

   public final String getName()
   {
      return name;
   }

   public final void set(double x, double y)
   {
      tuple.setX(x);
      tuple.setY(y);
   }

   public final void setIncludingFrame(ReferenceFrame referenceFrame, double x, double y)
   {
      this.referenceFrame = referenceFrame;
      set(x, y);
   }

   /**
    * Sets this tuple's components {@code x}, {@code y} in order from the given array
    * {@code tupleArray} and sets this tuple frame to {@code referenceFrame}.
    *
    * @param referenceFrame the new reference frame for this tuple.
    * @param tupleArray the array containing the new values for this tuple's components. Not
    *           modified.
    */
   public final void setIncludingFrame(ReferenceFrame referenceFrame, double[] tupleArray)
   {
      this.referenceFrame = referenceFrame;
      tuple.set(tupleArray);
   }

   /**
    * Sets this tuple's components {@code x}, {@code y} in order from the given array
    * {@code tupleArray} and sets this tuple frame to {@code referenceFrame}.
    *
    * @param referenceFrame the new reference frame for this tuple.
    * @param startIndex the first index to start reading from in the array.
    * @param tupleArray the array containing the new values for this tuple's components. Not
    *           modified.
    */
   public final void setIncludingFrame(ReferenceFrame referenceFrame, int startIndex, double[] tupleArray)
   {
      this.referenceFrame = referenceFrame;
      tuple.set(startIndex, tupleArray);
   }

   /**
    * Sets this tuple's components {@code x}, {@code y} in order from the given column
    * vector starting to read from its first row index and sets this tuple frame to
    * {@code referenceFrame}.
    *
    * @param referenceFrame the new reference frame for this tuple.
    * @param matrix the column vector containing the new values for this tuple's components. Not
    *           modified.
    */
   public final void setIncludingFrame(ReferenceFrame referenceFrame, DenseMatrix64F tupleDenseMatrix)
   {
      this.referenceFrame = referenceFrame;
      tuple.set(tupleDenseMatrix);
   }

   /**
    * Sets this tuple's components {@code x}, {@code y} in order from the given column
    * vector starting to read from {@code startRow} and sets this tuple frame to
    * {@code referenceFrame}.
    *
    * @param referenceFrame the new reference frame for this tuple.
    * @param startRow the first row index to start reading in the dense-matrix.
    * @param matrix the column vector containing the new values for this tuple's components. Not
    *           modified.
    */
   public final void setIncludingFrame(ReferenceFrame referenceFrame, int startRow, DenseMatrix64F tupleDenseMatrix)
   {
      this.referenceFrame = referenceFrame;
      tuple.set(startRow, tupleDenseMatrix);
   }

   /**
    * Sets this tuple's components {@code x}, {@code y} in order from the given matrix
    * starting to read from {@code startRow} at the column index {@code column} and sets this tuple
    * frame to {@code referenceFrame}.
    *
    * @param referenceFrame the new reference frame for this tuple.
    * @param startRow the first row index to start reading in the dense-matrix.
    * @param column the column index to read in the dense-matrix.
    * @param matrix the column vector containing the new values for this tuple's components. Not
    *           modified.
    */
   public final void setIncludingFrame(ReferenceFrame referenceFrame, int startRow, int column, DenseMatrix64F tupleDenseMatrix)
   {
      this.referenceFrame = referenceFrame;
      tuple.set(startRow, column, tupleDenseMatrix);
   }

   @Override
   public final void set(Tuple2DBasics tuple)
   {
      this.tuple.set(tuple);
   }

   public final void set(Tuple2DReadOnly tuple)
   {
      this.tuple.set(tuple);
   }

   @Override
   public final void setIncludingFrame(ReferenceFrame referenceFrame, Tuple2DBasics tuple)
   {
      this.referenceFrame = referenceFrame;
      set(tuple);
   }

   public final void setIncludingFrame(ReferenceFrame referenceFrame, Tuple2DReadOnly tuple)
   {
      this.referenceFrame = referenceFrame;
      set(tuple);
   }

   /**
    *
    * @throws ReferenceFrameMismatchException
    */
   public final void set(FrameTuple2D<?, ?> frameTuple2d)
   {
      checkReferenceFrameMatch(frameTuple2d);
      set(frameTuple2d.tuple);
   }

   /**
    * Set this frameTuple2d using the x and y coordinate the frameTuple passed in.
    *
    * @throws ReferenceFrameMismatchException
    */
   public final void setByProjectionOntoXYPlane(FrameTuple3D<?, ?> frameTuple)
   {
      checkReferenceFrameMatch(frameTuple);
      set(frameTuple.getX(), frameTuple.getY());
   }

   /**
    * Set this frameTuple2d using the x and y coordinate the frameTuple passed in.
    *
    * @throws ReferenceFrameMismatchException
    */
   public final void setByProjectionOntoXYPlaneIncludingFrame(FrameTuple3D<?, ?> frameTuple)
   {
      setIncludingFrame(frameTuple.getReferenceFrame(), frameTuple.getX(), frameTuple.getY());
   }

   public final void setIncludingFrame(FrameTuple2D<?, ?> frameTuple2d)
   {
      setIncludingFrame(frameTuple2d.referenceFrame, frameTuple2d.tuple);
   }

   public final void setX(double x)
   {
      tuple.setX(x);
   }

   public final void setY(double y)
   {
      tuple.setY(y);
   }

   public final void scale(double scaleFactor)
   {
      tuple.scale(scaleFactor);
   }

   public final void scale(double scaleXFactor, double scaleYFactor)
   {
      tuple.setX(tuple.getX() * scaleXFactor);
      tuple.setY(tuple.getY() * scaleYFactor);
   }

   public final double getX()
   {
      return tuple.getX();
   }

   public final double getY()
   {
      return tuple.getY();
   }

   public double distanceFromZero()
   {
      return Math.sqrt(tuple.getX() * tuple.getX() + tuple.getY() * tuple.getY());
   }

   /**
    * Returns a Point2d copy of the tuple in this FrameTuple.
    *
    * @return Point2d
    */
   public final Point2D getPointCopy()
   {
      return new Point2D(tuple);
   }

   /**
    * Returns a Vector2d copy of the tuple in this FrameTuple.
    *
    * @return Vector2d
    */
   public final Vector2D getVectorCopy()
   {
      return new Vector2D(this.tuple);
   }

   @Override
   public final void get(Tuple2DBasics tuple2dToPack)
   {
      tuple2dToPack.set(tuple);
   }

   /**
    * Pack this tuple2d in tuple3dToPack and tuple3dToPack.z = 0.0.
    *
    * @param tuple3dToPack {@code Tuple3DBasics}
    */
   public final void get(Tuple3DBasics tuple3dToPack)
   {
      tuple3dToPack.set(tuple.getX(), tuple.getY(), 0.0);
   }

   /**
    * Packs the components {@code x}, {@code y} in order in a column vector starting from
    * its first row index.
    *
    * @param tupleMatrixToPack the array in which this tuple is frame stored. Modified.
    */
   public final void get(DenseMatrix64F tupleMatrixToPack)
   {
      tuple.get(tupleMatrixToPack);
   }

   /**
    * Packs the components {@code x}, {@code y} in order in a column vector starting from
    * {@code startRow}.
    *
    * @param startRow the first row index to start writing in the dense-matrix.
    * @param tupleMatrixToPack the column vector in which this frame tuple is stored. Modified.
    */
   public final void get(int startRow, DenseMatrix64F tupleMatrixToPack)
   {
      tuple.get(startRow, tupleMatrixToPack);
   }

   /**
    * Packs the components {@code x}, {@code y} in order in a column vector starting from
    * {@code startRow} at the column index {@code column}.
    *
    * @param startRow the first row index to start writing in the dense-matrix.
    * @param column the column index to write in the dense-matrix.
    * @param tupleMatrixToPack the matrix in which this frame tuple is stored. Modified.
    */
   public final void get(int startRow, int column, DenseMatrix64F tupleMatrixToPack)
   {
      tuple.get(startRow, column, tupleMatrixToPack);
   }

   @Override
   public final void setToZero()
   {
      tuple.setToZero();
   }

   @Override
   public final void setToZero(ReferenceFrame referenceFrame)
   {
      setToZero();
      this.referenceFrame = referenceFrame;
   }

   @Override
   public final void setToNaN()
   {
      tuple.setToNaN();
   }

   @Override
   public final void setToNaN(ReferenceFrame referenceFrame)
   {
      setToNaN();
      this.referenceFrame = referenceFrame;
   }

   public final void checkForNaN()
   {
      if (containsNaN())
         throw new RuntimeException(getClass().getSimpleName() + " " + this + " has a NaN!");
   }

   @Override
   public final boolean containsNaN()
   {
      return tuple.containsNaN();
   }

   /**
    * Sets the value of this tuple to the scalar multiplication of tuple1 (this = scaleFactor *
    * tuple1).
    *
    * @param scaleFactor double
    * @param frameTuple1 Tuple2d
    */
   public final void scale(double scaleFactor, Tuple2DReadOnly tuple1)
   {
      tuple.setAndScale(scaleFactor, tuple1);
   }

   /**
    * Sets the value of this tuple to the scalar multiplication of tuple1 and then adds tuple2 (this
    * = scaleFactor * tuple1 + tuple2).
    *
    * @param scaleFactor double
    * @param frameTuple1 Tuple2d
    * @param frameTuple2 Tuple2d
    */
   public final void scaleAdd(double scaleFactor, Tuple2DReadOnly tuple1, Tuple2DReadOnly tuple2)
   {
      tuple.scaleAdd(scaleFactor, tuple1, tuple2);
   }

   /**
    * Sets the value of this tuple to the scalar multiplication of tuple1 and then adds the scalar
    * multiplication of tuple2 (this = scaleFactor1 * tuple1 + scaleFactor2 * tuple2).
    *
    * @param scaleFactor1 double
    * @param frameTuple1 Tuple2d
    * @param scaleFactor2 double
    * @param frameTuple2 Tuple2d
    */
   public final void scaleAdd(double scaleFactor1, Tuple2DReadOnly tuple1, double scaleFactor2, Tuple2DReadOnly tuple2)
   {
      tuple.setX(scaleFactor1 * tuple1.getX() + scaleFactor2 * tuple2.getX());
      tuple.setY(scaleFactor1 * tuple1.getY() + scaleFactor2 * tuple2.getY());
   }

   /**
    * Sets the value of this tuple to the scalar multiplication of itself and then adds tuple1 (this
    * = scaleFactor * this + tuple1). Checks if reference frames match.
    *
    * @param scaleFactor double
    * @param frameTuple1 Tuple2d
    */
   public final void scaleAdd(double scaleFactor, Tuple2DReadOnly tuple1)
   {
      tuple.scaleAdd(scaleFactor, tuple1);
   }

   /**
    * Sets the value of this frameTuple to the scalar multiplication of frameTuple1 (this =
    * scaleFactor * frameTuple1). Checks if reference frames match.
    *
    * @param scaleFactor double
    * @param frameTuple1 FrameTuple2d<?, ?>
    * @throws ReferenceFrameMismatchException
    */
   public final void scale(double scaleFactor, FrameTuple2D<?, ?> frameTuple1)
   {
      checkReferenceFrameMatch(frameTuple1);
      scale(scaleFactor, frameTuple1.tuple);
   }

   /**
    * Sets the value of this frameTuple to the scalar multiplication of frameTuple1 and then adds
    * frameTuple2 (this = scaleFactor * frameTuple1 + frameTuple2). Checks if reference frames
    * match.
    *
    * @param scaleFactor double
    * @param frameTuple1 FrameTuple2d<?, ?>
    * @param frameTuple2 FrameTuple2d<?, ?>
    * @throws ReferenceFrameMismatchException
    */
   public final void scaleAdd(double scaleFactor, FrameTuple2D<?, ?> frameTuple1, FrameTuple2D<?, ?> frameTuple2)
   {
      checkReferenceFrameMatch(frameTuple1);
      checkReferenceFrameMatch(frameTuple2);
      scaleAdd(scaleFactor, frameTuple1.tuple, frameTuple2.tuple);
   }

   /**
    * Sets the value of this tuple to the scalar multiplication of itself and then adds frameTuple1
    * (this = scaleFactor * this + frameTuple1). Checks if reference frames match.
    *
    * @param scaleFactor double
    * @param frameTuple1 FrameTuple2d<?, ?>
    * @throws ReferenceFrameMismatchException
    */
   public final void scaleAdd(double scaleFactor, FrameTuple2D<?, ?> frameTuple1)
   {
      checkReferenceFrameMatch(frameTuple1);
      scaleAdd(scaleFactor, frameTuple1.tuple);
   }

   public final void add(double dx, double dy)
   {
      tuple.setX(tuple.getX() + dx);
      tuple.setY(tuple.getY() + dy);
   }

   /**
    * Sets the value of this tuple to the sum of itself and tuple1.
    *
    * @param tuple1 the other Tuple2d
    */
   public final void add(Tuple2DReadOnly tuple1)
   {
      tuple.add(tuple1);
   }

   /**
    * Sets the value of this tuple to the sum of tuple1 and tuple2 (this = tuple1 + tuple2).
    *
    * @param tuple1 the first Tuple2d
    * @param tuple2 the second Tuple2d
    */
   public final void add(Tuple2DReadOnly tuple1, Tuple2DReadOnly tuple2)
   {
      tuple.add(tuple1, tuple2);
   }

   /**
    * Sets the value of this frameTuple to the sum of itself and frameTuple1 (this += frameTuple1).
    * Checks if reference frames match.
    *
    * @param frameTuple1 the other Tuple2d
    * @throws ReferenceFrameMismatchException
    */
   public final void add(FrameTuple2D<?, ?> frameTuple1)
   {
      checkReferenceFrameMatch(frameTuple1);
      add(frameTuple1.tuple);
   }

   /**
    * Sets the value of this frameTuple to the sum of frameTuple1 and frameTuple2 (this =
    * frameTuple1 + frameTuple2).
    *
    * @param frameTuple1 the first FrameTuple2d<?, ?>
    * @param frameTuple2 the second FrameTuple2d<?, ?>
    * @throws ReferenceFrameMismatchException
    */
   public final void add(FrameTuple2D<?, ?> frameTuple1, FrameTuple2D<?, ?> frameTuple2)
   {
      checkReferenceFrameMatch(frameTuple1);
      checkReferenceFrameMatch(frameTuple2);
      add(frameTuple1.tuple, frameTuple2.tuple);
   }

   /**
    * Sets the value of this tuple to the difference of itself and tuple1 (this -= tuple1).
    *
    * @param tuple1 the other Tuple2d
    */
   public final void sub(Tuple2DReadOnly tuple1)
   {
      tuple.sub(tuple1);
   }

   public final void sub(double dx, double dy)
   {
      tuple.setX(tuple.getX() - dx);
      tuple.setY(tuple.getY() - dy);
   }

   /**
    * Sets the value of this tuple to the difference of tuple1 and tuple2 (this = tuple1 - tuple2).
    *
    * @param tuple1 the first Tuple2d
    * @param tuple2 the second Tuple2d
    */
   public final void sub(Tuple2DReadOnly tuple1, Tuple2DReadOnly tuple2)
   {
      tuple.sub(tuple1, tuple2);
   }

   /**
    * Sets the value of this frameTuple to the difference of itself and frameTuple1 (this -=
    * frameTuple1).
    *
    * @param frameTuple1 the first FrameTuple2d<?, ?>
    * @throws ReferenceFrameMismatchException
    */
   public final void sub(FrameTuple2D<?, ?> frameTuple1)
   {
      checkReferenceFrameMatch(frameTuple1);
      sub(frameTuple1.tuple);
   }

   /**
    * Sets the value of this frameTuple to the difference of frameTuple1 and frameTuple2 (this =
    * frameTuple1 - frameTuple2).
    *
    * @param frameTuple1 the first FrameTuple2d<?, ?>
    * @param frameTuple2 the second FrameTuple2d<?, ?>
    * @throws ReferenceFrameMismatchException
    */
   public final void sub(FrameTuple2D<?, ?> frameTuple1, FrameTuple2D<?, ?> frameTuple2)
   {
      checkReferenceFrameMatch(frameTuple1);
      checkReferenceFrameMatch(frameTuple2);
      sub(frameTuple1.tuple, frameTuple2.tuple);
   }

   /**
    * Linearly interpolates between tuples tuple1 and tuple2 and places the result into this tuple:
    * this = (1-alpha) * tuple1 + alpha * tuple2.
    *
    * @param t1 the first tuple
    * @param t2 the second tuple
    * @param alpha the alpha interpolation parameter
    */
   public final void interpolate(Tuple2DReadOnly tuple1, Tuple2DReadOnly tuple2, double alpha)
   {
      tuple.interpolate(tuple1, tuple2, alpha);
   }

   /**
    * Linearly interpolates between tuples tuple1 and tuple2 and places the result into this tuple:
    * this = (1-alpha) * tuple1 + alpha * tuple2.
    *
    * @param t1 the first tuple
    * @param t2 the second tuple
    * @param alpha the alpha interpolation parameter
    * @throws ReferenceFrameMismatchException
    */
   public final void interpolate(FrameTuple2D<?, ?> frameTuple1, FrameTuple2D<?, ?> frameTuple2, double alpha)
   {
      frameTuple1.checkReferenceFrameMatch(frameTuple2);

      interpolate(frameTuple1.tuple, frameTuple2.tuple, alpha);
      referenceFrame = frameTuple1.getReferenceFrame();
   }

   public final void clipToMinMax(double minValue, double maxValue)
   {
      this.tuple.clipToMinMax(minValue, maxValue);
   }

   public final void negate()
   {
      tuple.negate();
   }

   /**
    * Returns true if the L-infinite distance between this tuple and tuple1 is less than or equal to
    * the epsilon parameter, otherwise returns false. The L-infinite distance is equal to
    * MAX[abs(x1-x2), abs(y1-y2)].
    *
    * @param tuple1 Tuple2d
    * @param threshold double
    */
   public final boolean epsilonEquals(Tuple2DReadOnly tuple1, double threshold)
   {
      return tuple.epsilonEquals(tuple1, threshold);
   }

   /**
    * Returns true if the L-infinite distance between this frameTuple and frameTuple1 is less than
    * or equal to the epsilon parameter, otherwise returns false. The L-infinite distance is equal
    * to MAX[abs(x1-x2), abs(y1-y2)].
    *
    * @param frameTuple1 FrameTuple2d<?, ?>
    * @param threshold double
    * @throws ReferenceFrameMismatchException
    */
   public final boolean epsilonEquals(FrameTuple2D<?, ?> frameTuple1, double threshold)
   {
      checkReferenceFrameMatch(frameTuple1);

      return epsilonEquals(frameTuple1.tuple, threshold);
   }

   /**
    * Returns this FrameTuple's ReferenceFrame.
    *
    * @return ReferenceFrame
    */
   @Override
   public final ReferenceFrame getReferenceFrame()
   {
      return referenceFrame;
   }

   //   public abstract void changeFrameUsingTransform(ReferenceFrame desiredFrame, RigidBodyTransform transformToNewFrame);
   //
   //   public abstract FrameTuple2d<?, ?> changeFrameUsingTransformCopy(ReferenceFrame desiredFrame, RigidBodyTransform transformToNewFrame);
   //
   //   public abstract void applyTransform(RigidBodyTransform transform);
   //
   //   public abstract FrameTuple2d<?, ?> applyTransformCopy(RigidBodyTransform transform);
   //
   //   public abstract void changeFrame(ReferenceFrame desiredFrame);

   public final double[] toArray()
   {
      return new double[] {tuple.getX(), tuple.getY()};
   }

   /**
    * toString
    * <p/>
    * String representation of a FrameVector (x,y):reference frame name
    *
    * @return String
    */
   @Override
   public final String toString()
   {
      return "" + tuple + "-" + referenceFrame;
   }
}