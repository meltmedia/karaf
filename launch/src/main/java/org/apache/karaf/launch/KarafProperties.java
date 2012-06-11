package org.apache.karaf.launch;

import java.io.File;
import java.net.URI;
import java.util.Properties;

public interface KarafProperties {
	public File getBase();

	public File getHome();

	public File getData();

	public File getInstances();
	public String getName();

	public String getOpts();

	public String getOpts(String defaultValue);
	
  public File getConfigPropertiesFile();

  public void setRestart(boolean value);

  public void setRestartClean(boolean value);

  public String getVersion();

  public String getHistory();

  public String getHistory(String defaultValue);

  public String getShellInitScript();

  public String getName(String defaultValue);

  public Boolean getConvertToMavenUrl(Boolean defaultValue);

  int getLockDelay();

  public String getBootstrapLog();
  
  public boolean getUseLock();

  public String getLockClass();

  Properties getProperties();

  public File getEtc();

  public int getDefaultBundleStartlevel();

  String[] getSecurityProviders();

  public int getLockStartLevel();

  public URI getFrameworkBundle();

  public String getFrameworkFactoryClass();

  public String getDefaultRepo();

  public String getBundleLocations();

  public int getShutdownTimeout();

  public String getPidFile();

  public int getDefaultStartLevel();

  public int getShutdownPort();

  public String getShutdownHost();

  public String getPortFile();

  public String getShutdownCommand();

  public void setShutdownPort(int portFromShutdownPortFile);
}
