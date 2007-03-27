package org.gotpike.pdt.actions;

import org.gotpike.pdt.editors.PikeEditor;
import org.gotpike.pdt.editors.PikeEditorActionIds;




public class Jump2BracketAction extends PikeEditorAction
{
    //~ Constructors

    public Jump2BracketAction(PikeEditor editor)
    {
        super(editor);
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



}
