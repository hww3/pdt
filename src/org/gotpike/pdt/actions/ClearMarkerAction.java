package org.gotpike.pdt.actions;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;


import org.gotpike.pdt.editors.PikeEditor;
import org.gotpike.pdt.editors.PikeEditorActionIds;
import org.gotpike.pdt.util.MarkerUtilities;


/**
 */
public abstract class ClearMarkerAction extends PikeEditorAction
{
    //~ Constructors

    protected ClearMarkerAction(PikeEditor editor)
    {
        super(editor);
    }

    //~ Methods

    public void selectionChanged(IAction action, ISelection selection)
    {
        // empty impl
    }

    protected abstract String getMarkerType();

    protected final void doRun()
    {
        scheduleJob(getMarkerType());
    }

    private void scheduleJob(final String markerType)
    {
        Job job =
            new Job("Clear EPIC Marker(s)")
        {
            protected IStatus run(IProgressMonitor monitor)
            {
                MarkerUtilities factory = new MarkerUtilities(getLog(), getPluginId());
                factory.deleteMarkers(getResource(), markerType);

                return Status.OK_STATUS;
            }
        };

        job.setSystem(true);
        job.schedule();
    }

    //~ Inner Classes

    /**
     */
    public static class AllMarkers extends ClearMarkerAction
    {

        public AllMarkers(PikeEditor editor)
        {
            super(editor);
        }

        protected String getMarkerType()
        {
            return "org.gotpike.pdt.markers.pdt";
        }

        protected String getPikeEditorActionId()
        {
            return PikeEditorActionIds.CLEAR_ALL_MARKERS;
        }
    }

    /**
     */
    public static class Critic extends ClearMarkerAction
    {
        public Critic(PikeEditor editor)
        {
            super(editor);
        }

        protected String getMarkerType()
        {
            return "org.gotpike.pdt.markers.critic";
        }

        protected String getPikeEditorActionId()
        {
            return PikeEditorActionIds.CLEAR_CRITIC_MARKERS;
        }
    }

    /**
     */
    public static class PodChecker extends ClearMarkerAction
    {
        public PodChecker(PikeEditor editor)
        {
            super(editor);
        }

        protected String getMarkerType()
        {
            return "org.epic.perleditor.markers.podChecker";
        }

        protected String getPikeEditorActionId()
        {
            return PikeEditorActionIds.CLEAR_POD_MARKERS;
        }
    }
}
