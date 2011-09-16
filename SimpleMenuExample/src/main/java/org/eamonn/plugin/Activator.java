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
package org.eamonn.plugin;

import org.isatools.isacreator.plugins.host.service.PluginMenu;
import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * This class implements a simple bundle activator for the circle
 * <tt>SimpleShape</tt> service. This activator simply creates an instance
 * of the circle service object and registers it with the service registry
 * along with the service properties indicating the service's name and icon.
 **/
public class Activator implements BundleActivator {

    private BundleContext context = null;

    /**
     * Implements the <tt>BundleActivator.start()</tt> method, which
     * registers the circle <tt>SimpleShape</tt> service.
     * @param context The context for the bundle.
     **/
    public void start(BundleContext context) {
        this.context = context;
        Hashtable dict = new Hashtable();
        context.registerService(
                PluginMenu.class.getName(), new SubmissionStats(), dict);
    }

    /**
     * Implements the <tt>BundleActivator.start()</tt> method, which
     * does nothing.
     * @param context The context for the bundle.
     **/
    public void stop(BundleContext context) {
    }
}
