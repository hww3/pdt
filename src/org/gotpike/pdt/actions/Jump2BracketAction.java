package org.gotpike.pdt.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.gotpike.pdt.editors.PikeEditor;
import org.gotpike.pdt.editors.PikeEditorActionIds;




public class Jump2BracketAction extends PikeEditorAction
{
    //~ Constructors

    public Jump2BracketAction(PikeEditor editor)
    {
        super(editor);
    	System.out.println("Jump2BracketAction(" + getPikeEditorActionId() + ")");
    	this.setActionDefinitionId(getPikeEditorActionId());
    }

    //~ Methods

    protected void doRun()
    {
        System.out.println("Jump2BracketAction.doRun()");
    	getEditor().jumpToMatchingBracket();
    }

    protected String getPikeEditorActionId()
    {
        return PikeEditorActionIds.MATCHING_BRACKET;
    }

}
