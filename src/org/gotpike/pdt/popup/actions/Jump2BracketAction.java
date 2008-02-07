package org.gotpike.pdt.popup.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.gotpike.pdt.editors.PikeEditor;

public class Jump2BracketAction implements IEditorActionDelegate {

	 private IEditorPart editorPart;
	 private IViewPart viewPart = null;
	
	/**
	 * Constructor for Action1.
	 */
	public Jump2BracketAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */

	public void setActiveEditor(IAction action, IEditorPart targetPart) {
		editorPart = targetPart;
	
	}
	/**
	 * @see IActionDelegate#run(IAction)
	 */
	  protected void doRun()
	    {
	    	((PikeEditor) editorPart).jumpToMatchingBracket();
	    }
	  
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void run(IAction action) {
		doRun();
		
	}

}
