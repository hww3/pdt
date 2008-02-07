package org.gotpike.pdt.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.gotpike.pdt.editors.PikeEditor;
import org.gotpike.pdt.editors.PikeEditorActionIds;


public class Jump2BracketAction extends PikeEditorAction
{
    //~ Constructors

    public Jump2BracketAction(PikeEditor editor)
    {
        super(editor);
    	this.setActionDefinitionId(getPikeEditorActionId());
    }

    //~ Methods

    protected void doRun()
    {
    	getEditor().jumpToMatchingBracket();
    }

    protected String getPikeEditorActionId()
    {
        return PikeEditorActionIds.MATCHING_BRACKET;
    }

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}

	public void run(IAction action) {
		// TODO Auto-generated method stub
		
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
