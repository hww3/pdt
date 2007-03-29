package org.gotpike.pdt.model;

import org.gotpike.pdt.parser.PikeSymbol;

/**
 * An ISourceElement representing an "inherit" statement.
 * 
 * @author jploski
 */
public class Inherit implements IClassElement
{
    private final Class parent;
    private final int modifiers;
    private final PikeSymbol className;
    private final PikeSymbol name;
    
    public Inherit(Class parent, PikeSymbol className, PikeSymbol name, int modifiers)
    {
        this.parent = parent;
        this.modifiers = modifiers;
        this.className = className;
        this.name = name;
    }
    
    public int getModifiers()
    {
        return modifiers;
    }

    public int getLength()
    {
        return name.getLength();
    }

    public String getName()
    {
        return (String)className.value;
    }
    
    public PikeSymbol getNameToken()
    {
        return className;
    }

    public int getOffset()
    {
          return className.getOffset();
    }
    
    public Class getParent()
    {
        return parent;
    }
    
    public String getRefName()
    {
    	if(name != null)
          return (String)name.value;
    	else return "";
    }
    
    public String toString()
    {
        return "inherit #" + modifiers + " " + getName() + " @" + getOffset(); 
    }
}
