package org.gotpike.pdt.editors;

import org.eclipse.ui.editors.text.TextEditor;

public class PikeEditor extends TextEditor {

	private ColorManager colorManager;

	public PikeEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new PikeConfiguration(colorManager));
		setDocumentProvider(new PikeDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

}
