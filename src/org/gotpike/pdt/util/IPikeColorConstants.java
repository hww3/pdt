/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.gotpike.pdt.util;

/**
 * Color keys used for syntax highlighting Pike 
 * code and AutoDoc compliant comments. 
 * A <code>IColorManager</code> is responsible for mapping 
 * concrete colors to these keys.
 * <p>
 * This interface declares static final fields only; it is not intended to be 
 * implemented.
 * </p>
 *
 * @see org.eclipse.jdt.ui.text.IColorManager
 */
public interface IPikeColorConstants {
	
	String STRING_COLOR = "nullColor";
	String KEYWORD_COLOR = "keywordColor";
	String MODIFIER_COLOR = "modifierColor";
	String DATATYPE_COLOR = "datatypeColor";
	String COMMENT1_COLOR = "comment1Color";
	String MARKUP_COLOR = "markupColor";
	String OPERATOR_COLOR ="operatorColor";
	String NUMBER_COLOR = "numberColor";
	String INVALID_COLOR = "invalidColor";
}
