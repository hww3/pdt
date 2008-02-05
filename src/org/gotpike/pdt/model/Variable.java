package org.gotpike.pdt.model;

import org.gotpike.pdt.parser.PikeSymbol;

/**
 * An ISourceElement representing an "variable" statement.
 * 
 * @author jploski
 */
public class Variable implements IClassElement
{
    private final Class parent;
    private final int modifiers;
    private final PikeSymbol type;
    private final PikeSymbol name;
    
    public Variable(Class parent, PikeSymbol type, PikeSymbol name, int modifiers)
    {
        this.parent = parent;
        this.modifiers = modifiers;
        this.type = type;
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
        return (String)type.value + ((name != null) ? (" : " + getRefName()) : "");
    }
    
    public PikeSymbol getTypeToken()
    {
        return type;
    }

    public int getOffset()
    {
          return type.getOffset();
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
        return "field #" + modifiers + " " + getName() + " @" + getOffset(); 
    }
}
