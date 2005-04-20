package org.gotpike.pdt.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class PikeConfiguration extends SourceViewerConfiguration {
	private PikeDoubleClickStrategy doubleClickStrategy;
	private ColorManager colorManager;

	public PikeConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}
	
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] {
			IDocument.DEFAULT_CONTENT_TYPE,
			EditorConstants.PIKE_AUTODOC,
			EditorConstants.PIKE_COMMENT,
			EditorConstants.PIKE_CPP,
			EditorConstants.PIKE_STRING};
	}
	
	public ITextDoubleClickStrategy getDoubleClickStrategy(
		ISourceViewer sourceViewer,
		String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new PikeDoubleClickStrategy();
		return doubleClickStrategy;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PikePresentationReconciler reconciler = new PikePresentationReconciler(colorManager);
		return (PresentationReconciler)reconciler;
		// return null;
	}

}