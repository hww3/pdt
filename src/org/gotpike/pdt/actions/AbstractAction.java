/*******************************************************************************
 * Copyright (c) 2004 Andrei Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD License
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/bsd-license.php
 * Contributor:  Andrei Loskutov - initial API and implementation
 *******************************************************************************/
package org.gotpike.pdt.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.gotpike.pdt.editors.PikeEditor;
import org.gotpike.pdt.util.AbstractEditor;
import org.gotpike.pdt.util.EclipseUtils;

/**
 * @author Andrei
 * TODO Documentation
 */
public abstract class AbstractAction implements IActionDelegate, IWorkbenchWindowActionDelegate {

    protected AbstractEditor editor;
    private IFile file;
    private IWorkbenchWindow window;

    public AbstractAction() {
        super();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action) {
        if(action == null){
            return;
        }
//        isCommand = id.endsWith(IAnyEditConstants.COMMAND_ACTION_FLAG);
        setEditor(new AbstractEditor( EclipseUtils.getActiveEditor() ) );
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#dispose()
     */
    public void dispose() {
        if(editor != null){
            editor.dispose();
            editor = null;
        }
        window = null;
    }

    protected void setEditor(AbstractEditor editor) {
        if(getEditor() != null){
            getEditor().dispose();
        }
        this.editor = editor;
    }

    protected AbstractEditor getEditor() {
        return editor;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchWindowActionDelegate#init(org.eclipse.ui.IWorkbenchWindow)
     */
    public void init(IWorkbenchWindow window1) {
        window = window1;
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection) {
        // unused
    }

    /**
     * @return could return null, if we do not have associated file and operating on
     * editor inputs instead
     */
    public IFile getFile() {
        IFile myFile = file;
        if(myFile == null && getEditor() != null){
            myFile = getEditor().getFile();
        }
        return myFile;
    }

    /**
     * @param file to perform operation on
     */
    public void setFile(IFile file) {
        this.file = file;
    }

    /**
     * @return may be null if this action is not yet initialized
     */
    public IWorkbenchWindow getWindow(){
        return window;
    }
}
