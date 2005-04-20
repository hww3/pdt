package org.gotpike.pdt.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.gotpike.pdt.editors.rule.PreprocessorRule;

public class PikeDefaultScanner extends RuleBasedScanner {

	public PikeDefaultScanner(ColorManager manager) {
		IToken pikeReserved = new Token(new TextAttribute(manager.getColor(IPikeColorConstants.PIKE_RESERVED)));
		IToken pikeCpp = new Token(new TextAttribute(manager.getColor(IPikeColorConstants.PIKE_CPP)));

		IRule[] rules = new IRule[2];
		
	    WordRule r = new WordRule(new PikeDefaultWordDetector());
	    
	    // populate the reserved words list from our predefined list.
	    for(int i = 0; i < EditorConstants.PIKE_RESERVED_KEYWORDS.length; i++)
  	      r.addWord(EditorConstants.PIKE_RESERVED_KEYWORDS[i], pikeReserved);
		rules[0] = r;

		PreprocessorRule pr = new PreprocessorRule(new PikeDefaultWordDetector(), pikeCpp);
		pr.addWord("define", pikeCpp);
		rules[1] = pr;
		
		setRules(rules);
	}
	
	static class PikeDefaultWordDetector implements IWordDetector {

		/**
		 * @see IWordDetector#isWordStart
		 */
		public boolean isWordStart(char c) {
			return (c >= 'a' && c <= 'z');
		}

		/**
		 * @see IWordDetector#isWordPart
		 */
		public boolean isWordPart(char c) {
			return Character.isLetter(c);
		}
	};
}
