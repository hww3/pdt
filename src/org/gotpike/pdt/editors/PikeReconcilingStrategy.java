package org.gotpike.pdt.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.*;
import org.eclipse.jface.text.source.ISourceViewer;

class PikeReconcilingStrategy
    implements IReconcilingStrategy, IReconcilingStrategyExtension
{
    //private final ISourceViewer viewer;
    private final PikeEditor editor;
    //private IProgressMonitor monitor;
    
    public PikeReconcilingStrategy(ISourceViewer viewer, PikeEditor editor)
    {
        this.editor = editor;
        //this.viewer = viewer;
    }

    public void setDocument(IDocument document)
    {
    }

    public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion)
    {
        // this does not get called because we are non-incremental
    }

    public void reconcile(IRegion partition)
    {
        editor.reconcile();        
    }

    public void setProgressMonitor(IProgressMonitor monitor)
    {
        //this.monitor = monitor;
    }

    public void initialReconcile()
    {
    }
}
