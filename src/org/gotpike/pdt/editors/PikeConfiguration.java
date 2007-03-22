package org.gotpike.pdt.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

public class PikeConfiguration extends SourceViewerConfiguration {
	private PikeDoubleClickStrategy doubleClickStrategy;
	private ColorManager colorManager;
	private PikeEditor editor;
	
	public PikeConfiguration(ColorManager colorManager, PikeEditor editor) {
		this.colorManager = colorManager;
		this.editor = editor;
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

	public IReconciler getReconciler(ISourceViewer sourceViewer)
    {
        PikeReconciliationStrategy strategy = new PikeReconciliationStrategy();
        strategy.setEditor(editor);
  
        MonoReconciler reconciler = new MonoReconciler(strategy,false);
        return reconciler;
    }
}