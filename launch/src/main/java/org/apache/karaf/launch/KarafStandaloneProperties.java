package org.apache.karaf.launch;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.karaf.launch.PropertiesLoader;
import org.apache.karaf.launch.lock.SimpleFileLock;
import org.apache.karaf.launch.util.Utils;
import org.osgi.framework.Constants;

public class KarafStandaloneProperties implements KarafProperties {
  
  /**
   * The system property for specifying the Karaf home directory.  The home directory
   * hold the binary install of Karaf.
   */
  public static final String PROP_KARAF_HOME = "karaf.home";
  /**
   * The environment variable for specifying the Karaf home directory.  The home directory
   * hold the binary install of Karaf.
   */
  public static final String ENV_KARAF_HOME = "KARAF_HOME";
  /**
   * The system property for specifying the Karaf base directory.  The base directory
   * holds the configuration and data for a Karaf instance.
   */
  public static final String PROP_KARAF_BASE = "karaf.base";
  /**
   * The environment variable for specifying the Karaf base directory.  The base directory
   * holds the configuration and data for a Karaf instance.
   */
  public static final String ENV_KARAF_BASE = "KARAF_BASE";
  /**
   * The system property for specifying the Karaf data directory. The data directory
   * holds the bundles data and cache for a Karaf instance.
   */
  public static final String PROP_KARAF_DATA = "karaf.data";
  /**
   * The environment variable for specifying the Karaf data directory. The data directory
   * holds the bundles data and cache for a Karaf instance.
   */
  public static final String ENV_KARAF_DATA = "KARAF_DATA";
  /**
   * The system property for specifying the Karaf data directory. The data directory
   * holds the bundles data and cache for a Karaf instance.
   */
  public static final String PROP_KARAF_INSTANCES = "karaf.instances";
  /**
   * The system property for specifying the Karaf data directory. The data directory
   * holds the bundles data and cache for a Karaf instance.
   */
  public static final String ENV_KARAF_INSTANCES = "KARAF_INSTANCES";
  /**
   * The system property for hosting the current Karaf version.
   */
  public static final String PROP_KARAF_VERSION = "karaf.version";
  /**
   * The default name used for the configuration properties file.
   */
  private static final String CONFIG_PROPERTIES_FILE_NAME = "config.properties";
  /**
   * The default name used for the system properties file.
   */
  public static final String SYSTEM_PROPERTIES_FILE_NAME = "system.properties";

  /**
   * Config property which identifies directories which contain bundles to be loaded by SMX
   */
  private static final String BUNDLE_LOCATIONS = "bundle.locations";
  
  /**
   * The lock implementation
   */
  private static final String PROPERTY_LOCK_CLASS = "karaf.lock.class";

  private static final String PROPERTY_LOCK_DELAY = "karaf.lock.delay";

  private static final String PROPERTY_LOCK_LEVEL = "karaf.lock.level";

  private static final String DEFAULT_REPO = "karaf.default.repository";
  
  private static final String KARAF_FRAMEWORK = "karaf.framework";

  private static final String KARAF_FRAMEWORK_FACTORY = "karaf.framework.factory";

  private static final String KARAF_SHUTDOWN_TIMEOUT = "karaf.shutdown.timeout";

  private static final String KARAF_SHUTDOWN_PORT = "karaf.shutdown.port";

  private static final String KARAF_SHUTDOWN_HOST = "karaf.shutdown.host";

  private static final String KARAF_SHUTDOWN_PORT_FILE = "karaf.shutdown.port.file";

  private static final String KARAF_SHUTDOWN_COMMAND = "karaf.shutdown.command";

  private static final String KARAF_SHUTDOWN_PID_FILE = "karaf.shutdown.pid.file";

  private static final String DEFAULT_SHUTDOWN_COMMAND = "SHUTDOWN";

  private static final String PROPERTY_LOCK_CLASS_DEFAULT = SimpleFileLock.class.getName();

  private static final String SECURITY_PROVIDERS = "org.apache.karaf.security.providers";
  
  private static final String KARAF_NAME = "karaf.name";
  
  private static final String KARAF_OPTS = "karaf.opts";

  /**
   * If a lock should be used before starting the runtime
   */
  private static final String PROPERTY_USE_LOCK = "karaf.lock";
  private static final String KARAF_RESTART = "karaf.restart";
  private static final String KARAF_HISTORY = "karaf.history";
  private static final String KARAF_RESTART_CLEAN = "karaf.restart.clean";
  private static final String KARAF_SHELL_INIT_SCRIPT = "karaf.shell.init.script";
  private static final String KARAF_CONVERT_TO_MAVEN_URL = null; /* TODO: define maven URL options. */
  private static final String KARAF_BOOTSTRAP_LOG = "karaf.bootstrap.log";

  
  File karafHome;
  File karafBase;
  File karafData;
  File karafInstances;
  
  Properties props;
  String[] securityProviders;
  int defaultStartLevel = 100;
  int lockStartLevel = 1;
  int lockDelay = 1000;
  int shutdownTimeout = 5 * 60 * 1000;
  boolean useLock;
  String lockClass;
  String frameworkFactoryClass;
  URI frameworkBundle;
  String defaultRepo;
  String bundleLocations;
  int defaultBundleStartlevel;
  String pidFile;
  int shutdownPort;
  String shutdownHost;
  String portFile;
  String shutdownCommand;
  String includes;
  String optionals;
  File etcFolder;
  String karafName;
  String karafOpts;
  Boolean restart;
  Boolean restartClean;
  String version;
  String history;
  String shellInitScript;
  Boolean convertToMavenUrl;
  private String bootstrapLog;

  public KarafStandaloneProperties() throws Exception {
    this.karafHome = Utils.getKarafHome(KarafStandaloneProperties.class, PROP_KARAF_HOME, ENV_KARAF_HOME);
    this.karafBase = Utils.getKarafDirectory(PROP_KARAF_BASE, ENV_KARAF_BASE, karafHome, false, true);
    this.karafData = Utils.getKarafDirectory(PROP_KARAF_DATA, ENV_KARAF_DATA, new File(karafBase, "data"), true, true);
    
    if (Boolean.getBoolean("karaf.restart.clean")) {
        Utils.deleteDirectory(this.karafData);
        this.karafData = Utils.getKarafDirectory(PROP_KARAF_DATA, ENV_KARAF_DATA, new File(karafBase, "data"), true, true);
    }
    
    this.karafInstances = Utils.getKarafDirectory(PROP_KARAF_INSTANCES, ENV_KARAF_INSTANCES, new File(karafHome, "instances"), false, false);

    Package p = Package.getPackage("org.apache.karaf.main");
    if (p != null && p.getImplementationVersion() != null)
        System.setProperty(PROP_KARAF_VERSION, p.getImplementationVersion());
    System.setProperty(PROP_KARAF_HOME, karafHome.getPath());
    System.setProperty(PROP_KARAF_BASE, karafBase.getPath());
    System.setProperty(PROP_KARAF_DATA, karafData.getPath());
    System.setProperty(PROP_KARAF_INSTANCES, karafInstances.getPath());

    this.etcFolder = new File(karafBase, "etc");
    if (!etcFolder.exists()) {
        throw new FileNotFoundException("etc folder not found: " + etcFolder.getAbsolutePath());
    }
    PropertiesLoader.loadSystemProperties(new File(etcFolder, SYSTEM_PROPERTIES_FILE_NAME));

    File file = new File(etcFolder, CONFIG_PROPERTIES_FILE_NAME);
    this.props = PropertiesLoader.loadConfigProperties(file);
    
    String prop = props.getProperty(SECURITY_PROVIDERS);
    this.securityProviders = (prop != null) ? prop.split(",") : new String[] {};
    this.defaultStartLevel = getIntProperty(Constants.FRAMEWORK_BEGINNING_STARTLEVEL, 0);
    this.lockStartLevel = getIntProperty(PROPERTY_LOCK_LEVEL, lockStartLevel);                
    this.lockDelay = getIntProperty(PROPERTY_LOCK_DELAY, lockDelay);
    this.props.setProperty(Constants.FRAMEWORK_BEGINNING_STARTLEVEL, Integer.toString(lockStartLevel));
    this.shutdownTimeout = getIntProperty(KARAF_SHUTDOWN_TIMEOUT, shutdownTimeout);
    this.useLock = Boolean.parseBoolean(props.getProperty(PROPERTY_USE_LOCK, "true"));
    this.lockClass = props.getProperty(PROPERTY_LOCK_CLASS, PROPERTY_LOCK_CLASS_DEFAULT);
    initFrameworkStorage(karafData);
    this.frameworkFactoryClass = props.getProperty(KARAF_FRAMEWORK_FACTORY);
    this.frameworkBundle = getFramework();
    this.defaultRepo = System.getProperty(DEFAULT_REPO, "system");
    this.bundleLocations = props.getProperty(BUNDLE_LOCATIONS);
    this.defaultBundleStartlevel = getDefaultBundleStartLevel(60);
    this.pidFile = props.getProperty(KARAF_SHUTDOWN_PID_FILE);
    this.shutdownPort = Integer.parseInt(props.getProperty(KARAF_SHUTDOWN_PORT, "0"));
    this.shutdownHost = props.getProperty(KARAF_SHUTDOWN_HOST, "localhost");
    this.portFile = props.getProperty(KARAF_SHUTDOWN_PORT_FILE);
    this.shutdownCommand = props.getProperty(KARAF_SHUTDOWN_COMMAND, DEFAULT_SHUTDOWN_COMMAND);
    this.karafName = props.getProperty(KARAF_NAME);
    this.karafOpts = props.getProperty(KARAF_OPTS);
    this.restart = getBooleanProperty(KARAF_RESTART, Boolean.FALSE);
    this.restartClean = getBooleanProperty(KARAF_RESTART_CLEAN, Boolean.FALSE);
    this.version = props.getProperty(PROP_KARAF_VERSION);
    this.history = props.getProperty(KARAF_HISTORY);
    this.shellInitScript = props.getProperty(KARAF_SHELL_INIT_SCRIPT);
    this.convertToMavenUrl = getBooleanProperty(KARAF_CONVERT_TO_MAVEN_URL, Boolean.FALSE);
    this.bootstrapLog = props.getProperty(KARAF_BOOTSTRAP_LOG);
}
  private String getProperyOrFail(String propertyName) {
    String value = props.getProperty(propertyName);
    if (value == null) {
        throw new IllegalArgumentException("Property " + propertyName + " must be set in the etc/" + CONFIG_PROPERTIES_FILE_NAME + " configuration file");
    }
    return value;
}

private URI getFramework() throws URISyntaxException {
    String framework = getProperyOrFail(KARAF_FRAMEWORK);
    String frameworkBundleUri = getProperyOrFail(KARAF_FRAMEWORK + "." + framework);
    return new URI(frameworkBundleUri);
}

private void initFrameworkStorage(File karafData) throws Exception {
    String frameworkStoragePath = props.getProperty(Constants.FRAMEWORK_STORAGE);
    if (frameworkStoragePath == null) {
        File storage = new File(karafData.getPath(), "cache");
        try {
            storage.mkdirs();
        } catch (SecurityException se) {
            throw new Exception(se.getMessage()); 
        }
        props.setProperty(Constants.FRAMEWORK_STORAGE, storage.getAbsolutePath());
    }
}

private int getDefaultBundleStartLevel(int ibsl) {
    try {
        String str = props.getProperty("karaf.startlevel.bundle");
        if (str != null) {
            ibsl = Integer.parseInt(str);
        }
    } catch (Throwable t) {
    }
    return ibsl;
}

  private int getIntProperty( String name, int defaultValue ) {
    try {
      String value = props.getProperty(name);
      return value != null ? Integer.parseInt(value) : defaultValue;
    }
    catch( Exception e ) {
      return defaultValue;
    }
  }

  private Boolean getBooleanProperty(String name, Boolean defaultValue) {
    try {
    return props.containsKey(name) ? Boolean.valueOf(props.getProperty(name)) : defaultValue;
    }
    catch( Exception e ) {
      System.out.println("Exception thrown for "+name+".  Using default value of "+defaultValue+".");
      return defaultValue;
    }
  }


  @Override
  public File getBase() {
    return karafBase;
  }

  @Override
  public File getHome() {
    return karafHome;
  }

  @Override
  public File getData() {
    return karafData;
  }

  @Override
  public File getInstances() {
    return karafInstances;
  }
  
  public File getEtc() {
    return etcFolder;
  }

  @Override
  public String getName() {
    return karafName;
  }

  @Override
  public String getOpts() {
    return karafOpts;
  }

  @Override
  public String getOpts(String defaultValue) {
    return karafOpts != null ? karafOpts : defaultValue;
  }

  @Override
  public File getConfigPropertiesFile() {
    throw new UnsupportedOperationException("Config properties file not yet supported.");
  }

  @Override
  public void setRestart(boolean value) {
    this.restart = value;
  }

  @Override
  public void setRestartClean(boolean value) {
    this.restartClean = value;
  }

  @Override
  public String getVersion() {
    return version;
  }

  @Override
  public String getHistory() {
    return history;
  }

  @Override
  public String getHistory(String defaultValue) {
    return history != null ? history : defaultValue;
  }

  @Override
  public String getShellInitScript() {
    return shellInitScript;
  }

  @Override
  public String getName(String defaultValue) {
    return karafName != null ? karafName : defaultValue;
  }

  @Override
  public Boolean getConvertToMavenUrl(Boolean defaultValue) {
    return convertToMavenUrl != null ? convertToMavenUrl : defaultValue;
  }
  
  @Override
  public int getLockDelay() {
    return lockDelay;
  }
  @Override
  public String getBootstrapLog() {
    return bootstrapLog;
  }
  
  public boolean getUseLock()
  {
    return this.useLock;
  }
  @Override
  public String getLockClass() {
    return lockClass;
  }
  
  @Override
  public Properties getProperties()
  {
    return props;
  }
  @Override
  public int getDefaultBundleStartlevel() {
    return defaultBundleStartlevel;
  }

  @Override
  public String[] getSecurityProviders() {
    return securityProviders;
  }
  @Override
  public int getLockStartLevel() {
    return lockStartLevel;
  }
  @Override
  public URI getFrameworkBundle() {
    return frameworkBundle;
  }
  @Override
  public String getFrameworkFactoryClass() {
    return frameworkFactoryClass;
  }
  @Override
  public String getDefaultRepo() {
    return defaultRepo;
  }
  @Override
  public String getBundleLocations() {
    return bundleLocations;
  }
  @Override
  public int getShutdownTimeout() {
    return shutdownTimeout;
  }
  @Override
  public String getPidFile() {
    return pidFile;
  }
  @Override
  public int getDefaultStartLevel() {
    return defaultStartLevel;
  }
  @Override
  public int getShutdownPort() {
    return shutdownPort;
  }
  @Override
  public String getShutdownHost() {
    return shutdownHost;
  }
  @Override
  public String getPortFile() {
    return portFile;
  }
  @Override
  public String getShutdownCommand() {
    return shutdownCommand;
  }
  @Override
  public void setShutdownPort(int shutdownPort) {
    this.shutdownPort = shutdownPort;
  }
}
