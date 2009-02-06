package org.gotpike.pdt.model;

import java.util.*;

import org.eclipse.jface.text.BadLocationException;
import org.gotpike.pdt.parser.CurlySymbol;
import org.gotpike.pdt.parser.InheritItem;
import org.gotpike.pdt.parser.PikeSymbol;


/**
 * IMultilineElement representing a "package" declaration in a source file,
 * or the implicit "main" package. Other source file elements defined within
 * the scope of the package are maintained as subelements within the Package
 * object.
 * <p>
 * The identity of the Package object only reflects its relative position in
 * the ordered list of "package" keywords present in the source file.
 * This is to support incremental updates while reparsing the file and
 * to optimise performance of {@link TreeViewer#refresh}, which disfavours
 * models that change contained objects' identities. Consequently, reordering
 * package keywords within a source file is likely to suffer from low
 * performance (this is supposed to be a rarely occuring case).
 * <p>
 * The Package element logically spans multiple lines, so that all
 * elements belonging to the package are fully contained between its first
 * and last line. The first line is the one containing the "package" keyword.
 * The last line is the one containing the terminating } of the enclosing block,
 * or the line containing the last token before the next "package" keyword
 * in the same block, or the last line in file, whichever comes first.
 * 
 * @author jploski
 */
public class Class implements IMultilineElement
{
    private final int index;
    private final int blockLevel;
    private final String name;
    private List methods;
    private List inherits;
    private List variables;
    private List classes;
    private PikeSymbol lastToken;
	private boolean top = false;
	private int modifiers;
    private List constants;
    private PikeSymbol classname = null;
    
    /**
     * Creates the default ("main") package.
     */
    public Class(String name)
    {
        this.init();
        this.index = 0;
        this.blockLevel = 0;
        this.name = name;

    }
    
    public Class(PikeSymbol name)
    {
        init();
    	this.classname = name;
        this.index = 0;
        this.blockLevel = 0;
        this.name = name.getText();

    }
    
    /**
     * Creates a named package.
     */
    public Class(
        int index,
        int blockLevel,
        String name) 
    {
        this.index = index;
        this.blockLevel = blockLevel;
        this.name = name;
        init();
    }

    private void init()
    {
    	this.methods = new ArrayList();
    	this.inherits = new ArrayList();
    	this.classes = new ArrayList();
    	this.constants = new ArrayList();
    	this.variables = new ArrayList();
	}

    
    public Method addMethod(
    		PikeSymbol subKeyword,
    		PikeSymbol name,
        CurlySymbol openCurly)
    {
        Method ret = new Method(
            this, methods.size(), subKeyword, name, openCurly);
        methods.add(ret);
        return ret;
    }

    
    public boolean equals(Object obj)
    {
        return obj instanceof Class &&
               index == ((Class) obj).index;
    }
    
    public int getBlockLevel()
    {
        return blockLevel;
    }

    public int getEndLine()
    {
    	if(lastToken != null) 
          return lastToken.getLine()-1;
    	else return 0;
    }

    public int getStartLine()
    {
  //  	System.out.println("getStartLine: " + classname.getLine());
    	if(classname != null)
    		return classname.getLine()-1;
    		
        return 0;
    }
    
    public int getIndex()
    {
        return index;
    }

    public int getLength()
    {
        return name.length();
    }

    public String getName()
    {
        return name;
    }

    public int getOffset()
    {
    	if(classname != null)
    		return classname.getOffset();
        return  0;
    }
    
    public List getMethods()
    {
        return Collections.unmodifiableList(methods);
    }
    
    public List getClasses()
    {
        return Collections.unmodifiableList(classes);
    }
 
    public List getInherits()
    {
        return Collections.unmodifiableList(inherits);
    }
    
	public List getConstants() 
	{
       return Collections.unmodifiableList(constants);
	}
	
    public int hashCode()
    {
        return index;
    }
    
    public void setLastToken(PikeSymbol lastToken)
    {
        this.lastToken = lastToken;
    }

	public void addClass(Class cls) {
		classes.add(cls);
	}

	public boolean getTop()
	{
		return top;
	}
	
	public void setTop() {
		top  = true;
		// TODO Auto-generated method stub
		
	}

	public void setModifiers(int currentModifiers) {
		this.modifiers = currentModifiers;
	}

	public Constant addConstant(PikeSymbol c, int modifiers2) {
		Constant constant = new Constant(c, modifiers2);
		constants.add(constant);
		return constant;
	}

	public Variable addVariable(PikeSymbol t, PikeSymbol n, int modifiers2) {
		Variable v = new Variable(this, t, n, modifiers2);
		variables.add(v);
		return v;
	}
	
	public Inherit addInherit(InheritItem in) {
		System.out.println("addInherit " + in.ref);
		Inherit inherit = new Inherit(this, in.ref, in.name, in.modifiers);
		inherits.add(inherit);
		return inherit;
	}

	public Collection getVariables() {
       return Collections.unmodifiableList(variables);
	}


}
