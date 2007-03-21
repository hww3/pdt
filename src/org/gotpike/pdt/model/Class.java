package org.gotpike.pdt.model;

import java.util.*;

import org.eclipse.jface.text.BadLocationException;
import org.gotpike.pdt.parser.CurlySymbol;
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
    private final List methods;
    private final List inherits;
    private PikeSymbol lastToken;
    
    /**
     * Creates the default ("main") package.
     */
    public Class(String name)
    {
        this.index = 0;
        this.blockLevel = 0;
        this.name = name;
        this.methods = new ArrayList();
        this.inherits = new ArrayList();
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
        this.methods = new ArrayList();
        this.inherits = new ArrayList();
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
    
    public Inherit addInherit(PikeSymbol className, PikeSymbol name)
        throws BadLocationException
    {
        Inherit ret = new Inherit(this, inherits.size(), className, name);
        inherits.add(ret);
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
        return lastToken.getLine()-1;
    }

    public int getStartLine()
    {
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
        return  -1;
    }
    
    public List getMethods()
    {
        return Collections.unmodifiableList(methods);
    }
    
    public List getInherits()
    {
        return Collections.unmodifiableList(inherits);
    }
    
    public int hashCode()
    {
        return index;
    }
    
    public void setLastToken(PikeSymbol lastToken)
    {
        this.lastToken = lastToken;
    }
}