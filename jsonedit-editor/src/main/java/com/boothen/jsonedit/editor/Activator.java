package com.boothen.jsonedit.editor;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.boothen.jsonedit.log.JsonLog;
import com.boothen.jsonedit.outline.node.JsonTreeNode;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

    /**
     * The plug-in ID
     */
    public static final String PLUGIN_ID = "jsonedit-editor"; //$NON-NLS-1$

    /**
     * The shared instance
     */
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
        JsonLog.getInstance(PLUGIN_ID, super.getLog());
        if (PlatformUI.isWorkbenchRunning() || Display.getCurrent() != null) {
            getImageRegistry();
        }
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    @Override
    protected void initializeImageRegistry(ImageRegistry reg) {
        JsonTreeNode.initializeImageRegistry(reg);
        super.initializeImageRegistry(reg);
    }
}
