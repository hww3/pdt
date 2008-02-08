package org.gotpike.pdt.util;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ui.part.FileEditorInput;
import org.gotpike.pdt.PDTPlugin;


/**
 * @author luelljoc
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class FileUtilities
{
	public static FileEditorInput getFileEditorInput(IPath fPath)
    {
        IWorkspaceRoot root = PDTPlugin.getWorkspace().getRoot();

        try
        {
            IFile[] files = root.findFilesForLocation(fPath);
    		if (files.length > 0) return new FileEditorInput(files[0]); // found

            // not found, let's create a link to its parent folder
            // and search again
            createFolderLink(fPath, getPDTLinksProject(root));
    
    		files = root.findFilesForLocation(fPath);    
            if (files.length > 0) return new FileEditorInput(files[0]); // found
            
            // we have the link and the file still can't be found??
            throw new CoreException(new Status(
                IStatus.ERROR,
                PDTPlugin.getPluginId(),
                IStatus.OK,
                fPath.toOSString() + " could not be found through pdt-links", 
                null));
        }
        catch (CoreException e)
        {
            IStatus[] status;
            IPath folderPath = fPath.removeLastSegments(1);
            
            if (root.getLocation().isPrefixOf(folderPath) ||
                folderPath.isPrefixOf(root.getLocation()))    
            {
                status = new IStatus[] {
                    e.getStatus(),
                    new Status(
                        IStatus.ERROR,
                        PDTPlugin.getPluginId(),
                        IStatus.OK,
                        "PDT cannot access files located in folders on the path " +
                        "to the workspace folder, nor within the workspace folder itself.",
                        null)
                    };
            }
            else
            {
                status = new IStatus[] { e.getStatus() };   
            }
            
            PDTPlugin.getDefault().getLog().log(
                new MultiStatus(
                    PDTPlugin.getPluginId(),
                    IStatus.OK,
                    status,
                    "An unexpected exception occurred while creating a link to " +
                    fPath.toString(),
                    e));
            
            // TODO: propagate this exception and/or update client code
            return null; 
        }
	}

    private static void createFolderLink(IPath fPath, IProject prj)
        throws CoreException
    {
        String name = Long.toString(System.currentTimeMillis());
		IFolder link = prj.getFolder(name);

		while (link.exists())
        {
			name = name + "_";
			link = prj.getFolder(name);
		}

        link.createLink(
            fPath.removeLastSegments(1),
			IResource.NONE,
			null);
    }

    private static IProject getPDTLinksProject(IWorkspaceRoot root)
        throws CoreException
    {
        IProject prj = root.getProject("pdt_links");

		if (!prj.exists())
		{
            prj.create(null);
            prj.open(null);
            IProjectDescription description = prj.getDescription();
            String[] natures = new String[1];
            natures[0] = "org.pdt.pikeeditor.pikeinkexternalfilesnature";
            description.setNatureIds(natures);					      
            prj.setDescription(description, null);
		}
		else prj.open(null);

        return prj;
    }    
}
