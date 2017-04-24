package us.ihmc.robotics.geometry;

import us.ihmc.euclid.geometry.BoundingBox2D;
import us.ihmc.euclid.geometry.tools.EuclidGeometryTools;
import us.ihmc.euclid.tuple2D.Point2D;
import us.ihmc.euclid.tuple2D.interfaces.Point2DBasics;
import us.ihmc.euclid.tuple2D.interfaces.Point2DReadOnly;
import us.ihmc.euclid.tuple2D.interfaces.Tuple2DReadOnly;
import us.ihmc.euclid.tuple2D.interfaces.Vector2DBasics;
import us.ihmc.euclid.tuple2D.interfaces.Vector2DReadOnly;
import us.ihmc.robotics.robotSide.RobotSide;

/**
 * This calculator class contains methods for computations with a ConvexPolygon2d such as
 * orthogonal projections and intersections.
 */
public class ConvexPolygon2dCalculator
{
   private static final double epsilon = 1.0E-10;

   /**
    * Returns the index of the closest vertex of the polygon to the given line. If there
    * are multiple closest vertices (line parallel to an edge) this will return the smaller
    * index.
    */
   public static int getClosestVertexIndex(Line2d line, ConvexPolygon2d polygon)
   {
      double minDistance = Double.POSITIVE_INFINITY;
      int index = -1;
      for (int i = 0; i < polygon.getNumberOfVertices(); i++)
      {
         Point2DReadOnly vertex = polygon.getVertex(i);
         double distance = line.distance(vertex);
         if (distance < minDistance)
         {
            index = i;
            minDistance = distance;
         }
      }

      return index;
   }

   /**
    * Packs the closest vertex of the polygon to the given line.
    */
   public static boolean getClosestVertex(Line2d line, ConvexPolygon2d polygon, Point2DBasics pointToPack)
   {
      int index = getClosestVertexIndex(line, polygon);
      if (index < 0)
         return false;
      pointToPack.set(polygon.getVertex(index));
      return true;
   }

   /**
    * Returns the index of the closest vertex of the polygon to the given point
    */
   public static int getClosestVertexIndex(Point2DReadOnly point, ConvexPolygon2d polygon)
   {
      double minDistance = Double.POSITIVE_INFINITY;
      int index = -1;
      for (int i = 0; i < polygon.getNumberOfVertices(); i++)
      {
         Point2DReadOnly vertex = polygon.getVertex(i);
         double distance = vertex.distance(point);
         if (distance < minDistance)
         {
            index = i;
            minDistance = distance;
         }
      }

      return index;
   }

   /**
    * Packs the closest vertex of the polygon to the given point
    */
   public static boolean getClosestVertex(Point2DReadOnly point, ConvexPolygon2d polygon, Point2DBasics pointToPack)
   {
      int index = getClosestVertexIndex(point, polygon);
      if (index < 0)
         return false;
      pointToPack.set(polygon.getVertex(index));
      return true;
   }

   /**
    * Packs the index of the closest edge to the given point. The index corresponds to the index
    * of the vertex at the start of the edge.
    */
   public static int getClosestEdgeIndex(Point2DReadOnly point, ConvexPolygon2d polygon)
   {
      int index = -1;
      if (!polygon.hasAtLeastTwoVertices())
         return index;

      double minDistance = Double.POSITIVE_INFINITY;
      for (int i = 0; i < polygon.getNumberOfVertices(); i++)
      {
         Point2DReadOnly start = polygon.getVertex(i);
         Point2DReadOnly end = polygon.getNextVertex(i);
         double distance = EuclidGeometryTools.distanceFromPoint2DToLineSegment2D(point, start, end);
         if (distance < minDistance)
         {
            index = i;
            minDistance = distance;
         }
      }

      return index;
   }

   /**
    * Packs the closest edge to the given point.
    */
   public static boolean getClosestEdge(Point2DReadOnly point, ConvexPolygon2d polygon, LineSegment2d edgeToPack)
   {
      int edgeIndex = getClosestEdgeIndex(point, polygon);
      if (edgeIndex == -1)
         return false;
      edgeToPack.set(polygon.getVertex(edgeIndex), polygon.getNextVertex(edgeIndex));
      return true;
   }

   /**
    * Determines if the point is inside the bounding box of the convex polygon.
    */
   public static boolean isPointInBoundingBox(double pointX, double pointY, double epsilon, ConvexPolygon2d polygon)
   {
      BoundingBox2D boundingBox = polygon.getBoundingBox();

      if (pointX < boundingBox.getMinPoint().getX() - epsilon)
         return false;
      if (pointY < boundingBox.getMinPoint().getY() - epsilon)
         return false;
      if (pointX > boundingBox.getMaxPoint().getX() + epsilon)
         return false;
      if (pointY > boundingBox.getMaxPoint().getY() + epsilon)
         return false;

      return true;
   }

   /**
    * Determines if the point is inside the bounding box of the convex polygon.
    */
   public static boolean isPointInBoundingBox(double pointX, double pointY, ConvexPolygon2d polygon)
   {
      return isPointInBoundingBox(pointX, pointY, 0.0, polygon);
   }

   /**
    * Determines if the pointToTest is inside the bounding box of the convex polygon.
    */
   public static boolean isPointInBoundingBox(Point2DReadOnly pointToTest, double epsilon, ConvexPolygon2d polygon)
   {
      return isPointInBoundingBox(pointToTest.getX(), pointToTest.getY(), epsilon, polygon);
   }

   /**
    * Determines if the point is inside the bounding box of the convex polygon.
    */
   public static boolean isPointInBoundingBox(Point2DReadOnly pointToTest, ConvexPolygon2d polygon)
   {
      return isPointInBoundingBox(pointToTest, 0.0, polygon);
   }

   /**
    * Determines if the polygonToTest is inside the convex polygon.
    */
   public static boolean isPolygonInside(ConvexPolygon2d polygonToTest, double epsilon, ConvexPolygon2d polygon)
   {
      for (int i = 0; i < polygonToTest.getNumberOfVertices(); i++)
      {
         if (!polygon.isPointInside(polygonToTest.getVertex(i), epsilon))
            return false;
      }

      return true;
   }

   /**
    * Determines if the polygonToTest is inside the convex polygon.
    */
   public static boolean isPolygonInside(ConvexPolygon2d polygonToTest, ConvexPolygon2d polygon)
   {
      return isPolygonInside(polygonToTest, 0.0, polygon);
   }

   /**
    * Translates the given polygon.
    */
   public static void translatePolygon(Tuple2DReadOnly translation, ConvexPolygon2d polygon)
   {
      for (int i = 0; i < polygon.getNumberOfVertices(); i++)
      {
         Point2D vertex = polygon.getVertexUnsafe(i);
         vertex.add(translation);
      }

      polygon.updateBoundingBox();
      polygon.updateCentroidAndArea();
   }

   /**
    * For an observer looking at the vertices corresponding to index1 and index2 this method will select the
    * index that corresponds to the vertex on the specified side.
    */
   public static int getVertexOnSide(int index1, int index2, RobotSide side, Point2DReadOnly observer, ConvexPolygon2d polygon)
   {
      Point2DReadOnly point1 = polygon.getVertex(index1);
      Point2DReadOnly point2 = polygon.getVertex(index2);
      double observerToPoint1X = point1.getX() - observer.getX();
      double observerToPoint1Y = point1.getY() - observer.getY();
      double observerToPoint2X = point2.getX() - observer.getX();
      double observerToPoint2Y = point2.getY() - observer.getY();

      // Rotate the vector from observer to point 2 90 degree counter clockwise.
      double observerToPoint2PerpendicularX = - observerToPoint2Y;
      double observerToPoint2PerpendicularY = observerToPoint2X;

      // Assuming the observer is looking at point 1 the dot product will be positive if point 2 is on the right of point 1.
      double dotProduct = observerToPoint1X * observerToPoint2PerpendicularX + observerToPoint1Y * observerToPoint2PerpendicularY;

      dotProduct = side.negateIfLeftSide(dotProduct);
      if (dotProduct > 0.0)
         return index2;
      return index1;
   }

   /**
    * For an observer looking at the vertices corresponding to index1 and index2 this method will select the
    * index that corresponds to the vertex on the left side.
    */
   public static int getVertexOnLeft(int index1, int index2, Point2DReadOnly observer, ConvexPolygon2d polygon)
   {
      return getVertexOnSide(index1, index2, RobotSide.LEFT, observer, polygon);
   }

   /**
    * For an observer looking at the vertices corresponding to index1 and index2 this method will select the
    * index that corresponds to the vertex on the right side.
    */
   public static int getVertexOnRight(int index1, int index2, Point2DReadOnly observer, ConvexPolygon2d polygon)
   {
      return getVertexOnSide(index1, index2, RobotSide.RIGHT, observer, polygon);
   }

   /**
    * Returns the index in the middle of the range from firstIndex to secondIndex moving counter clockwise.
    * E.g. in a polygon with 6 vertices given indices 0 and 2 (in this order) the method will return the
    * middle of the range [0 5 4 3 2]: 4
    */
   public static int getMiddleIndexCounterClockwise(int firstIndex, int secondIndex, ConvexPolygon2d polygon)
   {
      int numberOfVertices = polygon.getNumberOfVertices();
      if (secondIndex >= firstIndex)
         return (secondIndex + (firstIndex + numberOfVertices - secondIndex + 1) / 2) % numberOfVertices;
      else
         return (secondIndex + firstIndex + 1) / 2;
   }

   /**
    * Packs a vector that is orthogonal to the given edge, facing towards the outside of the polygon
    */
   public static void getEdgeNormal(int edgeIndex, Vector2DBasics normalToPack, ConvexPolygon2d polygon)
   {
      Point2DReadOnly edgeStart = polygon.getVertex(edgeIndex);
      Point2DReadOnly edgeEnd = polygon.getNextVertex(edgeIndex);

      double edgeVectorX = edgeEnd.getX() - edgeStart.getX();
      double edgeVectorY = edgeEnd.getY() - edgeStart.getY();

      normalToPack.set(-edgeVectorY, edgeVectorX);
      normalToPack.normalize();
   }

   /**
    * This finds the edges of the polygon that intersect the given line. Will pack the edges into edgeToPack1 and
    * edgeToPack2. Returns number of intersections found. An edge parallel to the line can not intersect the edge.
    * If the line goes through a vertex but is not parallel to an edge adjacent to that vertex this method will
    * only pack the edge before the vertex, not both edges.
    */
   public static int getIntersectingEdges(Line2d line, LineSegment2d edgeToPack1, LineSegment2d edgeToPack2, ConvexPolygon2d polygon)
   {
      if (polygon.hasExactlyOneVertex())
         return 0;

      int foundEdges = 0;
      for (int i = 0; i < polygon.getNumberOfVertices(); i++)
      {
         Point2DReadOnly startVertex = polygon.getVertex(i);
         Point2DReadOnly endVertex = polygon.getNextVertex(i);

         // edge is on the line
         if (line.isPointOnLine(startVertex) && line.isPointOnLine(endVertex))
         {
            if (polygon.hasExactlyTwoVertices())
               return 0;
            // set the edges to be the previous and the next edge
            edgeToPack1.set(polygon.getPreviousVertex(i), startVertex);
            edgeToPack2.set(endVertex, polygon.getNextVertex(polygon.getNextVertexIndex(i)));
            return 2;
         }

         if (line.isPointOnLine(startVertex))
            continue;

         if (doesLineIntersectEdge(line, i, polygon) || line.isPointOnLine(endVertex))
         {
            if (foundEdges == 0)
               edgeToPack1.set(startVertex, endVertex);
            else
               edgeToPack2.set(startVertex, endVertex);
            foundEdges++;
         }

         if (foundEdges == 2) break; // performance only
      }

      return foundEdges;
   }

   /**
    * Checks if a line intersects the edge with the given index.
    */
   public static boolean doesLineIntersectEdge(Line2d line, int edgeIndex, ConvexPolygon2d polygon)
   {
      if (!polygon.hasAtLeastTwoVertices())
         return false;

      Point2DReadOnly edgeStart = polygon.getVertex(edgeIndex);
      Point2DReadOnly edgeEnd = polygon.getNextVertex(edgeIndex);

      double lineDirectionX = line.normalizedVector.getX();
      double lineDirectionY = line.normalizedVector.getY();
      double edgeDirectionX = edgeEnd.getX() - edgeStart.getX();
      double edgeDirectionY = edgeEnd.getY() - edgeStart.getY();

      if (EuclidGeometryTools.areVector2DsParallel(lineDirectionX, lineDirectionY, edgeDirectionX, edgeDirectionY, 1.0e-7))
            return false;
      else
         return EuclidGeometryTools.doLine2DAndLineSegment2DIntersect(line.point, line.normalizedVector, edgeStart, edgeEnd);
   }

   /**
    * Determines if edge i of the polygon is parallel to the given direction. If the edge is too
    * short to determine its direction this method will return false.
    */
   public static boolean isEdgeParallel(int edgeIndex, Vector2DReadOnly direction, ConvexPolygon2d polygon)
   {
      Point2DReadOnly edgeStart = polygon.getVertex(edgeIndex);
      Point2DReadOnly edgeEnd = polygon.getNextVertex(edgeIndex);

      double edgeDirectionX = edgeEnd.getX() - edgeStart.getX();
      double edgeDirectionY = edgeEnd.getY() - edgeStart.getY();

      double crossProduct = -edgeDirectionY * direction.getX() + edgeDirectionX * direction.getY();
      return Math.abs(crossProduct) < epsilon;
   }

   // --- Methods that generate garbage ---
   public static Point2D getClosestVertexCopy(Line2d line, ConvexPolygon2d polygon)
   {
      Point2D ret = new Point2D();
      if (getClosestVertex(line, polygon, ret))
         return ret;
      return null;
   }

   public static Point2D getClosestVertexCopy(Point2DReadOnly point, ConvexPolygon2d polygon)
   {
      Point2D ret = new Point2D();
      if (getClosestVertex(point, polygon, ret))
         return ret;
      return null;
   }

   public static ConvexPolygon2d translatePolygonCopy(Tuple2DReadOnly translation, ConvexPolygon2d polygon)
   {
      ConvexPolygon2d ret = new ConvexPolygon2d(polygon);
      translatePolygon(translation, ret);
      return ret;
   }

   public static LineSegment2d[] getIntersectingEdgesCopy(Line2d line, ConvexPolygon2d polygon)
   {
      LineSegment2d edge1 = new LineSegment2d();
      LineSegment2d edge2 = new LineSegment2d();

      int edges = getIntersectingEdges(line, edge1, edge2, polygon);
      if (edges == 2)
         return new LineSegment2d[] {edge1, edge2};
      if (edges == 1)
         return new LineSegment2d[] {edge1};
      return null;
   }

   public static LineSegment2d getClosestEdgeCopy(Point2DReadOnly point, ConvexPolygon2d polygon)
   {
      LineSegment2d ret = new LineSegment2d();
      if (getClosestEdge(point, polygon, ret))
         return ret;
      return null;
   }
}
