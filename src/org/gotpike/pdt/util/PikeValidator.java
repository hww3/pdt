package org.gotpike.pdt.util;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.*;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorDescriptor;
import org.gotpike.pdt.Constants;
import org.gotpike.pdt.util.PikeExecutor;
import org.gotpike.pdt.PDTPlugin;

/**
 * PerlValidator is a singleton class, use PerlValidator.instance() to obtain
 * an instance of it. 
 * 
 * TODO: document this class; much refactoring still needed
 * 
 * @author luelljoc
 * @author jploski
 */
public class PikeValidator extends PikeValidatorBase
{
    private static PikeValidator instance;    
    
    private PikeValidator()
    {
        super(PDTPlugin.getDefault().getLog(), new PikeExecutor(true));
    }
    
    /**
     * @return the PerlValidator singleton
     */
    public synchronized static PikeValidator instance()
    {
        if (instance == null) instance = new PikeValidator();
        return instance;
    }
	
    /**
     * Validates source code of the specified resource (through "perl -c")
     * and appropriately updates problem markers on this and possibly also
     * related resources.
     * 
     * @param resource
     *        resource to validate (source code will be read from disk)
     * @return true if the resource was validated,
     *         false if it was not validated because it did not require validation
     * @exception java.io.IOException
     *            if the source text of the resource could not be read
     */
	public synchronized boolean validate(IResource resource)
        throws CoreException, IOException
    {
		IEditorDescriptor defaultEditorDescriptor =
			PDTPlugin
				.getDefault()
				.getWorkbench()
				.getEditorRegistry()
				.getDefaultEditor(resource.getFullPath().toString());

		if (defaultEditorDescriptor == null) return false;

		if (!defaultEditorDescriptor.getId().equals(Constants.PIKE_EDITOR_ID))
        {
			return false;
		}
        
        // if workspace is out-of-sync with the file system:
        if (!resource.exists()) return false; 

		validate(resource, readSourceFile(resource));
        return true;
	} 

    protected void addMarker(IResource resource, Map attributes)
    {
        new MarkerUtil(resource).addMarker(attributes, Constants.PROBLEM_MARKER);
    }
    
    protected void clearAllUsedMarkers(IResource resource)
    {
        new MarkerUtil(resource).clearAllUsedFlags(Constants.PROBLEM_MARKER);
    }
    
    protected IResource getErrorResource(ParsedErrorLine line, IResource resource)
    {
        IResource ret = super.getErrorResource(line, resource);
        if (ret != null) return ret;
        
        // Figure out the project relative path of the resource which contains
        // an error:
        IPath projectRoot = resource.getProject().getLocation();
        String projectRootPath = projectRoot.toOSString();
        String errorResourcePath = line.getPath();
        
        if (errorResourcePath.startsWith(projectRootPath))
            errorResourcePath = errorResourcePath.substring(projectRootPath.length());
        else
            return resource; // resource with error is outside of the project root?
            
        IResource errorResource =
            resource.getProject().findMember(new Path(errorResourcePath));

        // if the resource referred to by the error message is not found,
        // we shall still attach the marker to the currently validated resource
        return errorResource != null ? errorResource : resource;
    }
    
    protected List getPikeArgs()
    {
        List args = super.getPikeArgs();        
    
        if (PDTPlugin.getDefault().getWarningsPreference())
            args.add("-w");
    
    
        return args;
    }
    
    protected boolean isProblemMarkerPresent(
        ParsedErrorLine line, IResource resource)
    {
        return new MarkerUtil(resource).isMarkerPresent(
            Constants.PROBLEM_MARKER, line.getLineNumber(), line.getMessage(), true);
    }
    
    protected void removeUnusedMarkers(IResource resource)
    {
        MarkerUtil util = new MarkerUtil(resource);
        util.removeObsoleteProblemMarkers(); // TODO: remove when no longer needed
        util.removeUnusedMarkers(Constants.PROBLEM_MARKER);
    }
    
    /**
     * @return true only if there is no existing marker with SEVERITY_ERROR
     *         on the given line
     */
    protected boolean shouldUnderlineError(IResource resource, int lineNr)
    {
        List markers = MarkerUtil.getMarkersForLine(resource, lineNr);
        
        for (Iterator i = markers.iterator(); i.hasNext();)
        {
            IMarker marker = (IMarker) i.next();
            try
            {
                Integer severity = (Integer) marker.getAttribute(IMarker.SEVERITY);
                if (severity != null && severity.intValue() == IMarker.SEVERITY_ERROR)
                    return false;
            }
            catch (CoreException e)
            {
                // thrown if the marker does not exist
                // it does not interest us then
            }
        }
        return true;
    }
}