package org.gotpike.pdt.model;

import org.gotpike.pdt.parser.PikeSymbol;

/**
 * An ISourceElement representing a "use Module" statement.
 * 
 * @author jploski
 */
public class Inherit implements IClassElement
{
    private final Class parent;
    private final int index;
    private final PikeSymbol className;
    private final PikeSymbol name;
    
    public Inherit(Class parent, int index, PikeSymbol className, PikeSymbol name)
    {
        this.parent = parent;
        this.index = index;
        this.className = className;
        this.name = name;
    }
    
    public int getIndex()
    {
        return index;
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
    
    public Class getParent()
    {
        return parent;
    }
    
    public String getClassName()
    {
        return (String)className.value;
    }
    
    public String toString()
    {
        return "use #" + index + " " + getName() + " @" + getOffset(); 
    }
}
