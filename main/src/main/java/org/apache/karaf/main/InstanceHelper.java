package org.apache.karaf.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.karaf.launch.KarafProperties;
import org.osgi.framework.launch.Framework;

public class InstanceHelper {

    static void updateInstancePid(File karafHome, File karafBase) {
        try {
            String instanceName = System.getProperty("karaf.name");
            String pid = ManagementFactory.getRuntimeMXBean().getName();
            if (pid.indexOf('@') > 0) {
                pid = pid.substring(0, pid.indexOf('@'));
            }
            
            boolean isRoot = karafHome.equals(karafBase);
            
            if (instanceName != null) {
                String storage = System.getProperty("karaf.instances");
                if (storage == null) {
                    throw new Exception("System property 'karaf.instances' is not set. \n" +
                        "This property needs to be set to the full path of the instance.properties file.");
                }
                File storageFile = new File(storage);
                File propertiesFile = new File(storageFile, "instance.properties");
                Properties props = new Properties();
                if (propertiesFile.exists()) {
                    FileInputStream fis = new FileInputStream(propertiesFile);
                    props.load(fis);
                    int count = Integer.parseInt(props.getProperty("count"));
                    for (int i = 0; i < count; i++) {
                        String name = props.getProperty("item." + i + ".name");
                        if (name.equals(instanceName)) {
                            props.setProperty("item." + i + ".pid", pid);
                            FileOutputStream fos = new FileOutputStream(propertiesFile);
                            props.store(fos, null);
                            fis.close();
                            fos.close();
                            return;
                        }
                    }
                    fis.close();
                    if (!isRoot) {
                        throw new Exception("Instance " + instanceName + " not found");
                    } 
                } else if (isRoot) {
                    if (!propertiesFile.getParentFile().exists()) {
                        try {
                            propertiesFile.getParentFile().mkdirs();
                        } catch (SecurityException se) {
                            throw new Exception(se.getMessage());
                        }
                    }
                    props.setProperty("count", "1");
                    props.setProperty("item.0.name", instanceName);
                    props.setProperty("item.0.loc", karafHome.getAbsolutePath());
                    props.setProperty("item.0.pid", pid);
                    props.setProperty("item.0.root", "true");
                    FileOutputStream fos = new FileOutputStream(propertiesFile);
                    props.store(fos, null);
                    fos.close();
                }
            }
        } catch (Exception e) {
            System.err.println("Unable to update instance pid: " + e.getMessage());
        }
    }

    private static void writePid(String pidFile) {
        try {
            if (pidFile != null) {
                RuntimeMXBean rtb = ManagementFactory.getRuntimeMXBean();
                String processName = rtb.getName();
                Pattern pattern = Pattern.compile("^([0-9]+)@.+$", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(processName);
                if (matcher.matches()) {
                    int pid = Integer.parseInt(matcher.group(1));
                    Writer w = new OutputStreamWriter(new FileOutputStream(pidFile));
                    w.write(Integer.toString(pid));
                    w.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void setupShutdown(KarafProperties config, Framework framework) {
        writePid(config.getPidFile());
        try {
            int port = config.getShutdownPort();
            String host = config.getShutdownHost();
            String portFile = config.getPortFile();
            final String shutdown = config.getShutdownCommand();
            if (port >= 0) {
                ServerSocket shutdownSocket = new ServerSocket(port, 1, InetAddress.getByName(host));
                if (port == 0) {
                    port = shutdownSocket.getLocalPort();
                }
                if (portFile != null) {
                    Writer w = new OutputStreamWriter(new FileOutputStream(portFile));
                    w.write(Integer.toString(port));
                    w.close();
                }
                Thread thread = new ShutdownSocketThread(shutdown, shutdownSocket, framework);
                thread.setDaemon(true);
                thread.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
