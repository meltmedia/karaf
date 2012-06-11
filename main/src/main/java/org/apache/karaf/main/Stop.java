/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.karaf.main;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.karaf.launch.KarafProperties;
import org.apache.karaf.launch.KarafStandaloneProperties;

/**
 * Main class used to stop the root Karaf instance
 */
public class Stop {

    /**
     * Sends the shutdown command to the running karaf instance. Uses either a shut down port configured in config.properties or
     * the port from the shutdown port file.
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        KarafProperties config = new KarafStandaloneProperties();
        if (config.getShutdownPort() == 0 && config.getPortFile() != null) {
            config.setShutdownPort(getPortFromShutdownPortFile(config.getPortFile()));
        }
        if (config.getShutdownPort() > 0) {
            Socket s = new Socket(config.getShutdownHost(), config.getShutdownPort());
            s.getOutputStream().write(config.getShutdownCommand().getBytes());
            s.close();
        } else {
            System.err.println("Unable to find port...");
        }

    }

    private static int getPortFromShutdownPortFile(String portFile) throws FileNotFoundException, IOException {
        int port;
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(portFile)));
        String portStr = r.readLine();
        port = Integer.parseInt(portStr);
        r.close();
        return port;
    }
}
