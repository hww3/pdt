package org.gotpike.pdt.builders;

import java.util.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.gotpike.pdt.PDTPlugin;
import org.gotpike.pdt.ResourceMessages;
import org.gotpike.pdt.decorators.PikeDecorator;
import org.gotpike.pdt.util.PikeValidator;

/**
 * This low-priority job is started by PerlBuilder and executes
 * asynchronously to validate Perl files using PerlValidator.
 * Note that it can be cancelled and rescheduled by PerlBuilder
 * if another build request comes in the meantime.
 * 
 * @author jploski
 */
class PikeBuilderJob extends Job
{
    /**
     * Job family used to locate a running instance of this job.
     */
    public static final String JOB_FAMILY = "PerlBuilder.jobFamily";

    /**
     * A list of IResources representing Perl files that still need
     * to be validated during this job's run.
     */
    private final List dirtyResources;
    
    /**
     * A set of IResources representing Perl files that have been
     * validated during this job's run as well as their parent folders
     * (and their ancestors).
     */
    private final Set validatedResources;

    /**
     * @param dirtyResources
     *        a list of IResource instances representing Perl files
     *        that should be validated; the validation order is
     *        determined by the order of elements in this list 
     * @param validatedResources
     *        an initial set of IResource instances whose labels
     *        should be updated after this job is complete;
     *        further resources are added to this set during
     *        job's execution
     */
    public PikeBuilderJob(List dirtyResources, Set successResources)
    {
        super(ResourceMessages.getString("PikeBuilderJob.name"));
        
        this.validatedResources = successResources;
        this.dirtyResources = dirtyResources;
    }
    
    public boolean belongsTo(Object family)
    {
        return super.belongsTo(family) || JOB_FAMILY.equals(family);
    }

    /**
     * @return the remaining Perl files that still need to be validated
     */
    public Collection getDirtyResources()
    {
        return Collections.unmodifiableList(dirtyResources);
    }
    
    /**
     * See constructor's documentation.
     */
    public Collection getValidatedResources()
    {
        return Collections.unmodifiableSet(validatedResources);
    }
    
    protected IStatus run(IProgressMonitor monitor)
    {        
        monitor.beginTask("", dirtyResources.size());
        for (Iterator i = dirtyResources.iterator(); i.hasNext();)
        {
            if (monitor.isCanceled()) break;
            IResource resource = (IResource) i.next();
            monitor.subTask(resource.getProjectRelativePath().toString());
            buildResource(resource);
            i.remove();
            monitor.worked(1);
        }
        monitor.done();       

        PikeDecorator decorator = PikeDecorator.getPikeDecorator();
        if (decorator != null)
        {
            decorator.fireLabelEvent(
                new LabelProviderChangedEvent(
                    decorator, validatedResources.toArray()));
        }
        
        if (monitor.isCanceled()) return Status.CANCEL_STATUS;
        else return Status.OK_STATUS;
    }
    
    /**
     * Attempts to validate a single Perl file, schedules label update
     * for this file and its ancestor folders if validation was performed.
     */
    private void buildResource(IResource resource)
    {
        try
        {
            if (PikeValidator.instance().validate(resource))
            {
                validatedResources.add(resource);
                markParentFoldersUpdated(resource);
            }
        }
        catch (CoreException e)
        {
            PDTPlugin.getDefault().getLog().log(
                new MultiStatus(
                    PDTPlugin.getPluginId(),
                    IStatus.OK,
                    new IStatus[] { e.getStatus() },
                    "An unexpected exception occurred while validating " +
                    resource.getProjectRelativePath(),
                    e));
        }
        catch (Exception e)
        {   
            // TODO: it would be better to add info about the exception
            // as a special error marker on the resource...
            PDTPlugin.getDefault().getLog().log(
                new Status(Status.ERROR,
                    PDTPlugin.getPluginId(),
                    IStatus.OK,
                    "An unexpected exception occurred while validating " +
                    resource.getProjectRelativePath(),
                    e));
        }
    }
    
    private void markParentFoldersUpdated(IResource resource)
    {
        while ((resource = resource.getParent()) != null)
            if (resource.getType() == IResource.FOLDER)
                validatedResources.add(resource);
    }
}