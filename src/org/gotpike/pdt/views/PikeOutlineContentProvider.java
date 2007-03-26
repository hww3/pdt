package org.gotpike.pdt.views;

import java.util.*;

import org.eclipse.jface.viewers.*;

import org.gotpike.pdt.model.Class;
import org.gotpike.pdt.model.IClassElement;
import org.gotpike.pdt.model.ISourceFileListener;
import org.gotpike.pdt.model.Inherit;
import org.gotpike.pdt.model.Method;
import org.gotpike.pdt.model.SourceFile;

public class PikeOutlineContentProvider implements ITreeContentProvider
{   
    private static final Object[] EMPTY_ARRAY = new Object[0];

    private final List prevSubsContent;
    private final List prevUsesContent;
    private final ISourceFileListener listener = new ISourceFileListener() {
        public void sourceFileChanged(SourceFile source)
        {
            PikeOutlineContentProvider.this.modelChanged();
        } };

    private SourceFile model;
    private TreeViewer viewer;
    
    public PikeOutlineContentProvider()
    {
        this.prevSubsContent = new ArrayList();
        this.prevUsesContent = new ArrayList();
    }

    public void dispose()
    {
        if (model != null) model.removeListener(listener);
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
    {
        if (oldInput instanceof SourceFile)
            ((SourceFile) oldInput).removeListener(listener);

        this.model = (SourceFile) newInput;
        this.viewer = (TreeViewer) viewer;

        rememberContent();
        
        if (model != null) model.addListener(listener);
    }

    public Object[] getChildren(Object parentElement)
    {    	
        if (parentElement instanceof SourceFile)
        {
            return model.getClasses().toArray();
        }
        else if (parentElement instanceof Class)
        {
            ArrayList x = new ArrayList();
            
            x.addAll(((Class)parentElement).getInherits());
            x.addAll(((Class)parentElement).getMethods());
            x.addAll(((Class)parentElement).getClasses());
            x.addAll(((Class)parentElement).getConstants());
            
            Object[] y = x.toArray();
            return y;
        }
 
        return EMPTY_ARRAY;
    }

    public Object getParent(Object element)
    {
        if (element instanceof Method)
            return ((Method) element).getParent();
        else if (element instanceof Inherit)
            return ((Inherit) element).getParent();
        else if (element instanceof ClassElem)
            return ((ClassElem) element).pkg;
        else if (element instanceof Package)
            return model;

        return null;
    }

    public boolean hasChildren(Object element)
    {
        return getChildren(element).length > 0;
    }

    public Object[] getElements(Object inputElement)
    {
        return getChildren(inputElement);
    }
    
    /**
     * @return true if the outline page's  differs from its previous
     *         content; false otherwise
     */
    private boolean contentChanged()
    {
    	return true;
    	
/*        return
            packageContentChanged(model.getMethods(), prevSubsContent.iterator()) ||
            packageContentChanged(model.getInherits(), prevUsesContent.iterator());
            */
    }
    
    private boolean packageContentChanged(Iterator curContent, Iterator prevContent)
    {
        while(curContent.hasNext() && prevContent.hasNext())
        {
            IClassElement curElem = (IClassElement) curContent.next();
            IClassElement prevElem = (IClassElement) prevContent.next();
            
            if (packageElementsDiffer(curElem, prevElem))                
            {
                return true;
            }
        }
        return curContent.hasNext() != prevContent.hasNext();
    }
    
    private boolean packageElementsDiffer(IClassElement curElem, IClassElement prevElem)
    {
        return
            !curElem.getName().equals(prevElem.getName()) ||
            curElem.getOffset() != prevElem.getOffset() ||
            !curElem.getParent().getName().equals(prevElem.getParent().getName());
    }
    
    private void modelChanged()
    {
        if (contentChanged())
        {
            updateViewer();
            rememberContent();
        }
    }
    
    /**
     * Caches the content of the outline page derived from the model.
     * This is necessary to avoid calling {@link #updateViewer} every
     * time the model changes insignificantly.
     */
    private void rememberContent()
    {
        prevSubsContent.clear();
        prevUsesContent.clear();
        
        if (model != null)
        {
        	/*
            for (Iterator i = model.getMethods(); i.hasNext();)
                prevSubsContent.add(i.next());        
            
            for (Iterator i = model.getInherits(); i.hasNext();)
                prevUsesContent.add(i.next());
*/
        }
    }
    
    /**
     * Loads the current contents of the outline page into the tree viewer
     * and expands its nodes. This is an expensive operation, especially
     * under Windows where it results in a visible and annoying redrawing.
     */
    private void updateViewer()
    {
    	System.out.println("UpdateViewer()");
        viewer.refresh();
        viewer.expandToLevel(3);
    }
    
    public static class ClassElem
    {
        public final Class pkg;
        public final String name;
        
        public ClassElem(Class pkg, String name)
        {
            this.pkg = pkg;
            this.name = name;
        }
        
        public boolean equals(Object obj)
        {
            if (!(obj instanceof Class)) return false;
            
            Class pkg = (Class) obj;
            return pkg.equals(this.pkg) && name.equals(this.name);
        }
        
        public int hashCode()
        {
            return pkg.hashCode() * 37 + name.hashCode();
        }
        
        public String toString()
        {
            return name;
        }
    }
}
