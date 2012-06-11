/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.instance.core.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Properties;

import junit.framework.TestCase;

import org.apache.karaf.instance.core.Instance;
import org.apache.karaf.instance.core.InstanceSettings;
import org.apache.karaf.instance.core.internal.InstanceServiceImpl;
import org.apache.karaf.launch.KarafStandaloneProperties;

public class InstanceServiceImplTest extends TestCase {

    public void testHandleFeatures() throws Exception {
        InstanceServiceImpl as = new InstanceServiceImpl();
        
        File f = File.createTempFile(getName(), ".test");
        try {
            Properties p = new Properties();
            p.put("featuresBoot", "abc,def ");
            p.put("featuresRepositories", "somescheme://xyz");
            OutputStream os = new FileOutputStream(f);
            try {
                p.store(os, "Test comment");
            } finally {
                os.close();
            }
            
            InstanceSettings s = new InstanceSettings(8122, 1122, 44444, null, null, null, Arrays.asList("test"));
            as.addFeaturesFromSettings(f, s);
            
            Properties p2 = new Properties();
            InputStream is = new FileInputStream(f);
            try {
                p2.load(is);
            } finally {
                is.close();
            }
            assertEquals(2, p2.size());
            assertEquals("abc,def,ssh,test", p2.get("featuresBoot"));
            assertEquals("somescheme://xyz", p2.get("featuresRepositories"));
        } finally {
            f.delete();
        }
    }

    /**
     * Ensure the instance:create generates all the required configuration files
     * //TODO: fix this test so it can run in an IDE
     */
    public void testConfigurationFiles() throws Exception {
        System.setProperty("karaf.base", new File("target/test-classes/").getAbsolutePath());
        InstanceServiceImpl service = new InstanceServiceImpl();
        service.setConfiguration(new KarafStandaloneProperties());
        service.setStorageLocation(new File("target/instances/" + System.currentTimeMillis()));
        
        InstanceSettings settings = new InstanceSettings(8122, 1122, 44444, getName(), null, null, null);
        Instance instance = service.createInstance(getName(), settings, true);

        assertFileExists(instance.getLocation(), "etc/config.properties");
        assertFileExists(instance.getLocation(), "etc/users.properties");
        assertFileExists(instance.getLocation(), "etc/startup.properties");

        assertFileExists(instance.getLocation(), "etc/java.util.logging.properties");
        assertFileExists(instance.getLocation(), "etc/org.apache.karaf.features.cfg");
        assertFileExists(instance.getLocation(), "etc/org.apache.felix.fileinstall-deploy.cfg");
        assertFileExists(instance.getLocation(), "etc/org.apache.karaf.log.cfg");
        assertFileExists(instance.getLocation(), "etc/org.apache.karaf.management.cfg");
        assertFileExists(instance.getLocation(), "etc/org.ops4j.pax.logging.cfg");
        assertFileExists(instance.getLocation(), "etc/org.ops4j.pax.url.mvn.cfg");
    }

    /**
     * <p>
     * Test the renaming of an existing instance.
     * </p>
     */
    public void testRenameInstance() throws Exception {
        System.setProperty("karaf.base", new File("target/test-classes/").getAbsolutePath());
        InstanceServiceImpl service = new InstanceServiceImpl();
        service.setConfiguration(new KarafStandaloneProperties());
        service.setStorageLocation(new File("target/instances/" + System.currentTimeMillis()));

        InstanceSettings settings = new InstanceSettings(8122, 1122, 44444, getName(), null, null, null);
        service.createInstance(getName(), settings, true);

        service.renameInstance(getName(), getName() + "b", true);
        assertNotNull(service.getInstance(getName() + "b"));
    }

    private void assertFileExists(String path, String name) throws IOException {
        File file = new File(path, name);
        assertTrue("Expected " + file.getCanonicalPath() + " to exist",
                   file.exists());
    }   
}
