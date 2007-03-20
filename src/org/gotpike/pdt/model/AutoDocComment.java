package org.gotpike.pdt.model;

import org.gotpike.pdt.parser.PikeSymbol;


/**
 * An ISourceElement representing a POD comment.
 * 
 * @author jploski
 */
public class AutoDocComment implements IMultilineElement
{
    private final PikeSymbol docStart;
    private final PikeSymbol docEnd;

    public AutoDocComment(PikeSymbol podStart, PikeSymbol podEnd)
    {
        this.docStart = podStart;
        this.docEnd = podEnd;
    }
    
    public int getLength()
    {
        return docStart.getLength();
    }

    public String getName()
    {
        return docStart.value.toString();
    }

    public int getOffset()
    {
        return docStart.getOffset();
    }

    public int getEndLine()
    {
        return docEnd.getLine()-1;
    }

    public int getStartLine()
    {
        return docStart.getLine()-1;
    }
}
