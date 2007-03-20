package org.gotpike.pdt.model;

import org.gotpike.pdt.parser.CurlySymbol;
import org.gotpike.pdt.parser.PikeSymbol;


/**
 * An ISourceElement representing a subroutine definition.
 * 
 * @author jploski
 */
public class Method implements IMultilineElement, IClassElement
{
    private final Class parent;
    private final int index;
    private final PikeSymbol subKeyword;
    private final PikeSymbol name;
    private final CurlySymbol openCurly;
    private CurlySymbol closeCurly;
    
    public Method(
        Class parent,
        int index,
        PikeSymbol subKeyword,
        PikeSymbol name,
        CurlySymbol openCurly)
    {
        assert parent != null;
        assert index >= 0;
        assert subKeyword != null;
        assert name != null;
        assert openCurly != null;

        this.parent = parent;
        this.index = index;
        this.subKeyword = subKeyword;
        this.name = name;
        this.openCurly = openCurly;
    }
    
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Method)) return false;
        
        Method sub = (Method) obj;
        return index == sub.index && parent.equals(sub.parent);
    }
    
    public int getBlockLevel()
    {
        return openCurly.getLevel();
    }

    public CurlySymbol getCloseCurly()
    {
        return closeCurly;
    }
    
    public int getLength()
    {
        return name.getLength();
    }
    
    public String getName()
    {
        return (String)name.value;
    }

    public PikeSymbol getNameToken()
    {
        return name;
    }
    
    public int getOffset()
    {
        return name.getOffset();
    }

    public CurlySymbol getOpenCurly()
    {
        return openCurly;
    }
    
    public Class getParent()
    {
        return parent;
    }

    public PikeSymbol getSubKeyword()
    {
        return subKeyword;
    }

    public int getEndLine()
    {
        return closeCurly != null ? closeCurly.getLine()-1 : getStartLine();
    }

    public int getStartLine()
    {
        return subKeyword.getLine()-1;
    }
    
    public int hashCode()
    {
        return parent.hashCode() * 37 + index;
    }
    
    public void setCloseCurly(CurlySymbol curly)
    {
        assert closeCurly == null;
        assert curly.getLevel() == openCurly.getLevel();
        
        closeCurly = curly;
    }
    
    public String toString()
    {
        return "sub #" + index + " " + getName() + " @" + getOffset(); 
    }
}
