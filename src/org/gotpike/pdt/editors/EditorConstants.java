/*
 * Created on Apr 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.gotpike.pdt.editors;

/**
 * @author e10401
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public  interface EditorConstants {


	public final static String[] PIKE_RESERVED_KEYWORDS = 
		{ "this", "for", "foreach", "do", "while", "if", "else", "return", 
			"class", "switch", "case", "default", "break", "continue", "catch", "gauge" };
	
	public final static String[] PIKE_RESERVED_DATATYPES = 
		{ "int", "float", "string", "array", "mapping", "multiset", "mixed", 
			"object", "function", "program", "void", "constant"};

	public final static String[] PIKE_RESERVED_MODIFIERS = 
	{ "public", "private", "static", "protected", "final", "optional", "nomask"};
	
	public final static String[] PIKE_RESERVED_CPP = 
	{ "#define", "#if", "#pragma", "#else", "#pike"};

}
