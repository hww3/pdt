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

	public final static String DEFAULT = "__default";
	public final static String PIKE_RESERVED = "__pike_reserved";
	public final static String PIKE_AUTODOC = "__pike_autodoc";
	public final static String PIKE_COMMENT = "__pike_comment";
	public final static String PIKE_STRING = "__pike_string";
	public final static String PIKE_CHAR = "__pike_char";
	public final static String PIKE_CPP = "__pike_cpp";

	public final static String[] PIKE_RESERVED_KEYWORDS = 
		{ "this", "for", "foreach", "do", "while", "if", "else", "return", 
			"public", "private", "static", "protected", "class",
			"switch", "case", "default", "break", "continue", "catch" };
}
