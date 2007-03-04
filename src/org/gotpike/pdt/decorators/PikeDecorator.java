/*
 * Created on Jan 4, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package org.gotpike.pdt.decorators;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IEditorDescriptor;
import org.gotpike.pdt.Constants;
import org.gotpike.pdt.PDTPlugin;

/**
 * @author luelljoc
 * 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PikeDecorator extends LabelProvider
		implements
			ILightweightLabelDecorator {

	private static final String PIKE_EDITOR_ID = "org.gotpike.pdt.editors.PikeEditor";
	private static final String PIKE_NATURE_ID = "org.gotpike.pdt.pikenature";

	public static int NO_ERROR = -1;

	// Define images
	static final URL BASE_URL = PDTPlugin.getDefault().getBundle().getEntry("/");
	public static final ImageDescriptor ICON_ERROR;
	public static final ImageDescriptor ICON_WARNING;
	static {
		String iconPath = "icons/";
		ICON_ERROR = createImageDescriptor(iconPath + "error_co.gif");
		ICON_WARNING = createImageDescriptor(iconPath + "warning_co.gif");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object,
	 *      org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) {
		IResource resource = getResource(element);
		boolean isPikeProject = false;
		boolean isPikeFolder = false;
		boolean isPikeFile = false;
		
		// Check if resource exists
		if(!resource.exists()) {
			return;
		}

		// Only decorate Perl sources or projects
		IEditorDescriptor defaultEditorDescriptor = PDTPlugin
				.getDefault().getWorkbench().getEditorRegistry()
				.getDefaultEditor(resource.getFullPath().toString());

		try {
			if (resource.getType() == IResource.PROJECT) {
				if (resource.getProject().isAccessible()) {
					if (resource.getProject().hasNature(PIKE_NATURE_ID)) {
						isPikeProject = true;
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		try {
			if (resource.getType() == IResource.FOLDER) {
				if (resource.getProject().isAccessible()) {
					if (resource.getProject().hasNature(PIKE_NATURE_ID)) {
						isPikeFolder = true;
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		if (!isPikeProject && defaultEditorDescriptor != null) {
			if (!defaultEditorDescriptor.getId().equals(PIKE_EDITOR_ID)) {
				return;
			} else {
				isPikeFile = true;
			}
		}

		if (isPikeProject || isPikeFolder) {
			PikeProjectVisitor projectVisitor = new PikeProjectVisitor();
			try {
				//resource.getProject().accept(projectVisitor);
				resource.accept(projectVisitor);
			} catch (CoreException e1) {
				e1.printStackTrace();
			}

			int state = projectVisitor.getState();

			if (state == IMarker.SEVERITY_ERROR) {
				decoration.addOverlay(ICON_ERROR);
			} else if (state == IMarker.SEVERITY_WARNING) {
				decoration.addOverlay(ICON_WARNING);
			}
		} else if (isPikeFile) {
			try {
				if (resource.findMarkers(Constants.PROBLEM_MARKER, true, 1).length > 0) {
					int state = PikeDecorator.getDecoratorMarker(resource);

					if (state == IMarker.SEVERITY_ERROR) {
						decoration.addOverlay(ICON_ERROR);
					} else if (state == IMarker.SEVERITY_WARNING) {
						decoration.addOverlay(ICON_WARNING);
					}
				}
			} catch (CoreException e1) {
				e1.printStackTrace();
			}
		}
	}
	public static PikeDecorator getPikeDecorator() {
		IDecoratorManager decoratorManager = PDTPlugin.getDefault()
				.getWorkbench().getDecoratorManager();

		if (decoratorManager
				.getEnabled("org.gotpike.pdt.decorators.PikeDecorator")) {
			return (PikeDecorator) decoratorManager
					.getBaseLabelProvider("org.gotpike.pdt.decorators.PikeDecorator");
		}
		return null;
	}

	public void fireLabelEvent(final LabelProviderChangedEvent event) {
		// We need to get the thread of execution to fire the label provider
		// changed event , else WSWB complains of thread exception.
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				fireLabelProviderChanged(event);
			}
		});
	}

	private IResource getResource(Object object) {
		if (object instanceof IResource) {
			return (IResource) object;
		}
		if (object instanceof IAdaptable) {
			return (IResource) ((IAdaptable) object)
					.getAdapter(IResource.class);
		}
		return null;
	}

	private static ImageDescriptor createImageDescriptor(String path) {
		try {
			URL url = new URL(BASE_URL, path);

			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
		}

		return ImageDescriptor.getMissingImageDescriptor();
	}

	public static int getDecoratorMarker(IResource resource) {
		int state = NO_ERROR;
		try {
			IMarker[] markers = resource.findMarkers(Constants.PROBLEM_MARKER, true, 1);
			for (int i = 0; i < markers.length; i++) {
				int severity = ((Integer) markers[i]
						.getAttribute(IMarker.SEVERITY)).intValue();
				if (severity == IMarker.SEVERITY_ERROR) {
					state = IMarker.SEVERITY_ERROR;
					break;
				} else if (severity == IMarker.SEVERITY_WARNING) {
					state = IMarker.SEVERITY_WARNING;
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return state;
	}

}

class PikeProjectVisitor implements IResourceVisitor {

	private int state = PikeDecorator.NO_ERROR;
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResourceVisitor#visit(org.eclipse.core.resources.IResource)
	 */
	public boolean visit(IResource resource) throws CoreException {
		int resourceState = PikeDecorator.getDecoratorMarker(resource);

		if (resourceState != PikeDecorator.NO_ERROR) {
			state = resourceState;
		}

		if (state == IMarker.SEVERITY_ERROR) {
			return false;
		} else {
			return true;
		}
	}

	public int getState() {
		return state;
	}

}