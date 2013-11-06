package us.ihmc.robotDataCommunication.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LogPropertiesWriter extends LogProperties
{
   private static final long serialVersionUID = -7146049485411068887L;

   private final File file;
   
   public LogPropertiesWriter(File file)
   {
      super();
      this.file = file;
      if(file.exists())
      {
         throw new RuntimeException("Properties file " + file.getAbsolutePath() + " already exists");
      }
      setProperty("version", version);
      
      // Backwards comparability options
      setProperty("video.hasTimebase", "true");
   }
   
   public void store() throws IOException
   {
      FileWriter writer = new FileWriter(file);
      store(writer, "Written by yovariable data logger");
      writer.close();
   }

}
