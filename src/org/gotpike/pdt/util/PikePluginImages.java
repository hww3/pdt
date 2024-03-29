package org.gotpike.pdt.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.runtime.*;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.gotpike.pdt.PDTPlugin;
import org.osgi.framework.Bundle;

/**
 * Bundle of most images used by the Perl plug-in.
 * 
 * This class is adapted from org.eclipse.jdt.internal.ui.JavaPluginImages.
 */
public class PikePluginImages
{
    public static final IPath ICONS_PATH = new Path("$nl$/icons"); //$NON-NLS-1$

    private static final String NAME_PREFIX = "org.gotpike.pdt.pikeeditor."; //$NON-NLS-1$
    private static final int NAME_PREFIX_LENGTH = NAME_PREFIX.length();

    private static ImageRegistry fgImageRegistry = null;
    private static HashMap fgAvoidSWTErrorMap = null;

    /*
     * Available cached Images in the Perl plug-in image registry.
     */
    public static final String IMG_ICON_EDITOR = NAME_PREFIX
        + "pike.gif"; //$NON-NLS-1$
     public static final String IMG_ICON_VARIABLE = NAME_PREFIX
        + "field_public_obj.gif"; //$NON-NLS-1$
    public static final String IMG_ICON_CONSTRUCTOR = NAME_PREFIX
        + "constructor.gif"; //$NON-NLS-1$
    public static final String IMG_ICON_SEARCH = NAME_PREFIX
        + "search.gif"; //$NON-NLS-1$
    public static final String IMG_ICON_MARK_OCCURRENCES = NAME_PREFIX
        + "mark_occurrences.gif"; //$NON-NLS-1$
    public static final String IMG_NEW_PROJECT_WIZARD = NAME_PREFIX
        + "new_wizard.gif"; //$NON-NLS-1$
    public static final String IMG_OBJS_ERROR = NAME_PREFIX
        + "error_obj.gif"; //$NON-NLS-1$
    public static final String IMG_OBJS_WARNING = NAME_PREFIX
        + "warning_obj.gif"; //$NON-NLS-1$
    public static final String IMG_OBJS_INFO = NAME_PREFIX
        + "info_obj.gif"; //$NON-NLS-1$
    public static final String IMG_OBJS_TEMPLATE = NAME_PREFIX
        + "template_obj.gif"; //$NON-NLS-1$

	public static final String IMG_ICON_METHOD = NAME_PREFIX
        + "methpub_obj.gif"; //$NON-NLS-1$
	public static final String IMG_ICON_CLASS = NAME_PREFIX
    + "class_default_obj.gif"; //$NON-NLS-1$
	public static final String IMG_ICON_CLASS_INNER = NAME_PREFIX
    + "innerclass_public_obj.gif"; //$NON-NLS-1$
	public static final String IMG_ICON_INHERIT = NAME_PREFIX
    + "inherit_obj.gif"; //$NON-NLS-1$

	public static final String IMG_ICON_CONSTANT = NAME_PREFIX
    + "field_public_obj.gif"; //$NON-NLS-1$

    static
    {
        createManaged("", IMG_ICON_EDITOR);
        createManaged("", IMG_ICON_VARIABLE);
        createManaged("", IMG_ICON_METHOD);
        createManaged("", IMG_ICON_CONSTANT);
        createManaged("", IMG_ICON_CLASS);
        createManaged("", IMG_ICON_CLASS_INNER);
        createManaged("", IMG_ICON_INHERIT);
        createManaged("", IMG_ICON_CONSTRUCTOR);
        createManaged("", IMG_ICON_SEARCH);
        createManaged("", IMG_ICON_MARK_OCCURRENCES);
        createManaged("", IMG_NEW_PROJECT_WIZARD);
        createManaged("", IMG_OBJS_ERROR);
        createManaged("", IMG_OBJS_WARNING);
        createManaged("", IMG_OBJS_INFO);
        createManaged("", IMG_OBJS_TEMPLATE);
    }

    /**
     * Returns the image managed under the given key in this registry.
     * 
     * @param key
     *            the image's key
     * @return the image managed under the given key
     */
    public static Image get(String key)
    {
        return getImageRegistry().get(key);
    }

    /**
     * Returns the image descriptor for the given key in this registry.
     * Might be called in a non-UI thread.
     * 
     * @param key the image's key
     * @return the image descriptor for the given key
     */
    public static ImageDescriptor getDescriptor(String key)
    {
        if (fgImageRegistry == null)
            return (ImageDescriptor) fgAvoidSWTErrorMap.get(key);

        return getImageRegistry().getDescriptor(key);
    }

    /**
     * Helper method to access the image registry from the
     * PerlEditorPlugin class.
     */
    static ImageRegistry getImageRegistry()
    {
        if (fgImageRegistry == null)
        {
            fgImageRegistry = new ImageRegistry();
            for (Iterator iter = fgAvoidSWTErrorMap.keySet().iterator();
                 iter.hasNext();)
            {
                String key = (String) iter.next();
                fgImageRegistry.put(
                    key, (ImageDescriptor) fgAvoidSWTErrorMap.get(key));
            }
            fgAvoidSWTErrorMap = null;
        }
        return fgImageRegistry;
    }

    private static ImageDescriptor createManaged(String prefix, String name)
    {
        return createManaged(prefix, name, name);
    }

    private static ImageDescriptor createManaged(
        String prefix, String name, String key)
    {
        ImageDescriptor result = create(
            prefix, name.substring(NAME_PREFIX_LENGTH), true);

        if (fgAvoidSWTErrorMap == null)
            fgAvoidSWTErrorMap = new HashMap();

        fgAvoidSWTErrorMap.put(key, result);
        return result;
    }

    /**
     * Creates an image descriptor for the given prefix and name in the Perl
     * plug-in bundle. The path can contain variables like $NL$. If no image could be
     * found, <code>useMissingImageDescriptor</code> decides if either the
     * 'missing image descriptor' is returned or <code>null</code>.
     */
    private static ImageDescriptor create(
        String prefix,
        String name,
        boolean useMissingImageDescriptor)
    {
        IPath path = ICONS_PATH.append(prefix).append(name);
        return createImageDescriptor(
            PDTPlugin.getDefault().getBundle(),
            path,
            useMissingImageDescriptor);
    }

    /**
     * Creates an image descriptor for the given path in a bundle. The path can
     * contain variables like $NL$. If no image could be found, <code>useMissingImageDescriptor</code>
     * decides if either the 'missing image descriptor' is returned or <code>null</code>.
     */
    private static ImageDescriptor createImageDescriptor(
        Bundle bundle,
        IPath path,
        boolean useMissingImageDescriptor)
    {
        URL url = Platform.find(bundle, path);
        if (url != null) return ImageDescriptor.createFromURL(url);
        else return useMissingImageDescriptor
            ? ImageDescriptor.getMissingImageDescriptor()
            : null;
    }
}