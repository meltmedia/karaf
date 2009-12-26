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
package org.apache.felix.dm.impl;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.felix.dm.resources.Resource;
import org.apache.felix.dm.service.Service;

public class ResourceAdapterImpl extends AbstractDecorator {
    private volatile Service m_service;
    private final String m_resourceFilter;
    private final Object m_adapterImplementation;
    private final Object m_adapterInterface;
    private final Dictionary m_adapterProperties;
    private final boolean m_propagate;

    public ResourceAdapterImpl(String resourceFilter, Object adapterImplementation, String adapterInterface, Dictionary adapterProperties, boolean propagate) {
        m_resourceFilter = resourceFilter;
        m_adapterImplementation = adapterImplementation;
        m_adapterInterface = adapterInterface;
        m_adapterProperties = adapterProperties;
        m_propagate = propagate;
    }

    public ResourceAdapterImpl(String resourceFilter, Object adapterImplementation, String[] adapterInterfaces, Dictionary adapterProperties, boolean propagate) {
        m_resourceFilter = resourceFilter;
        m_adapterImplementation = adapterImplementation;
        m_adapterInterface = adapterInterfaces;
        m_adapterProperties = adapterProperties;
        m_propagate = propagate;
    }	    

    public Service createService(Object[] properties) {
        Resource resource = (Resource) properties[0]; 
        Properties props = new Properties();
        if (m_adapterProperties != null) {
            Enumeration e = m_adapterProperties.keys();
            while (e.hasMoreElements()) {
                Object key = e.nextElement();
                props.put(key, m_adapterProperties.get(key));
            }
        }
        List dependencies = m_service.getDependencies();
        // the first dependency is always the dependency on the resource, which
        // will be replaced with a more specific dependency below
        dependencies.remove(0);
        if (m_adapterInterface instanceof String) {
            return m_manager.createService()
                .setInterface((String) m_adapterInterface, props)
                .setImplementation(m_adapterImplementation)
                .add(dependencies)
                .add(m_manager.createResourceDependency()
                    .setResource(resource)
                    .setPropagate(m_propagate)
                    .setRequired(true)
                );
        }
        else {
            return m_manager.createService()
                .setInterface((String[]) m_adapterInterface, props)
                .setImplementation(m_adapterImplementation)
                .add(dependencies)
                .add(m_manager.createResourceDependency()
                    .setResource(resource)
                    .setPropagate(m_propagate)
                    .setRequired(true)
                );
        }
    }
}