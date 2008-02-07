/*******************************************************************************
 * Copyright (c) 2004 Andrei Loskutov.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the BSD License
 * which accompanies this distribution, and is available at
 * http://www.opensource.org/licenses/bsd-license.php
 * Contributor:  Andrei Loskutov - initial API and implementation
 *******************************************************************************/

package org.gotpike.pdt.util;

import java.lang.reflect.Method;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.editors.text.IStorageDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorExtension2;
import org.gotpike.pdt.PDTPlugin;

/**
 * @author Andrei
 */
public class AbstractEditor implements ITextEditorExtension2 {
    private IEditorPart editorPart;
    private boolean multipage;

    /**
     * Proxy for different editor types
     */
    public AbstractEditor(IEditorPart editorPart) {
        super();
       if(editorPart!= null
                && !(editorPart instanceof ITextEditor)) {
            /*
             * added to support different multipage editors which are not extending
             * MultiPageEditorPart, like adobe Flex family editors
             */
            try {
                Method[] declaredMethods = editorPart.getClass().getDeclaredMethods();
                for (int i = 0; i < declaredMethods.length; i++) {
                    Method method = declaredMethods[i];
                    String methodName = method.getName();
                    if(("getActiveEditor".equals(methodName)
                            // lines below are for Flex 3, above for Flex 2
                            ||  "getCodeEditor".equals(methodName)
                            ||  "getTextEditor".equals(methodName))
                            && method.getParameterTypes().length == 0){
                        method.setAccessible(true);
                        editorPart = (IEditorPart) method.invoke(editorPart, null);
                        if(editorPart == null) {
                            continue;
                        }
                        multipage = true;
                        break;
                    }
                }
            } catch (Exception e) {
                PDTPlugin.logError("Can't get current page", e);
            }
        }
        this.editorPart = editorPart;
    }

    public boolean isMultiPage(){
        return multipage;
    }

    /**
     * @return may return null
     */
    public IDocumentProvider getDocumentProvider() {
        if (editorPart == null) {
            return null;
        }
        if (editorPart instanceof ITextEditor) {
            return ((ITextEditor) editorPart).getDocumentProvider();
        }

        IDocumentProvider docProvider =
            (IDocumentProvider) editorPart.getAdapter(IDocumentProvider.class);
        return docProvider;
    }

    /**
     * @return may return null
     */
    public IEditorInput getEditorInput() {
        if (editorPart == null) {
            return null;
        }
        return editorPart.getEditorInput();
    }

    /**
     * @return may return null
     */
    public IFile getFile(){
        IEditorInput input = getEditorInput();
        if(input == null){
            return null;
        }
        Object adapter = input.getAdapter(IFile.class);
        if(adapter instanceof IFile){
            return (IFile) adapter;
        }
        adapter = getAdapter(IFile.class);
        if(adapter instanceof IFile){
            return (IFile) adapter;
        }
        return null;
    }

    public String computeEncoding() {
        IDocumentProvider provider = getDocumentProvider();
        if(provider instanceof IStorageDocumentProvider) {
            IStorageDocumentProvider docProvider = (IStorageDocumentProvider) provider;
            String encoding = docProvider.getEncoding(getEditorInput());
            return encoding;
        }
        return null;
    }

    public ISelectionProvider getSelectionProvider() {
        if (editorPart == null) {
            return null;
        }
        if (editorPart instanceof ITextEditor) {
            return ((ITextEditor) editorPart).getSelectionProvider();
        }
        // PDEMultiPageEditor doesn't implement ITextEditor interface
        if (editorPart instanceof ISelectionProvider) {
            return (ISelectionProvider) editorPart;
        }
        Object adapter = editorPart.getAdapter(ISelectionProvider.class);
        if(adapter instanceof ISelectionProvider){
            return (ISelectionProvider) adapter;
        }
        return null;
    }

    public IDocument getDocument() {
        IDocumentProvider provider = getDocumentProvider();
        if (provider != null) {
            return provider.getDocument(getEditorInput());
        }
        return null;
    }

    public void selectAndReveal(int lineNumber){
        if (editorPart == null) {
            return;
        }
        if (editorPart instanceof ITextEditor) {
            ITextEditor editor = (ITextEditor)editorPart;
            IDocument document = getDocument();
            if(document != null){
                IRegion lineInfo = null;
                try {
                    // line count internaly starts with 0, and not with 1 like in GUI
                    lineInfo = document.getLineInformation(lineNumber - 1);
                } catch (BadLocationException e) {
                    //e.printStackTrace();
                    //ignored because line number may not really exist in document, we guess this...
                    //AnyEditToolsPlugin.error(null, e);
                }
                if(lineInfo != null){
                    editor.selectAndReveal(lineInfo.getOffset(), lineInfo.getLength());
                }
            }
        }
    }

    public boolean isDirty(){
        if (editorPart == null) {
            return false;
        }
        return editorPart.isDirty();
    }

    private Object getAdapter(Class clazz){
        if (editorPart == null) {
            return null;
        }
        return editorPart.getAdapter(clazz);
    }

    public void doSave(IProgressMonitor moni){
        if (editorPart == null) {
            return;
        }
        editorPart.doSave(moni);
    }

    public boolean isEditorInputModifiable() {
        if (editorPart == null) {
            return false;
        }
        if (editorPart instanceof ITextEditorExtension2) {
            return ((ITextEditorExtension2)editorPart).isEditorInputModifiable();
        }
        return true;
    }

    public boolean validateEditorInputState() {
        if (editorPart == null) {
            return false;
        }
        if (editorPart instanceof ITextEditorExtension2) {
            return ((ITextEditorExtension2)editorPart).validateEditorInputState();
        }
        return true;
    }

    /**
     * Sets the sequential rewrite mode of the viewer's document.
     */
    public void stopSequentialRewriteMode(DocumentRewriteSession session) {
        IDocument document = getDocument();
        if (document instanceof IDocumentExtension4) {
            IDocumentExtension4 extension= (IDocumentExtension4) document;
            extension.stopRewriteSession(session);
        } else if (document instanceof IDocumentExtension) {
            IDocumentExtension extension= (IDocumentExtension)document;
            extension.stopSequentialRewrite();
        }

        Object adapter = getAdapter(IRewriteTarget.class);
        if (adapter instanceof IRewriteTarget) {
            IRewriteTarget target = (IRewriteTarget) adapter;
            target.endCompoundChange();
            target.setRedraw(true);
        }
    }

    /**
     * Starts  the sequential rewrite mode of the viewer's document.
     * @param normalized <code>true</code> if the rewrite is performed
     * from the start to the end of the document
     */
    public DocumentRewriteSession startSequentialRewriteMode(boolean normalized) {
        DocumentRewriteSession rewriteSession = null;
        // de/activate listeners etc, prepare multiple replace
        Object adapter = getAdapter(IRewriteTarget.class);
        if (adapter instanceof IRewriteTarget) {
            IRewriteTarget target = (IRewriteTarget) adapter;
            target.setRedraw(false);
            target.beginCompoundChange();
        }

        IDocument document = getDocument();
        if (document instanceof IDocumentExtension4) {
            IDocumentExtension4 extension= (IDocumentExtension4) document;
            if (normalized) {
                rewriteSession = extension
                        .startRewriteSession(DocumentRewriteSessionType.STRICTLY_SEQUENTIAL);
            } else {
                rewriteSession = extension
                        .startRewriteSession(DocumentRewriteSessionType.SEQUENTIAL);
            }
        } else if (document instanceof IDocumentExtension) {
            IDocumentExtension extension = (IDocumentExtension) document;
            extension.startSequentialRewrite(normalized);
        }
        return rewriteSession;
    }

    /**
     * clean reference to wrapped "real" editor object
     */
    public void dispose(){
        editorPart = null;
    }

    public int hashCode() {
        IDocumentProvider provider = getDocumentProvider();
        IEditorInput input = getEditorInput();
        int code = 0;
        if(provider != null){
            code += provider.hashCode();
        }
        if(input != null){
            code += input.hashCode();
        }
        return code;
    }

    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }
        if(!(obj instanceof AbstractEditor)){
            return false;
        }
        AbstractEditor other = (AbstractEditor) obj;
        if(this.editorPart != other.editorPart){
            return false;
        }
        // now check for multi page stuff
        if(!isMultiPage()){
            return true;
        }
        return this.hashCode() == other.hashCode();
    }
}
