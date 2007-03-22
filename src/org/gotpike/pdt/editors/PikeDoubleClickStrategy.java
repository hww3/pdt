package org.gotpike.pdt.editors;

import org.eclipse.jface.text.*;

public class PikeDoubleClickStrategy implements ITextDoubleClickStrategy {
	protected ITextViewer fText;

	public void doubleClicked(ITextViewer part) {
		int pos = part.getSelectedRange().x;
		System.out.println("pos: " + pos);
		if (pos < 0)
			return;

		fText = part;

		if (!selectComment(pos)) {
			selectWord(pos);
		}
	}
	protected boolean selectComment(int caretPos) {
		IDocument doc = fText.getDocument();
		try {
			String type = doc.getPartition(caretPos).getType();
			System.out.println("type: " + type);
			if(type == PartitionTypes.AUTODOC)
			{	
				int start, end;
				start = doc.getPartition(caretPos).getOffset();
				end = start + doc.getPartition(caretPos).getLength();
				selectRange(start -1, end);
				return true;
			}
			if(type == PartitionTypes.COMMENT)
			{
				int start, end;
				start = doc.getPartition(caretPos).getOffset();
				end = start + doc.getPartition(caretPos).getLength();
				selectRange(start -2, end);
				return true;			
			}
			else return false;
		}
		catch(Exception e)
		{
			System.err.println("error!");
			return false;
		}

	}
	protected boolean selectWord(int caretPos) {

		IDocument doc = fText.getDocument();
		int startPos, endPos;

		try {

			int pos = caretPos;
			char c;

			while (pos >= 0) {
				c = doc.getChar(pos);
				if (!Character.isJavaIdentifierPart(c))
					break;
				--pos;
			}

			startPos = pos;

			pos = caretPos;
			int length = doc.getLength();

			while (pos < length) {
				c = doc.getChar(pos);
				if (!Character.isJavaIdentifierPart(c))
					break;
				++pos;
			}

			endPos = pos;
			selectRange(startPos, endPos);
			return true;

		} catch (BadLocationException x) {
		}

		return false;
	}

	private void selectRange(int startPos, int stopPos) {
		int offset = startPos + 1;
		int length = stopPos - offset;
		fText.setSelectedRange(offset, length);
	}
}