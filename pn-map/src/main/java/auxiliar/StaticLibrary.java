package auxiliar;

import model.Region;
import org.openstreetmap.gui.jmapviewer.Coordinate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class StaticLibrary {
    /*
     * constants
     */
    public static final int DATAHORA = 0;
    public static final int ORDEM = 1;
    public static final int LINHA = 2;
    public static final int LATITUDE = 3;
    public static final int LONGITUDE = 4;
    public static final int VELOCIDADE = 5;

    public static final String USER_AGENT = "Mozilla/5.0";


    /*
     * global command line configuration
     */
    /** run as in a headless environment */
    public static boolean forceHeadless = true;
    /** ContextNet IP address */
    public static String contextNetIPAddress;
    /** ContextNet TCP port number */
    public static int contextNetPortNumber;



    /** interval in ms (interval to create a thread */
    public static final long interval = 5000;
    public static long intervalBetweenThreads = 500;
    /** in % (interval variance to create a thread) */
    public static final long variance = 20;		//



    /*
     * statistics
     */
    public static long nMessages = 0;
    /** start time - negative value means that there is no start time setted yet */
    public static long startTime = -1;
    /** stop time */
    public static long stopTime;



    /*
     * Global data
     */
    /** Core UUID */
    public static UUID coreUUID;
    /** nÃºmero da mensagem */
    public static long sequencial;
    /** passenger group type */
    public static final int PASSENGER_GROUP = 0;
//	public static GroupCommunicationManager groupManager;




    public StaticLibrary() {
        nMessages = 0;
        startTime = -1;
    }



    /**
     * Handles files, jar entries, and deployed jar entries in a zip file (EAR).
     *
     * @return The date if it can be determined, or null if not.
     */
    public static Date getClassBuildTime() {
        Date d = null;
        Class<?> currentClass = new Object() {}.getClass().getEnclosingClass();
        URL resource = currentClass.getResource(currentClass.getSimpleName() + ".class");
        if (resource != null) {
            if (resource.getProtocol().equals("file")) {
                try {
                    d = new Date(new File(resource.toURI()).lastModified());
                } catch (URISyntaxException ignored) {
                }
            } else if (resource.getProtocol().equals("jar")) {
                String path = resource.getPath();
                d = new Date(new File(path.substring(5, path.indexOf("!"))).lastModified());
            } else if (resource.getProtocol().equals("zip")) {
                String path = resource.getPath();
                File jarFileOnDisk = new File(path.substring(0, path.indexOf("!")));
                // long jfodLastModifiedLong = jarFileOnDisk.lastModified ();
                // Date jfodLasModifiedDate = new Date(jfodLastModifiedLong);
                try (JarFile jf = new JarFile(jarFileOnDisk)) {
                    ZipEntry ze = jf.getEntry(path.substring(path.indexOf("!") + 2));	// Skip the ! and the /
                    long zeTimeLong = ze.getTime();
                    Date zeTimeDate = new Date(zeTimeLong);
                    d = zeTimeDate;
                } catch (IOException | RuntimeException ignored) {
                }
            }
        }
        return d;

    }
}

