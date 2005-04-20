/*
 * Created on Apr 20, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.gotpike.pdt.editors.rule;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

/**
 * @author e10401
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CppMultiLineConstructRule implements IPredicateRule {

	/**
	 * Comparator that orders <code>char[]</code> in decreasing array lengths.
	 *
	 * @since 3.1
	 */
	private static class DecreasingCharArrayLengthComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			return ((char[]) o2).length - ((char[]) o1).length;
		}
	}
	
	/** Internal setting for the un-initialized column constraint */
	protected static final int UNDEFINED= -1;

	/** The token to be returned on success */
	protected IToken fToken;
	/** The pattern's start sequence */
	protected char[] fStartSequence;
	/** The pattern's end sequence */
	protected char[] fEndSequence;
	/** The pattern's column constrain */
	protected int fColumn= UNDEFINED;
	/** The pattern's escape character */
	protected char fEscapeCharacter;
	/**
	 * Indicates whether the escape character continues a line
	 * @since 3.0
	 */
	protected boolean fEscapeContinuesLine;
	/** Indicates whether end of line terminates the pattern */
	protected boolean fBreaksOnEOL;
	/** Indicates whether end of file terminates the pattern */
	protected boolean fBreaksOnEOF;
	
	/**
	 * Line delimiter comparator which orders according to decreasing delimiter length.
	 * @since 3.1
	 */
	private Comparator fLineDelimiterComparator= new DecreasingCharArrayLengthComparator();
	/**
	 * Cached line delimiters.
	 * @since 3.1
	 */
	private char[][] fLineDelimiters;
	/**
	 * Cached sorted {@linkplain #fLineDelimiters}.
	 * @since 3.1
	 */
	private char[][] fSortedLineDelimiters;
	
	/**
	 * 
	 */
	public CppMultiLineConstructRule(String startSequence, String endSequence, 
			IToken token, char escapeCharacter, boolean breaksOnEOL, 
			boolean breaksOnEOF, boolean escapeContinuesLine) {
		this(startSequence, endSequence, token, escapeCharacter, breaksOnEOL, breaksOnEOF);
		fEscapeContinuesLine= escapeContinuesLine;
	}
	
	public CppMultiLineConstructRule(String startSequence, String endSequence, 
			IToken token, char escapeCharacter, boolean breaksOnEOL, 
			boolean breaksOnEOF) {
		this(startSequence, endSequence, token, escapeCharacter, breaksOnEOL);
		fBreaksOnEOF= breaksOnEOF;
	}
	
	public CppMultiLineConstructRule(String startSequence, String endSequence, 
			IToken token, char escapeCharacter, boolean breaksOnEOL) {
		Assert.isTrue(startSequence != null && startSequence.length() > 0);
		Assert.isTrue(endSequence != null || breaksOnEOL);
		Assert.isNotNull(token);

		fStartSequence= startSequence.toCharArray();
		fEndSequence= (endSequence == null ? new char[0] : endSequence.toCharArray());
		fToken= token;
		fEscapeCharacter= escapeCharacter;
		fBreaksOnEOL= breaksOnEOL;
		setColumnConstraint(1);

		System.out.println("char[0] " + (int)fStartSequence[0]);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sets a column constraint for this rule. If set, the rule's token
	 * will only be returned if the pattern is detected starting at the
	 * specified column. If the column is smaller then 0, the column
	 * constraint is considered removed.
	 *
	 * @param column the column in which the pattern starts
	 */
	public void setColumnConstraint(int column) {
		System.out.println("setting column to " + column);
		if (column < 0)
			column= UNDEFINED;
		fColumn= column;
	}


	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		return evaluate(scanner, false);
	}

	/**
	 * Returns whether the end sequence was detected. As the pattern can be considered
	 * ended by a line delimiter, the result of this method is <code>true</code> if the
	 * rule breaks on the end of the line, or if the EOF character is read.
	 *
	 * @param scanner the character scanner to be used
	 * @return <code>true</code> if the end sequence has been detected
	 */
	protected boolean endSequenceDetected(ICharacterScanner scanner) {

		char[][] originalDelimiters= scanner.getLegalLineDelimiters();
		int count= originalDelimiters.length;
		if (fLineDelimiters == null || originalDelimiters.length != count) {
			fSortedLineDelimiters= new char[count][];
		} else {
			while (count > 0 && fLineDelimiters[count-1] == originalDelimiters[count-1])
				count--;
		}
		if (count != 0) {
			fLineDelimiters= originalDelimiters;
			System.arraycopy(fLineDelimiters, 0, fSortedLineDelimiters, 0, fLineDelimiters.length);
			Arrays.sort(fSortedLineDelimiters, fLineDelimiterComparator);
		}

		int c;
		while ((c= scanner.read()) != ICharacterScanner.EOF) {
			if (c == fEscapeCharacter) {
				// Skip escaped character(s)
				if (fEscapeContinuesLine) {
					c= scanner.read();
					for (int i= 0; i < fSortedLineDelimiters.length; i++) {
						if (c == fSortedLineDelimiters[i][0] && sequenceDetected(scanner, fSortedLineDelimiters[i], true))
							break;
					}
				} else
					scanner.read();

			} else if (fEndSequence.length > 0 && c == fEndSequence[0]) {
				// Check if the specified end sequence has been found.
				if (sequenceDetected(scanner, fEndSequence, true))
					return true;
			} else if (fBreaksOnEOL) {
				// Check for end of line since it can be used to terminate the pattern.
				for (int i= 0; i < fSortedLineDelimiters.length; i++) {
					if (c == fSortedLineDelimiters[i][0] && sequenceDetected(scanner, fSortedLineDelimiters[i], true))
						return true;
				}
			}
		}
		if (fBreaksOnEOF) return true;
		scanner.unread();
		return false;
	}

	/**
	 * Returns whether the next characters to be read by the character scanner
	 * are an exact match with the given sequence. No escape characters are allowed
	 * within the sequence. If specified the sequence is considered to be found
	 * when reading the EOF character.
	 *
	 * @param scanner the character scanner to be used
	 * @param sequence the sequence to be detected
	 * @param eofAllowed indicated whether EOF terminates the pattern
	 * @return <code>true</code> if the given sequence has been detected
	 */
	protected boolean sequenceDetected(ICharacterScanner scanner, char[] sequence, boolean eofAllowed) {
		for (int i= 1; i < sequence.length; i++) {
			int c= scanner.read();
			if (c == ICharacterScanner.EOF && eofAllowed) {
				return true;
			} else if (c != sequence[i]) {
				// Non-matching character detected, rewind the scanner back to the start.
				// Do not unread the first character.
				scanner.unread();
				for (int j= i-1; j > 0; j--)
					scanner.unread();
				return false;
			}
		}

		return true;
	}
	
	/**
	 * @see IPredicateRule#evaluate(ICharacterScanner, boolean)
	 * @since 2.0
	 */
	
	public IToken evaluate(ICharacterScanner scanner, boolean resume) 
	{
	//	return Token.UNDEFINED;

		if (resume && endSequenceDetected(scanner))
			return fToken;
		int c= scanner.read();
//		System.out.println("CppMultiLineRule: evaluate(), column " + scanner.getColumn());
		if ((c == '#') && (scanner.getColumn() == 1))
		{
//			System.out.println("looking at a possible directive.");
			// ok, we can be at the start of a preprocessor construct.
			// we must find a preprocessor construct before a new line...
			boolean good = false;
			int count = 0;
			do 
			{
				c = scanner.read();
				count ++;
				if(isWhiteSpace(c)) continue;
				else if(isEOL(c)) break;
				else 
				{
		//			System.out.println("CppMultiLineRule: found a good format");

					good = true;
					scanner.unread();
					break;
				}
			} while(true);
			
			// if we don't have a valid construct, we need to unread the scanner.
			if(!good) 
			{
				for(int i = 0; i < count; i++)
					scanner.unread();
			//	System.out.println("bad define syntax");
			//	return fToken;
				return Token.UNDEFINED;
			}
			
			c= scanner.read();
			if (c == ((int)fStartSequence[0])) {
				if (sequenceDetected(scanner, fStartSequence, false)) {
					if (endSequenceDetected(scanner))
					{
						System.out.println("found an end sequence. " + fToken.getData().toString());
						return fToken;
					}
				}
			}
		}
		else
		{
			scanner.unread();
			return Token.UNDEFINED;
//			return fToken;
		}
	//	return fToken;
		return Token.UNDEFINED;
	}

	boolean isWhiteSpace(int c)
	{
		if(c == ' ' || c =='\t')
			return true;
		else
			return false;
	}
	
	boolean isEOL(int c)
	{
		if(c == '\r' || c == '\n')
			return true;
		else
			return false;
	}
	
	/*
	 * @see IPredicateRule#getSuccessToken()
	 * @since 2.0
	 */
	public IToken getSuccessToken() {
		return fToken;
	}
}

