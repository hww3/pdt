package org.gotpike.pdt.debug;

import java.util.logging.Logger;

import org.eclipse.jface.text.IRegion;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.console.IHyperlink;
import org.gotpike.pdt.util.FileUtil;

public class ExternalFileLink implements IHyperlink
{
    private String              fileName;
    private int                 fileLineNumber;
    private int                 fileColumnStart;
    private int                 fileColumnEnd;

    public ExternalFileLink(String fileName, int fileLineNumber, int fileColumnStart, int fileColumnEnd)
    {
        this.fileName = fileName;
        this.fileLineNumber = fileLineNumber;
        this.fileColumnStart = fileColumnStart;
        this.fileColumnEnd = fileColumnEnd;
    }

    public void linkActivated()
    {
        IEditorPart editor = FileUtil.openEclipseEditorForFile(fileName, false);
        FileUtil.selectEclipseEditorRegion(editor, fileLineNumber, fileColumnStart, fileColumnEnd);
    }

    public void linkEntered()
    {
    }

    public void linkExited()
    {
    }

	public IRegion getHyperlinkRegion() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getHyperlinkText() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getTypeLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public void open() {
		// TODO Auto-generated method stub
		
	}

}