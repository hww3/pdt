package org.gotpike.pdt.editors;

import org.eclipse.jface.text.rules.IWhitespaceDetector;

public class PikeWhitespaceDetector implements IWhitespaceDetector {

	public boolean isWhitespace(char c) {
		return (c == ' ' || c == '\t' || c == '\n' || c == '\r');
	}
}
