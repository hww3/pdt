package org.gotpike.pdt.debug;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;

/**
 * Generates hyperlinks for compiler output
 */
public class PikeLineTracker implements IConsoleLineTracker
{

	private IConsole fConsole;
	private List<Pattern> regexpPatterns;

	String[] regexps = {"(.*(?:\\.pike|\\.pmod)):(\\d+):(.*)"};
	
	/**
	 * Constructor for JavacLineTracker.
	 */
	public PikeLineTracker()
	{
		super();
	}

	/**
	 * @see org.eclipse.debug.ui.console.IConsoleLineTracker#init(org.eclipse.debug.ui.console.IConsole)
	 */
	public void init(IConsole console)
	{
		regexpPatterns = new ArrayList<Pattern>();
		fConsole = console;
		for (int i = 0; i < regexps.length; i++)
		{
			String regexp = regexps[i];
			System.out.println("compiling " + regexp);
			Pattern p = Pattern.compile(regexp);
			regexpPatterns.add(p);
		}
	}

	public void lineAppended(IRegion line)
	{
		try
		{
			int lineOffset = line.getOffset();
			int lineLength = line.getLength();
			String text = fConsole.getDocument().get(lineOffset, lineLength);
			for (Iterator iter = regexpPatterns.iterator(); iter.hasNext();)
			{
				Pattern pattern = (Pattern) iter.next();
				doMatch(pattern, text, lineOffset, lineLength);
			}
		}
		catch (BadLocationException e)
		{
		}
	}

	/**
	 * @see org.eclipse.debug.ui.console.IConsoleLineTracker#lineAppended(org.eclipse.jface.text.IRegion)
	 */
	public void doMatch(
		Pattern pattern,
		String text,
		int lineOffset,
		int lineLength)
	{
		Matcher matcher = pattern.matcher(text);
		while (matcher.find())
		{
			IFile file = null;
			String fileName = null;
			int lineNum = -1;
            int colStart = -1;
            int colEnd = -1;
			int linkOffset = -1;
			int linkLength = -1;

			try
			{
				fileName = matcher.group(1);
				// hyperlink starts at start of first group
				linkOffset = lineOffset + matcher.start(1);
				// hyperlink extends up to end of fourth group, depending on number of groups in regexp
				linkLength = matcher.end(1) - matcher.start(1);
				
				lineNum = Integer.parseInt(matcher.group(2));
				linkLength = matcher.end(2) - matcher.start(1);

                colStart = Integer.parseInt(matcher.group(3));
                linkLength = matcher.end(3) - matcher.start(1);
                
                colEnd = Integer.parseInt(matcher.group(4));
                linkLength = matcher.end(4) - matcher.start(1);
			}
			catch (IndexOutOfBoundsException e)
			{
			}
			catch (NumberFormatException e)
			{
			}

			if (fileName != null)
			{
				ExternalFileLink link = new ExternalFileLink(fileName, lineNum, colStart, colEnd);
				fConsole.addLink(link, linkOffset, linkLength);
			}
		}
	}

	/**
	 * @see org.eclipse.debug.ui.console.IConsoleLineTracker#dispose()
	 */
	public void dispose()
	{
		fConsole = null;
	}

}
