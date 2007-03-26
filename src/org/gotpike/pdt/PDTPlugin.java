package org.gotpike.pdt;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.widgets.Display;
import org.eclipse.team.core.IFileTypeInfo;
import org.eclipse.team.core.Team;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.*;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.gotpike.pdt.PDTPlugin;
import org.gotpike.pdt.editors.PikeDocumentProvider;
import org.gotpike.pdt.preferences.CodeAssistPreferences;
import org.gotpike.pdt.preferences.MarkOccurrencesPreferences;
import org.gotpike.pdt.preferences.PreferenceConstants;
import org.gotpike.pdt.preferences.SourceFormatterPreferences;
import org.gotpike.pdt.preferences.TaskTagPreferences;
import org.gotpike.pdt.util.PikeColorProvider;
import org.gotpike.pdt.util.PikeExecutor;
import org.osgi.framework.BundleContext;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;


import java.io.File;
import java.util.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class PDTPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static PDTPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;
	private IDocumentProvider fDocumentProvider;
	
	public static final String PIKE_EXECUTABLE_PREFERENCE = "PIKE_EXECUTABLE";

	private static final String PIKE_EXECUTABLE_DEFAULT = "pike";

	public static final String WEB_BROWSER_PREFERENCE = "WEB_BROWSER";

	private static final String WEB_BROWSER_DEFAULT = "http://";

	public static final String WARNINGS_PREFERENCE = "SHOW_WARNINGS";

    public static final String DEBUG_CONSOLE_PREFERENCE = "ENABLE_DEBUG_CONSOLE";

    public static final String SUSPEND_AT_FIRST_PREFERENCE = "SUSPEND_AT_FIRST_CONSOLE";

	private static final boolean WARNINGS_DEFAULT = true;

	public static final String SYNTAX_VALIDATION_PREFERENCE = "SYNTAX_VALIDATION_PREFERENCE";
	public static final boolean SYNTAX_VALIDATION_PREFERENCE_DEFAULT = true;

	public static final String SYNTAX_VALIDATION_INTERVAL_PREFERENCE = "SYNTAX_VALIDATION_IDLE_INTERVAL";

	public static final int SYNTAX_VALIDATION_INTERVAL_DEFAULT = 400;
	
	private PikeColorProvider colorProvider = new PikeColorProvider();
	private boolean requirePikeCheckPassed;
	private boolean requirePikeErrorDisplayed;

	/**
	 * The constructor.
	 */
	public PDTPlugin() {
		super();
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.gotpike.pdt.PDTPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		
		// Set team file extensions
		String[] pikeTypes = { "pike", "pmod" };
		IFileTypeInfo[] fileTypes = Team.getAllTypes();

		int newTypesLength = fileTypes.length + pikeTypes.length;
		String[] extensions = new String[newTypesLength];
		int[] types = new int[newTypesLength];

		int i;
		for (i = 0; i < fileTypes.length; i++) {
			extensions[i] = fileTypes[i].getExtension();
			types[i] = fileTypes[i].getType();
		}

		// Add Perl extensions to the list as ASCII
		for (; i < newTypesLength; i++) {
			extensions[i] = pikeTypes[i - fileTypes.length];
			types[i] = Team.TEXT;
		}

		Team.setAllTypes(extensions, types);
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static PDTPlugin getDefault() {
		return plugin;
	}
	
	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}


	public static IWorkbenchWindow getWorkbenchWindow() {
		IWorkbenchWindow window = getDefault().getWorkbench()
				.getActiveWorkbenchWindow();
		if (window == null)
			window = getDefault().getWorkbench().getWorkbenchWindows()[0];
		return window;
	}


	/**
	 * Returns the string from the plugin's resource bundle,
	 * or 'key' if not found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = PDTPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

    public static String getUniqueIdentifier()
    {
        PDTPlugin plugin = getDefault();
        return plugin != null ? plugin.getBundle().getSymbolicName() : "org.gotpike.pdt";
    }
    
	public static String getPluginId() {
        PDTPlugin plugin = getDefault();
		return plugin != null
            ? plugin.getBundle().getSymbolicName()
            : "org.gotpike.pdt";
	}

	public synchronized IDocumentProvider getDocumentProvider() {

		if (fDocumentProvider == null)
			fDocumentProvider = new PikeDocumentProvider();
		return fDocumentProvider;
	}

    /**
     * Returns a color with the requested RGB value.
     */
    public Color getColor(RGB rgb)
    {
        return colorProvider.getColor(rgb);
    }
	
    /**
     * Returns a color represented by the given preference setting.
     */
    public Color getColor(String preferenceKey)
    {
        return getColor(
            PreferenceConverter.getColor(getPreferenceStore(), preferenceKey)
            );
    }
    
	/**
	 * Initializes a preference store with default preference values for this
	 * plug-in.
	 *
	 * @param store
	 *            the preference store to fill
	 */
	protected void initializeDefaultPreferences(IPreferenceStore store) {
		store.setDefault(PIKE_EXECUTABLE_PREFERENCE, PIKE_EXECUTABLE_DEFAULT);
		PreferenceConstants.initializeDefaultValues(store);
		store.setDefault(SYNTAX_VALIDATION_INTERVAL_PREFERENCE,
				SYNTAX_VALIDATION_INTERVAL_DEFAULT);
        store.setDefault(
            PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE,
            true);
        store.setDefault(SUSPEND_AT_FIRST_PREFERENCE, "1");
		SourceFormatterPreferences.initializeDefaultValues(store);
		CodeAssistPreferences.initializeDefaultValues(store);
		TaskTagPreferences.initializeDefaults(store);
		MarkOccurrencesPreferences.initializeDefaultValues(store);

        System.setProperty(PreferenceConstants.SOURCE_CRITIC_ENABLED,
                store.getString(PreferenceConstants.SOURCE_CRITIC_ENABLED));
	}

	public String getExecutablePreference() {
		return getPreferenceStore().getString(PIKE_EXECUTABLE_PREFERENCE);
	}

	public String getDefaultExecutablePreference() {
		return PIKE_EXECUTABLE_DEFAULT;
	}

	public void setExecutablePreference(String value) {

		getPreferenceStore().setValue(PIKE_EXECUTABLE_PREFERENCE, value);
        requirePikeErrorDisplayed = false;
        checkForPikeInterpreter(true);
	}
	
	public boolean getDefaultWarningsPreference() {
		return WARNINGS_DEFAULT;
	}
	
	public boolean getWarningsPreference() {
		String value = getPreferenceStore().getString(WARNINGS_PREFERENCE);

		return value.equals("1") ? true : false;
	}
	
	public void setWarningsPreference(boolean value) {
		getPreferenceStore().setValue(WARNINGS_PREFERENCE,
				value == true ? "1" : "0");
	}
	
	public String getWebBrowserPreference() {
		return getPreferenceStore().getString(WEB_BROWSER_PREFERENCE);
	}

	public String getDefaultWebBrowserPreference() {
		return WEB_BROWSER_DEFAULT;
	}

	public void setWebBrowserPreference(String value) {

		getPreferenceStore().setValue(WEB_BROWSER_PREFERENCE, value);
	}

	public boolean getSyntaxValidationPreference() {
		String value = getPreferenceStore().getString(SYNTAX_VALIDATION_PREFERENCE);

		return value.equals("1") ? true : false;
	}

	public boolean getDefaultSyntaxValidationPreference() {
		return SYNTAX_VALIDATION_PREFERENCE_DEFAULT;
	}

	public void setSyntaxValidationPreference(boolean value) {
		getPreferenceStore().setValue(SYNTAX_VALIDATION_PREFERENCE,
				value == true ? "1" : "0");
	}

	
    /**
     * @return false if no valid Perl interpreter has been available in
     *         Preferences since the plug-in's activation;
     *         true otherwise
     */
    public boolean hasPikeInterpreter()
    {
        return requirePikeCheckPassed;
    }

    /**
     * Same as {@link #hasPerlInterpreter}, but displays an error dialog
     * if false is returned.
     *
     * @param interactive
     *        true, if the check is performed in context of a user-requested
     *        action, false if the check is performed in context of a background
     *        operation
     */
    public boolean requirePikeInterpreter(boolean interactive)
    {
        if (!requirePikeCheckPassed) checkForPikeInterpreter(interactive);
        return requirePikeCheckPassed;
    }
    
    /**
     * Checks that a valid Pike interpreter is specified in Preferences
     * and updates the requirePikeCheckPassed flag. Displays an error dialog
     * if the check does not pass (but only once for background ops,
     * until Preferences are updated).
     */
    private void checkForPikeInterpreter(boolean interactive)
    {
        final String ERROR_TITLE = "Missing Perl interpreter";
        final String ERROR_MSG =
            "To operate correctly, PDT requires a Pike interpreter. " +
            "Check your configuration settings (\"Window/Preferences/PDT\").";

        PikeExecutor executor = new PikeExecutor();
        try
        {
            List args = new ArrayList(1);
            args.add("-v");
            if (executor.execute(new File("."), args, "")
                .stderr.indexOf("Pike v") != -1)
            {
                requirePikeCheckPassed = true;
            }
            else
            {
                Status status = new Status(
                    IStatus.ERROR,
                    getPluginId(),
                    IStatus.OK,
                    "The executable specified in PDT Preferences " +
                    "does not appear to be a valid Pike interpreter.",
                    null);

                getLog().log(status);
                if (!requirePikeErrorDisplayed || interactive)
                {
                    requirePikeErrorDisplayed = true;
                    showErrorDialog(ERROR_TITLE, ERROR_MSG, status);
                }
                requirePikeCheckPassed = false;
            }
        }
        catch (CoreException e)
        {
            getLog().log(e.getStatus());
            if (!requirePikeErrorDisplayed || interactive)
            {
                requirePikeErrorDisplayed = true;
                showErrorDialog(ERROR_TITLE, ERROR_MSG, e.getStatus());
            }
            requirePikeCheckPassed = false;
        }
        finally { executor.dispose(); }
    }

    private void showErrorDialog(
        final String title,
        final String msg,
        final IStatus status)
    {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                ErrorDialog.openError(null, title, msg, status);
            } });
    }
	
}
