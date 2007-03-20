package org.gotpike.pdt.views;

import java.util.*;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.gotpike.pdt.model.Method;
import org.gotpike.pdt.model.SourceFile;

public class PikeOutlinePage extends ContentOutlinePage
{
    private SourceFile source;
    
    /**
     * Subroutine in which the caret was during last call to updateSelection
     * We keep track of it to speed up outline synchronisations in the common
     * case (caret movements within a sub).
     */
    private Method lastCaretSub;
    
    public PikeOutlinePage(SourceFile source)
    {
        this.source = source;
    }

    public void createControl(Composite parent)
    {
        super.createControl(parent);

        TreeViewer viewer = getTreeViewer();
        viewer.setContentProvider(new PikeOutlineContentProvider());
        viewer.setLabelProvider(new PikeOutlineLabelProvider());
        viewer.setSorter(new ViewerSorter());
        viewer.setInput(source);
        viewer.expandAll();
    }

    public void updateContent(SourceFile source)
    {
        lastCaretSub = null;
        if (!source.equals(this.source))
        {
            this.source = source;
            getTreeViewer().setInput(source);
        }
    }
    
    public void updateSelection(int caretLine)
    {
        // check lastCaretSub first to speed up things in the most common case
        if (lastCaretSub == null ||
            caretLine < lastCaretSub.getStartLine() ||
            caretLine > lastCaretSub.getEndLine())
        {
            lastCaretSub = null;
            for (Iterator i = source.getMethods(); i.hasNext();)
            {
                Method sub = (Method) i.next();
                if (caretLine >= sub.getStartLine() &&
                    caretLine <= sub.getEndLine())
                {
                    lastCaretSub = sub;
                    break;
                }
            }
        }
        if (lastCaretSub != null)
            setSelection(new StructuredSelection(lastCaretSub));
        else
            setSelection(StructuredSelection.EMPTY);
    }
}
