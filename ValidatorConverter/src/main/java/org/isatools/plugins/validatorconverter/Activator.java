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
package org.isatools.plugins.validatorconverter;

import org.isatools.isacreator.common.UIHelper;
import org.isatools.isacreator.plugins.host.service.PluginMenu;

import java.util.Hashtable;

import org.jdesktop.fuse.ResourceInjector;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import javax.swing.*;

public class Activator implements BundleActivator {

    static {

        UIManager.put("MenuItemUI", "org.isatools.isacreator.common.CustomMenuItemUI");
        UIManager.put("MenuItem.selectionBackground", UIHelper.LIGHT_GREEN_COLOR);

        ResourceInjector.addModule("org.jdesktop.fuse.swing.SwingModule");

        ResourceInjector.get("validator-package.style").load(
                Activator.class.getResource("/dependency-injections/validator-package.properties"));
    }


    private BundleContext context = null;


    public void start(BundleContext context) {
        this.context = context;
        Hashtable dict = new Hashtable();
        context.registerService(
                PluginMenu.class.getName(), new Validator(), dict);
        context.registerService(
                PluginMenu.class.getName(), new Converter(), dict);
    }


    public void stop(BundleContext context) {
    }
}
