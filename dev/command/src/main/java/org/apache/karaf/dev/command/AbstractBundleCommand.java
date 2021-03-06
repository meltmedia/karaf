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
package org.apache.karaf.dev.command;

import org.apache.karaf.shell.commands.Argument;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * Base class for a dev: command that takes a bundle id as an argument
 *
 * It also provides convient access to the PackageAdmin service
 */
public abstract class AbstractBundleCommand extends DevCommandSupport {

    @Argument(index = 0, name = "id", description = "The bundle ID", required = true)
    Long id;
    
    protected BundleContext bundleContext;

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @Override
    protected Object doExecute() throws Exception {
        Bundle bundle = bundleContext.getBundle(id);
        if (bundle == null) {
            System.err.println("Bundle ID " + id + " is invalid");
            return null;
        }

        doExecute(bundle);
        
        return null;
    }

    protected abstract void doExecute(Bundle bundle) throws Exception;

}
