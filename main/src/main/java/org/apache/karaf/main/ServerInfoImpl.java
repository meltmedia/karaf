/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */


package org.apache.karaf.main;

import java.io.File;
import java.net.URI;

import org.apache.karaf.launch.KarafProperties;
import org.apache.karaf.launch.ServerInfo;

/**
 * @version $Rev: 1209819 $ $Date: 2011-12-03 02:35:52 +0100 (Sa, 03 Dez 2011) $
 */
public class ServerInfoImpl implements ServerInfo {

    private final String[] args;
    private final KarafProperties config;

    public ServerInfoImpl(String[] args, KarafProperties config) {
        this.args = args;
        this.config = config;
    }

    @Override
    public File getHomeDirectory() {
        return config.getHome();
    }

    @Override
    public String resolveHomePath(String filename) {
        return resolveWithBase(config.getHome(), filename).getAbsolutePath();
    }

    @Override
    public File resolveHome(String filename) {
        return resolveWithBase(config.getHome(), filename);
    }

    @Override
    public URI resolveHome(URI uri) {
        return config.getHome().toURI().resolve(uri);
    }

    @Override
    public File getBaseDirectory() {
        return config.getBase();
    }

    @Override
    public String resolveBasePath(String filename) {
        return resolveWithBase(config.getBase(), filename).getAbsolutePath();
    }

    @Override
    public File resolveBase(String filename) {
        return resolveWithBase(config.getBase(), filename);
    }

    @Override
    public URI resolveBase(URI uri) {
        return config.getBase().toURI().resolve(uri);
    }

    @Override
    public File getDataDirectory() {
        return config.getData();
    }

    @Override
    public File getInstancesDirectory() {
        return config.getInstances();
    }

    @Override
    public String[] getArgs() {
        return args.clone();
    }

    private File resolveWithBase(File baseDir, String filename) {
        File file = new File(filename);
        if (file.isAbsolute()) {
            return file;
        }
        return new File(baseDir, filename);
    }

}
