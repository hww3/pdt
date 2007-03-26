package org.gotpike.pdt.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.gotpike.pdt.PDTPlugin;
import org.gotpike.pdt.editors.rule.PreprocessorRule;
import org.gotpike.pdt.preferences.PreferenceConstants;

public class PikeDefaultScanner extends RuleBasedScanner {

	PDTPlugin plugin;
	
	public PikeDefaultScanner(ColorManager manager) {
		
		plugin = PDTPlugin.getDefault();
		
		IToken pikeReserved = new Token(new TextAttribute(plugin.getColor(PreferenceConstants.EDITOR_KEYWORD_COLOR)));
		IToken pikeDatatype = new Token(new TextAttribute(plugin.getColor(PreferenceConstants.EDITOR_DATATYPE_COLOR)));
		IToken pikeModifier = new Token(new TextAttribute(plugin.getColor(PreferenceConstants.EDITOR_MODIFIER_COLOR)));
		// IToken pikeCpp = new Token(new TextAttribute(manager.getColor(IPikeColorConstants.PIKE_CPP)));

		
		List<WordRule> rules = new ArrayList<WordRule>();
		
	    WordRule r = new WordRule(new PikeDefaultWordDetector());
	    
	    // populate the reserved words list from our predefined list.
	    for(int i = 0; i < EditorConstants.PIKE_RESERVED_KEYWORDS.length; i++)
  	      r.addWord(EditorConstants.PIKE_RESERVED_KEYWORDS[i], pikeReserved);
		rules.add(r);

		// populate the reserved words list from our predefined list.
		r = new WordRule(new PikeDefaultWordDetector());
	    for(int i = 0; i < EditorConstants.PIKE_RESERVED_MODIFIERS.length; i++)
  	      r.addWord(EditorConstants.PIKE_RESERVED_MODIFIERS[i], pikeModifier);
	    rules.add(r);

		r = new WordRule(new PikeDefaultWordDetector());
	    // populate the datatype words list from our predefined list.
	    for(int i = 0; i < EditorConstants.PIKE_RESERVED_DATATYPES.length; i++)
  	      r.addWord(EditorConstants.PIKE_RESERVED_DATATYPES[i], pikeDatatype);
	    rules.add(r);
		
		/*PreprocessorRule pr = new PreprocessorRule(new PikeDefaultWordDetector());
	    // populate the datatype words list from our predefined list.
	    for(int i = 0; i < EditorConstants.PIKE_RESERVED_CPP.length; i++)
  	      pr.addWord(EditorConstants.PIKE_RESERVED_CPP[i], pikeCpp);
		rules[2] = pr;
		*/
	    WordRule[] rx = new WordRule[rules.size()];
	    
	    rx = rules.toArray(rx);
		setRules(rx);
	}
	

	
	static class PikeDefaultWordDetector implements IWordDetector {

		/**
		 * @see IWordDetector#isWordStart
		 */
		public boolean isWordStart(char c) {
			return ((c >= 'a' && c <= 'z' )|| c == '_');
		}

		/**
		 * @see IWordDetector#isWordPart
		 */
		public boolean isWordPart(char c) {
			return Character.isLetter(c) || (c == '_');
		}
	};
}
