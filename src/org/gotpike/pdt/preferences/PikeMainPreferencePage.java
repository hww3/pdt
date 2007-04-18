package org.gotpike.pdt.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.gotpike.pdt.PDTPlugin;

public class PikeMainPreferencePage
	extends PreferencePage
	implements IWorkbenchPreferencePage {

	private Text executableText;
	private Text browserLabelText;
	private Button warningsCheckBox;
	private Button taintCheckBox;
    private Button debugConsoleCheckBox;
    private Button suspendAtFirstCheckBox;

	private Button validateCheckBox;
	private Scale syntaxCheckInterval;
	private Combo interpreterTypeCombo;
	private Label syntaxIntervalSecondsLabel;
	private Composite fParent;

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {

		fParent = parent;

		Composite top = new Composite(parent, SWT.NULL);

		//Create a data that takes up the extra space in the dialog .
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		top.setLayoutData(data);

		GridLayout layout = new GridLayout();
		top.setLayout(layout);

		Composite buttonComposite = new Composite(top, SWT.NULL);

		GridLayout buttonLayout = new GridLayout();
		//buttonLayout.numColumns = 2;
		buttonLayout.numColumns = 3;
		buttonComposite.setLayout(buttonLayout);

		//Create a data that takes up the extra space in the dialog and spans both columns.
		data =
			new GridData(
				GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
		buttonComposite.setLayoutData(data);

		Label executableLabel = new Label(buttonComposite, SWT.NONE);
		executableLabel.setText("Pike executable:");

		executableText = new Text(buttonComposite, SWT.BORDER);

		Button browseButton =
			new Button(buttonComposite, SWT.PUSH | SWT.CENTER);

		browseButton.setText("..."); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				FileDialog fileBrowser = new FileDialog(fParent.getShell());
				String dir = fileBrowser.open();
				if (dir != null) {
					// Surround interpreter name by ""
					executableText.setText( dir );
				}
			}
		});

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		executableText.setLayoutData(data);

		executableText.setText(
			PDTPlugin.getDefault().getExecutablePreference());

/*
		Label executableInfoLabel = new Label(top, SWT.NONE);
		executableInfoLabel.setText(
			"(Windows users, please specify path with forward slashes '/')");

		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		Label dummy = new Label(top, SWT.CHECK);
		dummy.setLayoutData(data);
		*/

//		data = new GridData(GridData.FILL_HORIZONTAL);
//		data.grabExcessHorizontalSpace = true;
					

		
		// Warning preference
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		warningsCheckBox = new Button(top, SWT.CHECK);
		warningsCheckBox.setText("Enable warnings");
		warningsCheckBox.setSelection(
			PDTPlugin.getDefault().getWarningsPreference());
		warningsCheckBox.setLayoutData(data);

        // Debugger console (experimental)
/*
		data = new GridData(GridData.FILL_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        debugConsoleCheckBox = new Button(top, SWT.CHECK);
        debugConsoleCheckBox.setText("Enable debugger console (experimental)");
        debugConsoleCheckBox.setSelection(
            PerlEditorPlugin.getDefault().getDebugConsolePreference());
        debugConsoleCheckBox.setLayoutData(data);        

		
        // Stop debugger at first line
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.grabExcessHorizontalSpace = true;
        suspendAtFirstCheckBox = new Button(top, SWT.CHECK);
        suspendAtFirstCheckBox.setText("Suspend debugger at first statement");
        suspendAtFirstCheckBox.setSelection(
            PerlEditorPlugin.getDefault().getSuspendAtFirstPreference());
        suspendAtFirstCheckBox.setLayoutData(data);
*/        
		/*
		//WebBrowser preferences
		Composite browserComposite = new Composite(top, SWT.NULL);
		GridLayout browserLayout = new GridLayout();
		browserLayout.numColumns = 2;
		browserComposite.setLayout(browserLayout);
		data = new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
		browserComposite.setLayoutData(data);
		
		Label browserLabel=new Label(browserComposite, SWT.NONE);
		browserLabel.setText("Default Web-Start page:");
		
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		browserLabelText = new Text(browserComposite, SWT.BORDER);
		browserLabelText.setLayoutData(data);
		browserLabelText.setText(
			PDTPlugin.getDefault().getWebBrowserPreference());
*/
		Composite syntaxIntervalComposite = new Composite(top, SWT.NULL);

		GridLayout syncIntervalLayout = new GridLayout();
		syncIntervalLayout.numColumns = 3;
		syntaxIntervalComposite.setLayout(syncIntervalLayout);
		data =
			new GridData(
				GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING);
		syntaxIntervalComposite.setLayoutData(data);



		validateCheckBox = new Button(syntaxIntervalComposite, SWT.CHECK);
		validateCheckBox.setText("Validate source when idle for ");
		validateCheckBox.setSelection(
				PDTPlugin.getDefault().getSyntaxValidationPreference());
	
		syntaxCheckInterval = new Scale(syntaxIntervalComposite, SWT.HORIZONTAL);
		syntaxCheckInterval.setMinimum(1);
		syntaxCheckInterval.setMaximum(10000);
		syntaxCheckInterval.setIncrement(100);
		
		syntaxIntervalSecondsLabel = new Label(syntaxIntervalComposite, SWT.NONE);
		int interval = PDTPlugin.getDefault().getPreferenceStore().getInt(PDTPlugin.SYNTAX_VALIDATION_INTERVAL_PREFERENCE) ;
		float intervalDisplay = Math.round(interval/10f)/100f;
		syntaxIntervalSecondsLabel.setText(intervalDisplay + " seconds  ");
		syntaxCheckInterval.setSelection(interval);
		
		syntaxCheckInterval.addListener (SWT.Selection, new Listener () {
						public void handleEvent (Event event) {
						  float intervalDisplay = Math.round(syntaxCheckInterval.getSelection()/10f)/100f;
						  syntaxIntervalSecondsLabel.setText(intervalDisplay + " seconds  ");
						}
				});
			
		
		syntaxIntervalComposite.setLayoutData(data);

		return new Composite(parent, SWT.NULL);
	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
		//Initialize the preference store we wish to use
		setPreferenceStore(PDTPlugin.getDefault().getPreferenceStore());
	}

	/**
	 * Performs special processing when this page's Restore Defaults button has 
	 * been pressed.
	 * Sets the contents of the color field to the default value in the preference
	 * store.
	 */
	protected void performDefaults() {
		executableText.setText(
			PDTPlugin.getDefault().getDefaultExecutablePreference());
			
		warningsCheckBox.setSelection(
			PDTPlugin.getDefault().getDefaultWarningsPreference());
/*
		browserLabelText.setText(
			PDTPlugin.getDefault().getDefaultWebBrowserPreference());
			*/
		
		validateCheckBox.setSelection(
				PDTPlugin.getDefault().getDefaultSyntaxValidationPreference());
		float intervalDisplay = Math.round(PDTPlugin.SYNTAX_VALIDATION_INTERVAL_DEFAULT/10f)/100f;
		syntaxIntervalSecondsLabel.setText(intervalDisplay + " seconds ");
		syntaxCheckInterval.setSelection(PDTPlugin.SYNTAX_VALIDATION_INTERVAL_DEFAULT);
	    
		//colorEditor.loadDefault();
	}
	/** 
	 * Method declared on IPreferencePage. Save the
	 * color preference to the preference store.
	 */
	public boolean performOk() {
		PDTPlugin.getDefault().setExecutablePreference(
			executableText.getText());
		PDTPlugin.getDefault().setWarningsPreference(
			warningsCheckBox.getSelection());

		/*
		 
		PDTPlugin.getDefault().setDebugConsolePreference(
            debugConsoleCheckBox.getSelection());
        PDTPlugin.getDefault().setSuspendAtFirstPreference(
            suspendAtFirstCheckBox.getSelection());
        
        */
        
		PDTPlugin.getDefault().setSyntaxValidationPreference(
            validateCheckBox.getSelection());

		PDTPlugin.getDefault().getPreferenceStore().setValue(PDTPlugin.SYNTAX_VALIDATION_INTERVAL_PREFERENCE, syntaxCheckInterval.getSelection());
/*		PDTPlugin.getDefault().setWebBrowserPreference(
			browserLabelText.getText());
*/			
		return super.performOk();
	}
}