/*
 * Created on Apr 19, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.gotpike.pdt.editors;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.Token;

/**
 * @author e10401
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */


public class PikePresentationReconciler extends PresentationReconciler {

	private ColorManager colorManager;

	/**
	 * 
	 */
	public PikePresentationReconciler(ColorManager colorManager) {
		super();
		this.colorManager = colorManager;
		
		DefaultDamagerRepairer dr;
		
		dr = new DefaultDamagerRepairer(new PikeDefaultScanner(colorManager));
		setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		PikeAutoDocScanner ps = new PikeAutoDocScanner(colorManager);
		ps.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getColor(IPikeColorConstants.PIKE_COMMENT))));
		dr = new DefaultDamagerRepairer(ps);
		
		setDamager(dr, EditorConstants.PIKE_AUTODOC);
		setRepairer(dr, EditorConstants.PIKE_AUTODOC);
		
		RuleBasedScanner s = new RuleBasedScanner();
		s.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getColor(IPikeColorConstants.PIKE_COMMENT))));	
		dr= new DefaultDamagerRepairer(s);
		setDamager(dr, EditorConstants.PIKE_COMMENT);
		setRepairer(dr, EditorConstants.PIKE_COMMENT);
	
		s = new RuleBasedScanner();
		s.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getColor(IPikeColorConstants.PIKE_STRING))));
	
		dr= new DefaultDamagerRepairer(s);
		setDamager(dr, EditorConstants.PIKE_STRING);
		setRepairer(dr, EditorConstants.PIKE_STRING);

		s = new RuleBasedScanner();
		s.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getColor(IPikeColorConstants.PIKE_CHAR))));
	
		dr= new DefaultDamagerRepairer(s);
		setDamager(dr, EditorConstants.PIKE_CHAR);
		setRepairer(dr, EditorConstants.PIKE_CHAR);
		
		s = new RuleBasedScanner();
		s.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getColor(IPikeColorConstants.PIKE_CPP))));
	
		dr= new DefaultDamagerRepairer(s);
		setDamager(dr, EditorConstants.PIKE_CPP);
		setRepairer(dr, EditorConstants.PIKE_CPP);

	}
}
