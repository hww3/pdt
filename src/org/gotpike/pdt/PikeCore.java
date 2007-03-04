package org.gotpike.pdt;

import org.eclipse.core.resources.IProject;

/**
 * Provides access to PerlProject instances.
 * 
 * @author jploski
 */
public class PikeCore
{
    private PikeCore() { }
    
    /**
     * @return a PerlProject instance wrapping the given project;
     *         note that no check that the project has a Perl nature
     *         occurs during this invocation
     */
    public static PikeProject create(IProject project)
    {
        return new PikeProject(project);
    }
}