
package org.eamonn.plugin;

import org.isatools.isacreator.plugins.host.service.PluginMenu;
import java.util.Hashtable;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private BundleContext context = null;

    public void start(BundleContext context) {
        this.context = context;
        Hashtable dict = new Hashtable();
        context.registerService(
                PluginMenu.class.getName(), new SubmissionStats(), dict);
    }

    public void stop(BundleContext context) {
    }
}
