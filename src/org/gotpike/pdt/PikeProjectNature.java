/*
 * Created on Apr 5, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.gotpike.pdt;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;


/**
 * @author luelljoc
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PikeProjectNature implements IProjectNature {

	private IProject project;
	private String PLUGIN_ID = "org.gotpike.pdt";

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#configure()
	 */
	public void configure() throws CoreException {
		// register Pike builder
		//		IProjectDescription description = getProject().getDescription();
		//		ICommand command = description.newCommand();
		//		command.setBuilderName(PLUGIN_ID + ".perlbuilder");
		//		System.out.println("Builder name: " + PLUGIN_ID+ ".pikebuilder");
		String BUILDER_ID = PLUGIN_ID + ".pikebuilder";
		IProjectDescription desc = project.getDescription();
		ICommand[] commands = desc.getBuildSpec();
		boolean found = false;

		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(BUILDER_ID)) {
				found = true;
				break;
			}
		}
		if (!found) {
			// TODO remove debug output
			System.out.println("Builder added: " + PLUGIN_ID+ ".pikebuilder");
			//add builder to project
			ICommand command = desc.newCommand();
			command.setBuilderName(BUILDER_ID);
			ICommand[] newCommands = new ICommand[commands.length + 1];

			// Add it before other builders.
			System.arraycopy(commands, 0, newCommands, 1, commands.length);
			newCommands[0] = command;
			desc.setBuildSpec(newCommands);
			project.setDescription(desc, null);
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#getProject()
	 */
	public IProject getProject() {
		return project;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.resources.IProjectNature#setProject(org.eclipse.core.resources.IProject)
	 */
	public void setProject(IProject fProject) {
		project = fProject;

	}


}
