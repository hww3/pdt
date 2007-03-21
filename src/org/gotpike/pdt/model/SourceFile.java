package org.gotpike.pdt.model;

import java.io.FileReader;
import java.io.StringReader;
import java.util.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.gotpike.pdt.PDTPlugin;
import org.gotpike.pdt.editors.PikePartitioner;
import org.gotpike.pdt.parser.*;

/**
 * A parsed Pike source file. This class provides access to 
 * {@link org.epic.core.model.ISourceElement}s recognised in a Pike
 * source file.
 * 
 * @author jploski
 */
public class SourceFile
{
    private final ListenerList listeners = new ListenerList(1);
    private final ILog log;
    private final IDocument doc;
    private List docs;
    private List classes;
    
    /**
     * Creates a SourceFile which will be reflecting contents of the given
     * source document. As a second step of initialisation, {@link #parse}
     * has to be called.
     */
    public SourceFile(ILog log, IDocument doc)
    {
        assert log != null;
        assert doc != null;
        this.log = log;
        this.doc = doc;
        this.classes = new ArrayList();
        this.docs = new ArrayList();
    }
    
    /**
     * Adds a listener for changes of this SourceFile.
     * Has no effect if an identical listener is already registered.
     */
    public synchronized void addListener(ISourceFileListener listener)
    {
        listeners.add(listener);
    }

    /**
     * @return the source document based on which this SourceFile was created;
     *         note that depending on the time when this method is called,
     *         the document may be more up to date than the information
     *         provided by this SourceFile instance
     */
    public IDocument getDocument()
    {
        return doc;
    }
    
    /**
     * @return a list of {@link Class} instances representing package
     *         scopes within the source file
     */
    public List getClasses()
    {
        return Collections.unmodifiableList(classes);
    }
    
    /**
     * @return an iterator over {@link AutoDocComment} instances representing
     *         POD comments found in the source, in their original order  
     */
    public Iterator getDocs()
    {
        return Collections.unmodifiableList(docs).iterator();
    }

    /**
     * @return an iterator over {@link Method} instances representing
     *         subroutines found in the source, in their original order  
     */
    public Iterator getMethods()
    {
        return new MethodIterator();
    }
    
    /**
     * @return an iterator over {@link Inherit} instances representing
     *         'use module' statements found in the source, in their original order  
     */
    public Iterator getInherits()
    {
        return new InheritIterator();
    }
    
    public synchronized void parse()
    {
    	System.out.println("whee!");
 /*       this.classes = new ArrayList();
        this.docs = new ArrayList();
        
        PikePartitioner partitioner = (PikePartitioner) doc.getDocumentPartitioner();
        if (partitioner == null) return;
        synchronized (partitioner.getTokensLock())
        {
            try
            {
                ParsingState state = new ParsingState(partitioner.getTokens());

                while (state.hasMoreTokens()) state.processToken();
                state.finish();
            }
            catch (BadLocationException e)
            {
                log.log(new Status(
                    Status.ERROR,
                    PDTPlugin.getPluginId(),
                    IStatus.OK,
                    "Unexpected exception: " + e.getClass().getName() +
                    "; report it as a bug " +
                    "in plug-in " + PDTPlugin.getPluginId(),
                    e));
            }
        }
        */
 /*   	 IWorkbench wb = PlatformUI.getWorkbench();
    	 IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
    	 IWorkbenchPage page = win.getActivePage();
    	 IEditorPart editor = page.getActiveEditor();
    	 IFile original = ((FileEditorInput)editor.getEditorInput()).getFile();
         IDocument edoc = ((TextEditor)editor).getDocumentProvider().getDocument(null);
    	 if(edoc == doc)
    	 {
    		 System.out.println(original.getName());
    		 System.out.println("whee!");
    	 }
*/
    	PikeScanner s = new PikeScanner(new StringReader(doc.get()) /*, original.getName()*/);
        parser p = new parser(s);
        p.source = this;
        
        try {
			this.addClass();
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try {
			p.parse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        fireSourceFileChanged();
    }
    
    /**
     * Removes the given listener from this SourceFile.
     * Has no affect if an identical listener is not registered.
     */
    public synchronized void removeListener(ISourceFileListener listener)
    {
        listeners.remove(listener);
    }
    
    public void addMethod(PikeSymbol keyword, PikeSymbol name, CurlySymbol front)
    {
    	Class cls = (Class)this.classes.get(0);
        cls.addMethod(keyword, name, front);
    }
    
    private void addDoc(PikeSymbol docStart, PikeSymbol docEnd)
        throws BadLocationException
    {
        docs.add(new AutoDocComment(docStart, docEnd));
    }
    
    private void addClass(PikeSymbol docStart, PikeSymbol docEnd)
    throws BadLocationException
{
        classes.add(new Class());
}
    
    private void addClass()
    throws BadLocationException
{
    classes.add(new Class());
}
    private void fireSourceFileChanged()
    {
        Object[] listeners = this.listeners.getListeners();
        for (int i = 0; i < listeners.length; i++)
            ((ISourceFileListener) listeners[i]).sourceFileChanged(this);
    }
   /* 
    private class ParsingState
    {
        private final int tokenCount;
        private final List tokens;
        private int tIndex;
        private PikeSymbol t;
        private int type;
        private int blockLevel;
        
        private Stack classStack;
        private Stack methodStack;
        private PikeSymbol docStart;
        private PikeSymbol methodName;
        private PikeSymbol className;
        
        public ParsingState(List tokens)
        {
            this.tIndex = 0;
            this.tokens = tokens;
            this.tokenCount = tokens.size();
            this.classStack = new Stack();
            this.methodStack = new Stack();
        }
        
        public void finish()
        {
            closeClass();
            closeMethod();
        }
        
        public boolean hasMoreTokens()
        {
            return tIndex < tokenCount;
        }
        
        public void processToken() throws BadLocationException
        {
            this.t = (PikeSymbol) tokens.get(tIndex);
            this.type = t.getType();
            
            updateBlockLevel();
            updateClassState();
            updateMethodState();
            updateInheritState();
            updateDocState();
            
            tIndex++;
        } 
        
        private void closeClass()
        {            
            if (classStack.isEmpty()) return;

            Class cclass = (Class) classStack.peek();
            if (blockLevel > cclass.getBlockLevel()) return;
            //System.err.println("closePackage " + pkg.getName() + " " + t);
            classStack.pop();
            cclass.setLastToken((PikeSymbol) tokens.get(tIndex-1));
        }
        
        private void closeMethod()
        {
            if (methodStack.isEmpty()) return;
            
            Method method = (Method) methodStack.peek();
            if (blockLevel-1 > method.getBlockLevel()) return;
            methodStack.pop();
            if (t instanceof CurlySymbol) // could be false on finish()
                method.setCloseCurly((CurlySymbol) t);
        }
        
        private Class getCurrentClass()
        {
            if (classStack.isEmpty()) openClass(new Class());
            return (Class) classStack.peek();
        }
        
        private void openClass(Class pkg)
        {
            //System.err.println("openPackage " + pkg.getName() + " " + t);
            classStack.push(pkg);
            classes.add(pkg);
        }
        
        private void updateBlockLevel()
        {
            if (type == sym.LBRACE) blockLevel++;
            else if (type == sym.RBRACE)
            {
                closeMethod();
                closeClass();
                blockLevel--;
            }
        }
        
        private void updateMethodState() throws BadLocationException
        {
            if (subKeyword == null)
            {
                if (type == sym.KEYWORD_SUB) subKeyword = t;
            }
            else
            {
                if (methodName == null && type == sym.TOK_IDENTIFIER)
                {
                    methodName = t;
                }
                else if (type == sym.SEMI)
                {
                    subKeyword = null;
                    subName = null;
                }
                else if (type == sym.OPEN_CURLY)
                {
                    if (subName != null)
                    {
                        Method method = getCurrentClass().addMethod(
                            subKeyword, methodName, (CurlySymbol) t);
                        methodStack.push(sub);
                    }
                    subKeyword = null;
                    subName = null;
                }
            }
        }
        
        private void updateClassState()
        {
            if (classKeyword == null)
            {
                if (type == sym.KEYWORD_PACKAGE)
                {
                    closeClass();
                    classKeyword = t;
                }
            }
            else
            {
                if (type == sym.WORD)
                {
                    openClass(new Class(
                        classes.size(), blockLevel, classKeyword, t));
                    classKeyword = null;
                }
            }
        }
        
        private void updateDocState() throws BadLocationException
        {
            if (docStart == null)
            {
                if (type == sym.OPEN_POD) docStart = t;
            }
            else
            {
                if (type == sym.CLOSE_POD)
                {
                    addDoc(docStart, t);
                    docStart = null;
                }
            }
        }
        
        private void updateInheritState() throws BadLocationException
        {
            if (inheritKeyword == null)
            {
                if (type == sym.KEYWORD_USE) inheritKeyword = t;
            }
            else
            {
                if (type == sym.WORD)
                {
                    String text = t.getText();
                    if (!"constant".equals(text) &&
                        !"warnings".equals(text) &&
                        !"strict".equals(text) &&
                        !"vars".equals(text))
                    {
                        getCurrentClass().addClass(inheritKeyword, t);
                    }
                    inheritKeyword = null;
                }
            }
        }
    }*/
    
    private class MethodIterator implements Iterator
    {
        private Iterator pkgIterator;
        private Iterator subIterator;
        
        public MethodIterator()
        {
            pkgIterator = classes.iterator();
        }
        
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext()
        {
            while (subIterator == null || !subIterator.hasNext())
            {
                if (pkgIterator.hasNext())
                {
                    Class pkg = (Class) pkgIterator.next();
                    subIterator = pkg.getMethods().iterator();
                }
                else return false;
            }
            return true;
        }

        public Object next()
        {
            return subIterator.next();
        }
    }

    
    private class InheritIterator implements Iterator
    {
        private Iterator pkgIterator;
        private Iterator subIterator;
        
        public InheritIterator()
        {
            pkgIterator = classes.iterator();
        }
        
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext()
        {
            while (subIterator == null || !subIterator.hasNext())
            {
                if (pkgIterator.hasNext())
                {
                    Class pkg = (Class) pkgIterator.next();
                    subIterator = pkg.getInherits().iterator();
                }
                else return false;
            }
            return true;
        }

        public Object next()
        {
            return subIterator.next();
        }
    }
    
}
