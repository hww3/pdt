package org.gotpike.pdt.editors;

import org.eclipse.core.internal.utils.ArrayIterator;
import org.eclipse.core.runtime.ILog;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.gotpike.pdt.PDTPlugin;
import org.gotpike.pdt.model.IMultilineElement;
import org.gotpike.pdt.model.SourceFile;
import org.gotpike.pdt.preferences.PreferenceConstants;
import org.gotpike.pdt.util.StatusFactory;
import org.gotpike.pdt.model.Class;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 *Responsible for keeping folds in sync with a source file's text within a PerlEditor. This class
 *relies on {@link org.gotpike.pdt.model.SourceFile} to obtain positions of foldable
 *{@link org.gotpike.pdt.model.SourceElement}s.
 *
 *@author jploski
 */
public class FoldReconciler
{
    //~ Instance fields

    private final PikeEditor editor;

    private final Set folds; // of Annotation instances

    private boolean initialized = false;

    //~ Constructors

    /**
     * Creates a FoldReconciler for the given editor.
     */
    public FoldReconciler(PikeEditor editor)
    {
        this.editor = editor;
        this.folds = new HashSet();
    }

    //~ Methods

    /**
     * Updates folds based on the current state of the editor's SourceFile. An invocation results in
     * removing/adding zero or more fold annotations to the document's annotation model.
     *
     * @see PerlEditor#getSourceFile()
     * @see org.eclipse.jface.text.source.projection.ProjectionAnnotationModel
     */
    public void reconcile()
    {
        if (! isFoldingEnabled()) { return; }

        try
        {
            IAnnotationModel annotations = getAnnotations();
            if (annotations == null) { return; }

            Set tuples = computeFoldPositions();

            removeFolds(tuples);
            addFolds(tuples);

            /*
             * this should probably be handled via some kind of initialization that occurs in the
             * constructor to set up the initial folds. due to the way the editor calls this method
             * after the class has been instanciated, this achieves the desired behavior
             */
            initialized = true;
        }
        catch (BadLocationException e)
        {
            // this one should never occur
            String pluginId = PDTPlugin.getPluginId();
            getLog().log(StatusFactory.createError(pluginId, "Unexpected exception; report it as "
                    + "a bug in plug-in " + pluginId, e));
        }
    }

    protected ILog getLog()
    {
        return PDTPlugin.getDefault().getLog();
    }

    protected SourceFile getSourceFile()
    {
        return editor.getSourceFile();
    }

    /**
     * Adds the specified set of new folds to the document's annotation model (and the <code>
     * folds</code> instance variable).
     *
     * @param tuples <code>Tuple</code> instances representing new folds
     */
    private void addFolds(Set tuples)
    {
        for (Iterator iter = tuples.iterator(); iter.hasNext();)
        {
            Tuple t = (Tuple) iter.next();
            if (! folds.contains(t))
            {
                getAnnotations().addAnnotation(t.annotation, t.position);
                folds.add(t);
            }
        }
    }

    /**
     * Computes fold positions for <code>SourceElement</code>s
     */
    private Set computeFoldPositions() throws BadLocationException
    {
        HashSet tuples = new HashSet();

        computeFoldPositions(tuples, getSourceFile().getDocs(),
            initialized ? false : isFoldPerldoc());

        List l = getSourceFile().getClasses();

        if(!l.isEmpty())
        {
        	Object c = l.get(0);
        	Class cls = (Class)c;
            Iterator x = cls.getMethods().iterator();
            computeFoldPositions(tuples, x,
                initialized ? false : isFoldSubroutines());
            x = cls.getClasses().iterator();
            computeFoldPositions(tuples, x,
                initialized ? false : isFoldSubroutines());
            x = cls.getClasses().iterator();
            while(x.hasNext())
            	computeFoldPositions((Class)x.next(), tuples);           
        }

        // TODO: add new fold position computations here

        return tuples;
    }

    private void computeFoldPositions(Class cls, Set t) throws BadLocationException
    {
        Class c;
              Iterator x = cls.getMethods().iterator();
              computeFoldPositions(t, x,
                initialized ? false : isFoldSubroutines());
            
            x = cls.getClasses().iterator();
            while(x.hasNext())
            	computeFoldPositions((Class)x.next(), t);

        // TODO: add new fold position computations here

    }
    
    /**
     * Computes fold elements for a given collection of <code>SourceElement</code>s
     *
     * @param tuples object <code>Tuple</code>s representing folds will be added to
     * @param elements iterator for a collection of <code>SourceElement</code>s
     * @param collapse true if fold is initially collapsed, false otherwise
     */
    private void computeFoldPositions(Set tuples, Iterator elements, boolean collapse)
        throws BadLocationException
    {
        IDocument doc = getSourceFile().getDocument();

        while (elements.hasNext())
        {
            IMultilineElement e = (IMultilineElement) elements.next();

            if (e.getStartLine() == e.getEndLine())
            {
         //   	System.out.println("single line element.");
                continue;
            }
       // 	System.out.println("multi line element.");

            int offset = doc.getLineOffset(e.getStartLine());
            int length =
                doc.getLineOffset(e.getEndLine()) - offset + doc.getLineLength(e.getEndLine());

            /*
             * store the position and annotation - the position is needed to create the fold, while
             * the annotation is needed to remove it
             */
            if(length > 1)
            {
              Tuple t = new Tuple(new Position(offset, length), new ProjectionAnnotation(collapse));
              tuples.add(t);
            }
        }
    }

    /**
     * @return the annotation model used for adding/removing folds
     */
    protected IAnnotationModel getAnnotations()
    {
        return (IAnnotationModel) editor.getAdapter(ProjectionAnnotationModel.class);
    }

    protected boolean getPreference(String name)
    {
        IPreferenceStore store = PDTPlugin.getDefault().getPreferenceStore();
        return store.getBoolean(name);
    }

    private boolean isFoldingEnabled()
    {
        return getPreference(PreferenceConstants.SOURCE_FOLDING);
    }

    private boolean isFoldPerldoc()
    {
        return getPreference(PreferenceConstants.AUTODOC_FOLDING);
    }

    private boolean isFoldSubroutines()
    {
        return getPreference(PreferenceConstants.SUBROUTINE_FOLDING);
    }

    /**
     * Removes no longer required folds from the document's annotation model (and the <code>
     * folds</code> instance variable). Removes positions of existing required folds from the
     * argument set.
     *
     * @param toRemove the set of Tuple instances representing required positions of folds according
     *                 to the current SourceFile of the editor; this set is updated by the method
     */
    private void removeFolds(Set toRemove)
    {
        for (Iterator iter = folds.iterator(); iter.hasNext();)
        {
            Tuple t = (Tuple) iter.next();
            Position p = getAnnotations().getPosition(t.annotation);

            if ((p != null) && (p.isDeleted() || ! toRemove.contains(t)))
            {
                getAnnotations().removeAnnotation(t.annotation);
                iter.remove();
            }
            else
            {
                // filter out any tuple instances that already exist
                toRemove.remove(t);
            }
        }
    }

    //~ Inner Classes

    /**
     *Fold data container
     */
    private class Tuple
    {
        Annotation annotation;
        Position position;

        Tuple(Position p, Annotation a)
        {
            this.position = p;
            this.annotation = a;
        }

        /*
         * @see java.lang.Object#equals(java.lang.Object)
         */
        public boolean equals(Object obj)
        {
            if (this == obj) { return true; }

            if (obj == null) { return false; }

            if (getClass() != obj.getClass()) { return false; }

            final Tuple other = (Tuple) obj;
            if (position == null)
            {
                if (other.position != null) { return false; }
            }
            else if (! position.equals(other.position))
            {
                return false;
            }

            return true;
        }

        /*
         * @see java.lang.Object#hashCode()
         */
        public int hashCode()
        {
            final int PRIME = 31;
            int result = 1;
            result = (PRIME * result) + ((position == null) ? 0 : position.hashCode());
            return result;
        }
    }

}
