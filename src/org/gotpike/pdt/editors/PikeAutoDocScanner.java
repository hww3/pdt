package org.gotpike.pdt.editors;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.gotpike.pdt.PDTPlugin;
import org.gotpike.pdt.preferences.PreferenceConstants;

public class PikeAutoDocScanner extends RuleBasedScanner {

	public PikeAutoDocScanner(ColorManager manager) {
		IToken pikeAutoDoc = new Token(new TextAttribute(PDTPlugin.getDefault().getColor(PreferenceConstants.EDITOR_COMMENT1_COLOR)));

		IRule[] rules = new IRule[1];
		
	    WordRule r = new WordRule(new PikeAutoDocWordDetector());
	    r.addWord("decl", pikeAutoDoc);
		r.addWord("returns", pikeAutoDoc);
		r.addWord("param", pikeAutoDoc);
		r.addWord("note", pikeAutoDoc);
		r.addWord("example", pikeAutoDoc);
		rules[0] = r;

		setRules(rules);
	}
	
	static class PikeAutoDocWordDetector implements IWordDetector {

		/**
		 * @see IWordDetector#isWordStart
		 */
		public boolean isWordStart(char c) {
			return (c >= '@');
		}

		/**
		 * @see IWordDetector#isWordPart
		 */
		public boolean isWordPart(char c) {
			return Character.isLetter(c);
		}
	};
}
