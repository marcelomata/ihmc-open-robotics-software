package us.ihmc.darpaRoboticsChallenge;


// Remove all parameters from this class and move to robot-specific interfaces. Change DRCConfigParametersTest to enforce less variables.
@Deprecated
public class DRCConfigParameters
{
   public static final boolean USE_LOGGER = true;
   public static final boolean MAKE_SLIDER_BOARD = false; 

   public static final boolean USE_HYDRA = false;
   public static final boolean USE_MINI_ATLAS = false;

   // Log images from the primary camera
   public static boolean LOG_PRIMARY_CAMERA_IMAGES = false;

   // UI
   public static final int UI_JOINT_CONFIGURATION_UPDATE_MILLIS = 100;
   public static final boolean USE_COLLISIONS_MESHS_FOR_VISUALIZATION = false;

   // LIDAR Processor
   public static boolean LIDAR_ADJUSTMENT_ACTIVE = false;
   public static final double GRID_RESOLUTION = 0.025; // in meters
   public static final double OCTREE_RESOLUTION_WHEN_NOT_USING_RESOLUTION_SPHERE = 0.025;
   public static final double FOOTSTEP_FITTING_BUFFER_SIZE = -0.01;
   public static boolean CALIBRATE_ARM_MODE = false;

   
   public static final boolean ALLOW_MODEL_CORRUPTION = false;
   public static final boolean SEND_ROBOT_DATA_TO_ROS = true;
}
