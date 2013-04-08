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
package org.isatools.plugins.metabolights.assignments;

import org.apache.log4j.Logger;
import org.isatools.isacreator.plugins.host.service.PluginSpreadsheetWidget;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import java.util.Hashtable;

public class Activator implements BundleActivator {

    private BundleContext context = null;
    private static Logger logger = Logger.getLogger(Activator.class);

    public void start(BundleContext context) {
        this.context = context;

        Hashtable dict = new Hashtable();
        logger.info("Activating the ISAcreator metabolite bundle....");
        context.registerService(
                PluginSpreadsheetWidget.class.getName(), new MetabolomicsResultEditor(), dict);
    }


    public void stop(BundleContext context) {
    }
}
