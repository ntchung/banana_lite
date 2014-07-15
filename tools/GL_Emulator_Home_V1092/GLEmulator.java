// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 8/18/2008 3:30:55 PM
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 

package emulator;

import com.nttdocomo.ui.maker.IApplicationMIDlet;
import emulator.multijar.Client;
import emulator.multijar.FrameSync;
import emulator.multijar.Server;
import java.io.*;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import sun.net.dns.ResolverConfiguration;

// Referenced classes of package emulator:
//            CustomClassAdapter, CustomDevicesFrame, DeviceLoader, E, 
//            KeyMapping, LaunchPad, LogFrame, MainApplet, 
//            MainFrame, ObfuscatorMapping, ResourceManager, W, 
//            WatchFrame, ag, ah, bg, 
//            cp, eC

public class GLEmulator
{

    public GLEmulator()
    {
    }

    public static String getOption(String s1)
    {
        return (String)v.get(s1);
    }

    public static String getAppProperty(String s1)
    {
        return getAppProperty(s1, true);
    }

    public static String getAppProperty(String s1, boolean flag)
    {
        if(s != null)
        {
            String s2 = s.getProperty(s1);
            if(flag)
                LogFrame.println("getAppProperty " + s1 + "='" + s2 + "'");
            return s2;
        } else
        {
            return null;
        }
    }

    public static String getVersionString()
    {
		try
		{
        return "Gameloft Java Emulator v" + (new BufferedReader(new InputStreamReader((emulator.GLEmulator.class).getResourceAsStream("/version.txt")))).readLine();
        }
		catch(Exception exception)
		{
        }
        return "Gameloft Java Emulator (unknown version)";
    }

    public static void notifyDestroyed()
    {
        mainFrame.watchFrame.dumpValues("./dump.txt");
    }

    public static void extractResource(String s1, File file)
    {
        try
        {
            InputStream inputstream = (emulator.GLEmulator.class).getResourceAsStream(s1);
            FileOutputStream fileoutputstream = new FileOutputStream(file);
            byte abyte0[] = new byte[512];
            try
            {
                int i1;
                for(; inputstream.available() > 0; fileoutputstream.write(abyte0, 0, i1))
                    i1 = inputstream.read(abyte0);

            }
            catch(Exception exception1) { }
            inputstream.close();
            fileoutputstream.close();
        }
        catch(Exception exception) { }
    }

    public static InputStream getResourceAsStream(String s1)
    {
        return x.getResourceAsStream(s1);
    }

    public static InputStream getSystemResourceAsStream(String s1)
    {
        return x.getResourceAsStream(s1, false);
    }

    public static ClassLoader getMIDletClassLoader()
    {
        if(j == null)
            j = new ag(ClassLoader.getSystemClassLoader());
        return j;
    }

    public static void showHelpFile()
    {
        try
        {
            Runtime.getRuntime().exec("rundll32 SHELL32.DLL,ShellExec_RunDLL glemulatordoc.pdf");
        }
        catch(Exception exception) { }
    }

    public static FrameSync getFrameSync()
    {
        return q;
    }

    public static void setFrameSync(FrameSync framesync)
    {
        q = framesync;
    }

    public static Server getServer()
    {
        return o;
    }

    public static Client getClient()
    {
        return p;
    }

    public static native long QueryPerformanceTimer();

    public static native long QueryPerformanceFrequency();

    public static long getPerformanceTime()
    {
		try
		{
        if(y == 0L)
            y = QueryPerformanceFrequency() / 0xf4240L;
        return (QueryPerformanceTimer() * 1000L) / y;
		}
		catch (Throwable throwable)
		{
        if(y == 0L)
        {
            y = 1L;
            System.out.println("glemu.dll warning: " + throwable);
			}
        }
        return System.currentTimeMillis();
    }

    public static long getVirtualTime()
    {
        long l1 = System.currentTimeMillis() - A;
        return l1;
    }

    static void a()
    {
        if(z != 0L)
        {
            long l1 = System.currentTimeMillis() - z;
            A += l1;
            z = 0L;
        }
    }

    static boolean a(String as[])
    {
        String s1 = "";
        for(int i1 = 0; i1 < as.length; i1++)
        {
            String s3 = as[i1].toLowerCase();
            if(s3.startsWith("-"))
            {
                s1 = s3.substring(1);
                if(s1.equals("help") || s1.equals("h"))
                    k = true;
                if(s1.startsWith("d") && s1.indexOf('=') > 0)
                {
                    StringTokenizer stringtokenizer = new StringTokenizer(s1.substring(1), "=");
                    DeviceLoader.a(stringtokenizer.nextToken(), stringtokenizer.nextToken());
                    continue;
                }
                if(s1.equals("quiet"))
                {
                    l = true;
                    continue;
                }
                if(s1.equals("server"))
                {
                    serverMode = true;
                    continue;
                }
                if(s1.equals("noverify"))
                {
                    n = false;
                    continue;
                }
                if(s1.equals("fullscreen"))
                {
                    MainFrame.f = true;
                    continue;
                }
                if(s1.equals("monitormethodcalls"))
                {
                    CustomClassAdapter.d = true;
                    continue;
                }
                if(s1.equals("syncthreadstart"))
                {
                    CustomClassAdapter.c = true;
                    continue;
                }
                if(s1.equals("monitorallimages"))
                {
                    Image.monitorAllImages = true;
                    continue;
                }
                if(s1.equals("omithideshownotify"))
                {
                    m = false;
                    continue;
                }
                if(s1.equals("nolibpng"))
                    useDefaultPNGLoader = true;
                else
                    v.put(s1, "");
                continue;
            }
            if(s1.equals("device"))
            {
                c = s3;
                continue;
            }
            if(s1.equals("jar"))
            {
                midletJar = s3;
                continue;
            }
            if(s1.equals("midletclassname"))
            {
                midletClassName = as[i1];
                continue;
            }
            if(s1.equals("cp"))
            {
                e = s3;
                continue;
            }
            if(s1.equals("ppfolder"))
            {
                d = s3;
                continue;
            }
            if(s1.equals("devicefile"))
            {
                f = s3;
                continue;
            }
            if(s1.equals("rms"))
            {
                rmsFolder = s3;
                if(!rmsFolder.endsWith("\\"))
                    rmsFolder = rmsFolder + "\\";
                continue;
            }
            if(s1.equals("invokemethod"))
            {
                invokeMethod = as[i1];
                continue;
            }
            if(s1.equals("logfile"))
            {
                g = s3;
                continue;
            }
            if(s1.equals("waitclientcount"))
            {
                waitClientCount = Integer.parseInt(s3);
                continue;
            }
            if(s1.equals("client"))
            {
                clientIndex = Integer.parseInt(s3);
                clientMode = true;
                continue;
            }
            if(s1.equals("rmsmaxsize"))
            {
                i = Integer.parseInt(s3);
                continue;
            }
            if(s1.equals("cmdfile"))
            {
                h = s3;
                continue;
            }
            if(s1.equals("systemfont"))
                systemFontName = s3;
            else
                v.put(s1, s3);
        }

        if(k || c.equals("") || midletJar.equals("") && midletClassName == null || f.equals(""))
        {
            String s2 = "";
            s2 = s2 + "params: -device <devicename> -jar <midletjar> -deviceFile <deviceFile> -ppfolder <ppfolder> -rms <folder to store the rms> [-invokeMethod <name of the midlet method to invoke before startApp>] [-midletclassname <midletname>] [-cp <path where classes can be found>]\n";
            s2 = s2 + "device and jar are mandatory\n";
            System.out.println(s2);
            return false;
        }
        if(midletJar.toLowerCase().endsWith(".zip"))
            try
            {
                File file = new File(System.getProperty("java.io.tmpdir"), "glemulatorzip_" + System.currentTimeMillis());
                file.mkdir();
                file.deleteOnExit();
                System.out.println("Extracting " + midletJar + " to " + file.getAbsolutePath());
                ZipFile zipfile = new ZipFile(midletJar);
                Enumeration enumeration = zipfile.entries();
                byte abyte0[] = new byte[500];
                FileOutputStream fileoutputstream;
                for(; enumeration.hasMoreElements(); fileoutputstream.close())
                {
                    ZipEntry zipentry = (ZipEntry)enumeration.nextElement();
                    System.out.println("+ " + zipentry.getName());
                    File file1 = new File(file, zipentry.getName());
                    file1.deleteOnExit();
                    InputStream inputstream = zipfile.getInputStream(zipentry);
                    fileoutputstream = new FileOutputStream(file1);
                    int j1;
                    for(; inputstream.available() > 0; fileoutputstream.write(abyte0, 0, j1))
                        j1 = inputstream.read(abyte0);

                    String s4 = file1.getAbsolutePath().toLowerCase();
                    if(s4.endsWith(".jar") && !midletJar.endsWith(".jar"))
                        midletJar = s4;
                    inputstream.close();
                }

                zipfile.close();
            }
            catch(Exception exception)
            {
                exception.printStackTrace();
            }
        System.out.println("device: " + c);
        System.out.println("jar: " + midletJar);
        System.out.println("ppfolder: " + d);
        System.out.println("deviceFile: " + f);
        System.out.println("rms folder: " + rmsFolder);
        return true;
    }

    static void b()
    {
        try
        {
            LogFrame.println("loading icon...");
            StringTokenizer stringtokenizer = new StringTokenizer(getAppProperty("MIDlet-1"), ",");
            stringtokenizer.nextToken();
            String s1 = stringtokenizer.nextToken().trim();
            if(s1.length() > 0)
                mainFrame.setIconImage(Image.createImage(s1).getImpl());
        }
        catch(Exception exception)
        {
            LogFrame.println("Failed to load icon.");
        }
    }

    static void a(String s1, String s2)
    {
        try
        {
            if(System.getProperty(s1) == null)
                System.setProperty(s1, s2);
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    static void c()
    {
        s.setProperty("Content-Folder", "Games");
        s.setProperty("Content-DRM-Renew-URL", "http://openpathproducts.com/mrc/renewlicense.jsp");
        s.setProperty("Content-DRM-Check-URL", "http://openpathproducts.com/mrc/delivery/checklicense.jsp");
        s.setProperty("Content-DRM-Cancel-URL", "http://openpathproducts.com/mrc/cancellicense.jsp");
        s.setProperty("Content-DRM-Until", "Sun, 25 Dec 2008 01:01:01 GMT");
        a("supports.mixing", "false");
        a("microedition.media.version", "1.0");
        a("microedition.configuration", "CLDC-1.1");
        a("microedition.platform", c);
        a("microedition.m3g.version", "1.1");
        a("bluetooth.network.host", "localhost");
        a("bluetooth.friendlyname", "DefaultName");
    }

    static void d()
    {
        try
        {
            bg.a();
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public static void appletMain(boolean flag)
        throws Exception
    {
        x = ResourceManager.a();
        new LogFrame();
        DeviceLoader.a(f);
        DeviceLoader.b(c);
        KeyMapping.a();
        HttpURLConnection httpurlconnection;
        InputStream inputstream;
        try
        {
            s = new Properties();
            String s1 = MainApplet.a.getParameter("auth");
            httpurlconnection = (HttpURLConnection)(new URL(s1)).openConnection();
            if(httpurlconnection.getResponseCode() != 200)
            {
                System.out.println(httpurlconnection.getResponseMessage());
                return;
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            return;
        }
        inputstream = httpurlconnection.getInputStream();
        s.load(inputstream);
        httpurlconnection.disconnect();
        mainFrame = new MainFrame();
        if(flag)
            mainFrame.show();
        c();
        midletClassName = s.getProperty("MIDlet-1");
        for(StringTokenizer stringtokenizer = new StringTokenizer(midletClassName, ", "); stringtokenizer.hasMoreTokens();)
            midletClassName = stringtokenizer.nextToken().trim();

        if(!flag)
            d();
        Class class1 = Class.forName(midletClassName);
        pMidlet = (MIDlet)class1.newInstance();
        pMidlet.invokeStartApp();
        if(flag)
        {
            try
            {
                mainFrame.setTitle(getAppProperty("MIDlet-Name") + " - " + mainFrame.getTitle());
            }
            catch(Exception exception1) { }
            b();
        }
        return;
    }

    static Object a(Object obj)
        throws Error, Exception
    {
        boolean flag = true;
        String s1 = new String(new char[] {
            'g', 'a', 'm', 'e', 'l', 'o', 'f', 't', '.', 'o', 
            'r', 'g'
        });
        String s2 = new String(new char[] {
            'l', 'o', 'n', 'g', 't', 'a', 'i', 'l', 's', 't', 
            'u', 'd', 'i', 'o', 's', '.', 'c', 'o', 'm'
        });
        Iterator iterator = ResolverConfiguration.open().searchlist().iterator();
        do
        {
            if(!iterator.hasNext())
                break;
            String s3 = (String)iterator.next();
            if(s3.endsWith(s1) || s3.endsWith(s2))
                flag = true;
        } while(true);
        if(!flag)
        {
            JOptionPane.showMessageDialog(null, "GLEmulator security check exception.");
            throw new Error("");
        } else
        {
            return obj;
        }
    }

    public static void main(String args[])
        throws Exception
    {
        a(new Object());
        x = ResourceManager.a();
        System.out.println("Emulator started");
        b = args;
        if(!a(args))
        {
            if(!k)
                (new LaunchPad()).show();
            return;
        }
        try
        {
            String s1 = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(s1);
        }
        catch(Exception exception)
        {
            System.out.println("Error setting native LAF: " + exception);
        }
        new LogFrame();
        try
        {
            System.loadLibrary("glemu");
        }
        catch(Error error)
        {
            LogFrame.println("Warning: glemu.dll can not be found. Using default png loader.");
            useDefaultPNGLoader = true;
        }
        if(d != null)
            t = new cp(System.out, d);
        if(l)
            t = new ah();
        if(g != null)
            u = new eC(g);
        if(!n)
            Graphics.verifyCoordinates = false;
        DeviceLoader.a(f);
        if((new File(CustomDevicesFrame.d)).exists())
            DeviceLoader.a(CustomDevicesFrame.d);
        DeviceLoader.b(c);
        W.a(DeviceLoader.currentDevice);
        KeyMapping.a();
        RecordStore.setRMSMaximumSize(i);
        if(DeviceLoader.b().compareTo("motorola_triplets") != 0);
        if(serverMode)
        {
            o = new Server(8000);
            o.waitForClientConnections(waitClientCount);
        } else
        if(clientMode)
        {
            Thread.sleep(2000L);
            p = new Client(8000);
            Thread.sleep(500L);
            p.waitForSync();
        }
        try
        {
            Properties properties = new Properties();
            properties.load(new FileInputStream(midletJar.substring(0, midletJar.length() - 3) + "jad"));
            s = properties;
            LogFrame.println("properties loaded from jad ");
        }
        catch(Exception exception1)
        {
            LogFrame.println("j2me warning: jad file not found: " + exception1);
        }
        try
        {
            if(s == null)
            {
                Properties properties1 = new Properties();
                properties1.load(new FileInputStream(midletJar.substring(0, midletJar.length() - 3) + "jam"));
                s = properties1;
                LogFrame.println("properties loaded from jam ");
                doja = true;
            }
        }
        catch(Exception exception2)
        {
            LogFrame.println("doja warning: jam file not found: " + exception2);
        }
        if(midletClassName == null)
        {
            JarFile jarfile = new JarFile(midletJar);
            Enumeration enumeration = jarfile.entries();
            do
            {
                if(!enumeration.hasMoreElements())
                    break;
                ZipEntry zipentry = (ZipEntry)enumeration.nextElement();
                if(zipentry.getName().endsWith(".class"))
                {
                    String s2 = zipentry.getName().substring(0, zipentry.getName().length() - 6).replace('/', '.');
                    a.add(s2);
                    LogFrame.println("class " + s2);
                }
            } while(true);
            if(s == null)
            {
                s = new Properties();
                InputStream inputstream = getSystemResourceAsStream("/META-INF/MANIFEST.MF");
                if(inputstream != null)
                {
                    s.load(inputstream);
                    inputstream.close();
                }
            }
            midletClassName = s.getProperty("MIDlet-1");
            if(midletClassName != null)
            {
                for(StringTokenizer stringtokenizer = new StringTokenizer(midletClassName, ", "); stringtokenizer.hasMoreTokens();)
                    midletClassName = stringtokenizer.nextToken().trim();

            }
            //break MISSING_BLOCK_LABEL_837;
        }
		else
		{
        s = new Properties();
        if(e == null)
        {
            System.out.println("invalid -cp argument:" + e);
            return;
        }
        try
        {
            File file = new File(e);
            File afile[] = file.listFiles();
            for(int i1 = 0; i1 < afile.length; i1++)
            {
                String s3 = afile[i1].getName();
                if(s3.endsWith(".class"))
                    a.add(s3.substring(0, s3.length() - 6));
            }

        }
        catch(Exception exception3) { }
		}
        if(doja)
            midletClassName = getAppProperty("AppClass");
        if(!clientMode)
            ObfuscatorMapping.a();
        mainFrame = new MainFrame();
        mainFrame.show();
        b();
        c();
        Image.allImages.clear();
        ResourceManager.c();
        if(doja)
        {
            pMidlet = new IApplicationMIDlet(getAppProperty("AppClass").trim());
            pMidlet.invokeStartApp();
            B = true;
            return;
        }
        try
        {
            LogFrame.println("Midlet class='" + midletClassName + "'");
            Class class1 = Class.forName(midletClassName, true, j);
            LogFrame.println("ClassLoader=" + class1.getClassLoader());
            pMidlet = (MIDlet)class1.newInstance();
            if(invokeMethod != null)
            {
                LogFrame.println("invokeMethod=" + invokeMethod);
                class1.getMethod(invokeMethod, null).invoke(pMidlet, null);
            }
            (new Thread(new E())).start();
        }
        catch(Exception exception4)
        {
            exception4.printStackTrace();
            LogFrame.dump();
        }
        return;
    }

    public static void syncThreadStart()
    {
        try
        {
            if(!B)
            {
                System.out.println("syncThreadStart: " + Thread.currentThread() + " waiting for startApp() to finish...");
                while(!B) 
                    Thread.sleep(50L);
                System.out.println("ok.");
            }
        }
        catch(Exception exception) { }
    }

    public static MainFrame mainFrame;
    public static MIDlet pMidlet;
    public static Canvas pCanvas;
    static Vector a = new Vector();
    static String b[];
    static String c = "";
    static String d = "";
    public static String midletJar = "";
    public static String midletClassName = null;
    static String e = null;
    static String f = "/devices.xml";
    public static String invokeMethod = null;
    static String g;
    static String h;
    public static String rmsFolder = ".\\";
    static int i = -1;
    static ClassLoader j;
    public static boolean useDefaultPNGLoader;
    public static boolean doja;
    static boolean k = false;
    static boolean l = false;
    static boolean m = true;
    static boolean n = true;
    static Server o;
    public static boolean serverMode = false;
    public static int waitClientCount = 0;
    static Client p;
    public static boolean clientMode = false;
    public static int clientIndex;
    static FrameSync q = new FrameSync();
    static boolean r = true;
    static Properties s;
    static PrintStream t;
    static eC u;
    static Hashtable v = new Hashtable();
    public static boolean bNetworkAvailable = true;
    public static int nFPSLimiter;
    public static int nFPSReal;
    static double w = 1.0D;
    public static int nRemainingRunStepCount = -1;
    public static String systemFontName = "Arial";
    static ResourceManager x;
    static long y;
    static long z;
    static long A;
    static boolean B;

    static 
    {
        nFPSLimiter = 30;
        nFPSReal = nFPSLimiter;
    }

    // Unreferenced inner class emulator/E
    static class E
        implements Runnable
    {

        public void run()
        {
            GLEmulator.pMidlet.invokeStartApp();
            GLEmulator.B = true;
        }

            E()
            {
            }
    }

}