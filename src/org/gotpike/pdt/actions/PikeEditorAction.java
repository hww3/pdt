package org.gotpike.pdt.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorActionDelegate;

import org.gotpike.pdt.PDTPlugin;
import org.gotpike.pdt.editors.PikeEditor;


/**
 * Abstract base class for actions operating in context of a PikeEditor.
 *
 * @author jploski
 */
public abstract class PikeEditorAction extends Action
{
    //~ Instance fields

    private PikeEditor editor;

    //~ Constructors

    protected PikeEditorAction(PikeEditor editor)
    {
        assert editor != null;
        this.editor = editor;

        setId(getPikeEditorActionId());
    }

    //~ Methods

    public final void run()
    {
        doRun();
    }

    protected abstract void doRun();

    /**
     * Invoked by PikeEditor to dispose of the action instance. Subclasses may override this method
     * to provide clean-up.
     */
    public void dispose()
    {
        // empty impl
    }

    /**
     * @return a constant from PikeEditorActionIds which identifies this action
     */
    protected abstract String getPikeEditorActionId();

    /**
     * @return the PikeEditor in which the action operates
     */
    protected final PikeEditor getEditor()
    {
        return editor;
    }

    protected final void log(IStatus status)
    {
        getLog().log(status);
    }

    /**
     * @return the log that could be used for reporting problems during the action
     */
    protected final ILog getLog()
    {
        return PDTPlugin.getDefault().getLog();
    }

    protected final String getPluginId()
    {
        return PDTPlugin.getPluginId();
    }

    /**
     * @return returns the resource on which to create the marker, or <code>null</code> if there is
     *         no applicable resource.
     */
    protected final IResource getResource()
    {
        return (IResource) ((IAdaptable) editor.getEditorInput()).getAdapter(IResource.class);
    }
}
