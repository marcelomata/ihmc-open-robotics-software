package us.ihmc.humanoidRobotics.communication.packets.walking;

import us.ihmc.communication.packets.Packet;
import us.ihmc.communication.packets.PlanarRegionsListMessage;
import us.ihmc.euclid.referenceFrame.FramePoint3D;
import us.ihmc.euclid.referenceFrame.FrameQuaternion;
import us.ihmc.euclid.referenceFrame.ReferenceFrame;
import us.ihmc.euclid.tuple3D.Point3D32;
import us.ihmc.euclid.tuple4D.Quaternion32;
import us.ihmc.footstepPlanning.FootstepPlannerType;
import us.ihmc.robotics.geometry.FramePose;
import us.ihmc.robotics.geometry.RotationTools;
import us.ihmc.robotics.robotSide.RobotSide;

public class FootstepPlanningRequestPacket extends Packet<FootstepPlanningRequestPacket>
{
   public static final int NO_PLAN_ID = -1;

   public RobotSide initialStanceSide;
   public Point3D32 stanceFootPositionInWorld;
   public Quaternion32 stanceFootOrientationInWorld;
   public Point3D32 goalPositionInWorld;
   public Quaternion32 goalOrientationInWorld;
   public FootstepPlannerType requestedPlannerType;
   public double timeout;
   public PlanarRegionsListMessage planarRegionsListMessage;

   public int planId = NO_PLAN_ID;

   public FootstepPlanningRequestPacket()
   {
      // empty constructor for serialization
   }

   public FootstepPlanningRequestPacket(FramePose initialStanceFootPose, RobotSide initialStanceSide, FramePose goalPose)
   {
      this(initialStanceFootPose, initialStanceSide, goalPose, FootstepPlannerType.PLANAR_REGION_BIPEDAL);
   }

   public FootstepPlanningRequestPacket(FramePose initialStanceFootPose, RobotSide initialStanceSide, FramePose goalPose, FootstepPlannerType requestedPlannerType)
   {
      set(initialStanceFootPose, initialStanceSide, goalPose, requestedPlannerType);
   }

   public void set(FramePose initialStanceFootPose, RobotSide initialStanceSide, FramePose goalPose, FootstepPlannerType requestedPlannerType)
   {
      this.initialStanceSide = initialStanceSide;

      FramePoint3D initialFramePoint = initialStanceFootPose.getFramePointCopy();
      initialFramePoint.changeFrame(ReferenceFrame.getWorldFrame());
      stanceFootPositionInWorld = new Point3D32(initialFramePoint.getPoint());

      FrameQuaternion initialFrameOrientation = initialStanceFootPose.getFrameOrientationCopy();
      initialFrameOrientation.changeFrame(ReferenceFrame.getWorldFrame());
      stanceFootOrientationInWorld = new Quaternion32(initialFrameOrientation.getQuaternion());

      FramePoint3D goalFramePoint = goalPose.getFramePointCopy();
      goalFramePoint.changeFrame(ReferenceFrame.getWorldFrame());
      goalPositionInWorld = new Point3D32(goalFramePoint.getPoint());

      FrameQuaternion goalFrameOrientation = goalPose.getFrameOrientationCopy();
      goalFrameOrientation.changeFrame(ReferenceFrame.getWorldFrame());
      goalOrientationInWorld = new Quaternion32(goalFrameOrientation.getQuaternion());

      this.requestedPlannerType = requestedPlannerType;
   }

   public void setTimeout(double timeout)
   {
      this.timeout = timeout;
   }

   public double getTimeout()
   {
      return timeout;
   }

   public void setPlanarRegionsListMessage(PlanarRegionsListMessage planarRegionsListMessage)
   {
      this.planarRegionsListMessage = planarRegionsListMessage;
   }

   public void setPlannerRequestId(int planId)
   {
      this.planId = planId;
   }

   @Override
   public boolean epsilonEquals(FootstepPlanningRequestPacket other, double epsilon)
   {
      if (!initialStanceSide.equals(other.initialStanceSide))
         return false;
      if (!stanceFootPositionInWorld.epsilonEquals(other.stanceFootPositionInWorld, (float) epsilon))
         return false;
      if (!RotationTools.quaternionEpsilonEquals(stanceFootOrientationInWorld, other.stanceFootOrientationInWorld, (float) epsilon))
         return false;
      if (!goalPositionInWorld.epsilonEquals(other.goalPositionInWorld, (float) epsilon))
         return false;
      if (!RotationTools.quaternionEpsilonEquals(goalOrientationInWorld, other.goalOrientationInWorld, (float) epsilon))
         return false;
      if(this.requestedPlannerType != other.requestedPlannerType)
         return false;
      if(planId != other.planId)
         return false;
      if(planarRegionsListMessage != null && other.planarRegionsListMessage != null && planarRegionsListMessage.epsilonEquals(other.planarRegionsListMessage, epsilon))
         return false;
      return true;
   }

}
