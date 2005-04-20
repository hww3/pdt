package org.gotpike.pdt.editors;

import java.util.ArrayList;

import org.eclipse.jface.text.rules.*;
import org.gotpike.pdt.editors.rule.CppMultiLineConstructRule;
import org.gotpike.pdt.editors.rule.PreprocessorRule;

public class PikePartitionScanner extends RuleBasedPartitionScanner {
	public PikePartitionScanner() {

		IToken pikeComment = new Token(EditorConstants.PIKE_COMMENT);
		IToken pikeAutoDoc = new Token(EditorConstants.PIKE_AUTODOC);
		IToken pikeString = new Token(EditorConstants.PIKE_STRING);
		IToken pikeChar = new Token(EditorConstants.PIKE_CHAR);

		ArrayList rulelist = new ArrayList();
		
		

		// Add rule for multi and single line autodoc.
		rulelist.add(new MultiLineRule("/*!", "*/", pikeAutoDoc));
		rulelist.add(new EndOfLineRule("//!", pikeAutoDoc));
		
		// Add rule for single and multi line comments.
		rulelist.add(new MultiLineRule("/*", "*/", pikeComment));
		rulelist.add(new EndOfLineRule("//", pikeComment)); 

		// Add rule for strings and character constants.
		rulelist.add(new SingleLineRule("\"", "\"", pikeString, '\\')); 
		rulelist.add(new SingleLineRule("'", "'", pikeChar, '\\')); 
	
		IPredicateRule[] rules = new IPredicateRule[rulelist.size()];
		for(int i = 0; i < rulelist.size(); i++)
			rules[i] = (IPredicateRule)rulelist.get(i);
		
		setPredicateRules(rules);
	}
}
