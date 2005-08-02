package org.gotpike.pdt.editors;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.editors.text.TextEditor;

public class PikeEditor extends TextEditor {

    private ProjectionSupport projectionSupport;
	private ColorManager colorManager;
	private ProjectionAnnotationModel annotationModel;
	
	public PikeEditor() {
		super();
		colorManager = new ColorManager();
		setSourceViewerConfiguration(new PikeConfiguration(colorManager, this));
		setDocumentProvider(new PikeDocumentProvider());
	}
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}

	public void createPartControl(Composite parent)
	{
	    super.createPartControl(parent);
	    ProjectionViewer viewer =(ProjectionViewer)getSourceViewer();

	    projectionSupport = new ProjectionSupport(viewer,getAnnotationAccess(),getSharedColors());
	    projectionSupport.install();

	    //turn projection mode on
	    viewer.doOperation(ProjectionViewer.TOGGLE);

	    annotationModel = viewer.getProjectionAnnotationModel();

	}
	
	protected ISourceViewer createSourceViewer(Composite parent,
            IVerticalRuler ruler, int styles)
	{
		ISourceViewer viewer = new ProjectionViewer(parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles);

		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);

		return viewer;
	}
	
	private ProjectionAnnotation[] oldAnnotations;
	public void updateFoldingStructure(ArrayList positions)
	{
	   ProjectionAnnotation[] annotations = new ProjectionAnnotation[positions.size()];

	   //this will hold the new annotations along
	   //with their corresponding positions
	   HashMap newAnnotations = new HashMap();

	   for(int i = 0; i < positions.size();i++)
	   {
	      ProjectionAnnotation annotation = new ProjectionAnnotation();

	      newAnnotations.put(annotation, positions.get(i));

	      annotations[i] = annotation;
	   }

	   annotationModel.modifyAnnotations(oldAnnotations, newAnnotations,null);

	   oldAnnotations = annotations;
	}
}
