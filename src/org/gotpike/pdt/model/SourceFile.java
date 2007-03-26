package org.gotpike.pdt.model;

import java.io.FileReader;
import java.io.StringReader;
import java.util.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.gotpike.pdt.Constants;
import org.gotpike.pdt.PDTPlugin;
import org.gotpike.pdt.editors.PikeEditor;
import org.gotpike.pdt.editors.PikePartitioner;
import org.gotpike.pdt.parser.*;
import org.gotpike.pdt.util.MarkerUtil;
import org.gotpike.pdt.util.StatusFactory;

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
    private PikeEditor editor;
    
    // used during parsing to keep track of state.
    private Stack currentClass;
    public int currentModifiers;
    
    /**
     * Creates a SourceFile which will be reflecting contents of the given
     * source document. As a second step of initialisation, {@link #parse}
     * has to be called.
     */
    public SourceFile(ILog log, IDocument doc, PikeEditor editor)
    {
        assert log != null;
        assert doc != null;
        this.log = log;
        this.doc = doc;
        this.currentClass = new Stack();
        this.classes = new ArrayList();
        this.docs = new ArrayList();
        this.editor = editor;
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
    
    /**
     * @return an iterator over {@link Inherit} instances representing
     *         'use module' statements found in the source, in their original order  
     */
    
    public synchronized void parse()
    {
    	//System.out.println("whee!");
       this.classes = new ArrayList();
       this.docs = new ArrayList();
       this.currentClass.clear(); 
 /*       
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
        String fn = "unknown";
    	   IFile original = ((FileEditorInput)editor.getEditorInput()).getFile();
           fn = original.getFullPath().toOSString();
    	 
    	PikeScanner s = new PikeScanner(new StringReader(doc.get()), fn, this);
        parser p = new parser(s);
        p.source = this;
        
    	if(fn != null)
        	fn = original.getName();
        	int i = fn.indexOf('.');
        	if(i!= -1)
        		fn = fn.substring(0, i);

        	try {
        	
			this.addClass(fn);
		} catch (BadLocationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        try {
        	 MarkerUtil markerUtil = new MarkerUtil(original);         
             markerUtil.removeObsoleteProblemMarkers();
			p.parse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
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
    
    public Method addMethod(PikeSymbol keyword, PikeSymbol name, CurlySymbol front)
    {
    	Class cls = (Class)currentClass.peek();
        return cls.addMethod(keyword, name, front);
    }
    
    public Method addMethod(PikeSymbol keyword, PikeSymbol name, CurlySymbol front, CurlySymbol back)
    {
    	Method meth;
    	Class cls = (Class)currentClass.peek();
  //  	System.out.println("addMethod: " + front.toString() + " " + back.toString());
        meth = cls.addMethod(keyword, name, front);
        meth.setCloseCurly(back);
        return meth;
    }
    
    private void addDoc(PikeSymbol docStart, PikeSymbol docEnd)
        throws BadLocationException
    {
        docs.add(new AutoDocComment(docStart, docEnd));
    }
    
    
    public void addClass(String name)
    throws BadLocationException
{
    	
    Class cls = new Class(name);
    cls.setModifiers(currentModifiers);
    if(currentClass.empty())
    {
    	classes.add(cls);
    	cls.setTop();
    }
    else
    {
    	Class parent = (Class)currentClass.peek();
    	parent.addClass(cls);
    }
    currentClass.push(cls);
}
    public void endClass()
    {
    	currentClass.pop();
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

	public void addConstants(ConstantList list, int currentModifiers2) {
	//	System.out.println("adding " + list.list.size() + " constants.");
		for(int i = 0; i < list.list.size(); i++)
        {
        	addConstant((PikeSymbol) list.list.get(i), currentModifiers2);
        }
		
	}
    
	public Constant addConstant(PikeSymbol c, int modifiers)
	{
		Constant constant;
//	   	System.out.println("addConstant: " + c.toString() + " " + modifiers);
		Class cls = (Class)currentClass.peek();
    	constant = cls.addConstant(c, modifiers);
        
        return constant;
		
	}
	
	public void reportError(String message, String filename, int line, int column, int severity)
	{
	  handleError(message,filename,line,column,severity==1?true:false);
	}
	
	protected void handleError(String message, String filename, int line, int column, boolean isFatal)
	{
		IFile file = ((FileEditorInput)editor.getEditorInput()).getFile();
	    Map map = new HashMap();
	    MarkerUtilities.setLineNumber(map, line);
	    MarkerUtilities.setMessage(map, message);
	    map.put(IMarker.MESSAGE, message);
	    map.put(IMarker.LOCATION, file.getFullPath().toString());
        int offset = 0;
		try {
			offset = doc.getLineInformation(line-1).getOffset() + column;
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  //  Integer charStart = doc.getChar(offset);


        map.put(IMarker.CHAR_START, offset);

	 //   Integer charEnd = getCharEnd(line, column);
//	    if (charEnd != null)
        map.put(IMarker.CHAR_END, offset);

	    map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));

	    new MarkerUtil(file).addMarker(map, IMarker.PROBLEM);
	}
    
}
