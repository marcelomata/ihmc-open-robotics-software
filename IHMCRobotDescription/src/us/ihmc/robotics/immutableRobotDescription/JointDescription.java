package us.ihmc.robotics.immutableRobotDescription;

import org.immutables.value.Value.Default;

import java.util.List;

public abstract class JointDescription implements NamedObject, LocatedObject
{
   public abstract List<JointDescription> getChildrenJoints();

   public abstract LinkDescription getLink();

   public abstract List<GroundContactPointDescription> getGroundContactPoints();

   public abstract List<ExternalForcePointDescription> getExternalForcePoints();

   public abstract List<KinematicPointDescription> getKinematicPoints();

   public abstract List<JointWrenchSensorDescription> getWrenchSensors();

   public abstract List<CameraSensorDescription> getCameraSensors();

   public abstract List<IMUSensorDescription> getIMUSensors();

   public abstract List<LidarSensorDescription> getLidarSensors();

   public abstract List<ForceSensorDescription> getForceSensors();

   @Default public boolean getDynamic()
   {
      return true;
   }

   // TODO: get rid of this as soon as Immutables can generate it
   public interface Builder
   {
      Builder addChildrenJoints(JointDescription element);

      Builder addAllChildrenJoints(Iterable<? extends JointDescription> elements);

      Builder link(LinkDescription link);

      Builder addGroundContactPoints(GroundContactPointDescription element);

      Builder addAllGroundContactPoints(Iterable<? extends GroundContactPointDescription> elements);

      Builder addExternalForcePoints(ExternalForcePointDescription element);

      Builder addAllExternalForcePoints(Iterable<? extends ExternalForcePointDescription> elements);

      Builder addKinematicPoints(KinematicPointDescription element);

      Builder addAllKinematicPoints(Iterable<? extends KinematicPointDescription> elements);

      Builder addWrenchSensors(JointWrenchSensorDescription element);

      Builder addAllWrenchSensors(Iterable<? extends JointWrenchSensorDescription> elements);

      Builder addCameraSensors(CameraSensorDescription element);

      Builder addAllCameraSensors(Iterable<? extends CameraSensorDescription> elements);

      Builder addIMUSensors(IMUSensorDescription element);

      Builder addAllIMUSensors(Iterable<? extends IMUSensorDescription> elements);

      Builder addLidarSensors(LidarSensorDescription element);

      Builder addAllLidarSensors(Iterable<? extends LidarSensorDescription> elements);

      Builder addForceSensors(ForceSensorDescription element);

      Builder addAllForceSensors(Iterable<? extends ForceSensorDescription> elements);

      Builder dynamic(boolean dynamic);

      JointDescription build();
   }
}