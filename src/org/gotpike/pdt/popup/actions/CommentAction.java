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

public class CommentAction implements IEditorActionDelegate {

	 private IEditorPart editorPart;
	 private IViewPart viewPart = null;
	
	/**
	 * Constructor for Action1.
	 */
	public CommentAction() {
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
	public void run(IAction action) {
		Shell shell = new Shell();
		MessageDialog.openInformation(
			shell,
			"Pike Development Tools",
			"New Action was executed.");
	}

	
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
