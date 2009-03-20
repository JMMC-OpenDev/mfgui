
//import fr.jmmc.mf.models.Settings;
import fr.jmmc.mf.models.Settings;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

/**
 * This class just help to test marsal/unmarshal performance
 */
public class ReadAndWriteXml {

    public static String process(String filename) {
        File f = new File(filename);
        File out = new File(filename+".out");
        System.out.println("" + new Date() + ": NEW PROCESS one file '"+filename+"'");
        try {
            //unserialize
            FileReader reader = new FileReader(f);
            System.out.println("" + new Date() + ": start file reading");
            Settings s = Settings.unmarshal(reader);
            System.out.println("" + new Date() + ": end file reading");

            //modify object
            s.setUserInfo("modified");

            //serialize in File
            Writer writer = new FileWriter(out);
            System.out.println("" + new Date() + ": start file writting");
            s.marshal(writer);
            System.out.println("" + new Date() + ": end file writting");

            //serialize in String
            writer = new StringWriter();
            System.out.println("" + new Date() + ": start string writting");
            s.marshal(writer);
            System.out.println("" + new Date() + ": end string writting");
            System.out.println("writer = " + writer.toString());

            System.out.println("");
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            process(arg);
        }
    }
}
