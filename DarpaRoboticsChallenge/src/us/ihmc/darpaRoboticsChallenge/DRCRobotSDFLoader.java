package us.ihmc.darpaRoboticsChallenge;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import com.yobotics.simulationconstructionset.SimulationConstructionSet;

import us.ihmc.SdfLoader.JaxbSDFLoader;
import us.ihmc.atlas.AtlasRobotModel;
import us.ihmc.darpaRoboticsChallenge.drcRobot.DRCRobotJointMap;

public class DRCRobotSDFLoader
{
   public static JaxbSDFLoader loadDRCRobot(DRCRobotJointMap jointMap)
   {
      return loadDRCRobot(jointMap, false);
   }

   public static JaxbSDFLoader loadDRCRobot(DRCRobotJointMap jointMap, boolean headless)
   {
      InputStream fileInputStream;
      ArrayList<String> resourceDirectories = new ArrayList<String>();
      Class<DRCRobotSDFLoader> myClass = DRCRobotSDFLoader.class;
      DRCRobotModel selectedModel = jointMap.getSelectedModel();

      if (!headless)
      {
    	  for(String resource : selectedModel.getResourceDirectories())
    	  {
    		  resourceDirectories.add(resource);
    	  }
    	  
//         resourceDirectories.add(myClass.getResource("models/GFE/gazebo").getFile());
//         resourceDirectories.add(myClass.getResource("models/GFE/").getFile());
//         resourceDirectories.add(myClass.getResource("models/GFE/gazebo_models/atlas_description").getFile());
//         resourceDirectories.add(myClass.getResource("models/GFE/gazebo_models/multisense_sl_description").getFile());
//         resourceDirectories.add(myClass.getResource("models").getFile());
      }

      fileInputStream = selectedModel.getSdfFileAsStream();
      
      if(fileInputStream==null)
      {
    	  System.err.println("failed to load sdf file");
      }


      JaxbSDFLoader jaxbSDFLoader;
      try
      {
         jaxbSDFLoader = new JaxbSDFLoader(fileInputStream, resourceDirectories);
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException("Cannot find SDF file: " + e.getMessage());
      }
      catch (JAXBException e)
      {
         e.printStackTrace();

         throw new RuntimeException("Invalid SDF file: " + e.getMessage());
      }

      return jaxbSDFLoader;
   }

   public static void main(String[] args)
   {
	   DRCRobotModel selectedModel = new AtlasRobotModel();
      DRCRobotJointMap jointMap = selectedModel.getJointMap(false, false);
      JaxbSDFLoader loader = loadDRCRobot(jointMap, false);
      System.out.println(loader.createRobot(jointMap, true).getName());
      
      SimulationConstructionSet scs = new SimulationConstructionSet(loader.createRobot(jointMap, false));
      scs.startOnAThread();

   }

}
