package org.gotpike.pdt.model;

import org.gotpike.pdt.parser.CurlySymbol;
import org.gotpike.pdt.parser.PikeSymbol;


/**
 * An ISourceElement representing a subroutine definition.
 * 
 * @author jploski
 */
public class Constant implements IMultilineElement, IClassElement
{
    private final Class parent = null;
    private final PikeSymbol name;
    private final int modifiers;
    
    public Constant(PikeSymbol c, int modifiers2) {
    	name = c;
    	modifiers = modifiers2;
    	// TODO Auto-generated constructor stub
	}
        
    public int getLength()
    {
        return name.getLength();
    }
    
    public String getName()
    {
    	//System.out.println("name: " + name);
        return (String)(name.value);
    }

    public PikeSymbol getNameToken()
    {
        return name;
    }
    
    public int getOffset()
    {
        return name.getOffset();
    }

    
    public Class getParent()
    {
        return parent;
    }

    public int getStartLine()
    {
        return name.getLine()-1;
    }
        
    
    public String toString()
    {
        return "constant " + getName() + " @" + getOffset(); 
    }

	public int getEndLine() {
		
		return getStartLine();
	}
}
