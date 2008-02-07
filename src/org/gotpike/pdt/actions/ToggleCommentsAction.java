package org.gotpike.pdt.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.gotpike.pdt.actions.AbstractReplaceAction;
import org.gotpike.pdt.editors.PikeEditor;
import org.gotpike.pdt.editors.PikeEditorActionIds;

public class ToggleCommentsAction extends AbstractReplaceAction {

	 private IEditorPart editorPart;
	 private IViewPart viewPart = null;
	
	/**
	 * Constructor for Action1.
	 */
	public ToggleCommentsAction(PikeEditor e) {
		super(e);
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */

	public void setActiveEditor(IAction action, IEditorPart targetPart) {
		editorPart = targetPart;
	
	}
	
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

	@Override
	protected int getActionKey(String actionID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected String performReplace(String line, HashMap map) {
		Boolean add = null;
		
		add = (Boolean) map.get("adding");
		
		if(add == null)
		{
			if(line.indexOf("//") != 0)
			{
				add = new Boolean(true);
			}
			else add = new Boolean(false);
			
			map.put("adding", add);
		}
		
		if(add.booleanValue())
		{
			return "// " + line;
		}
		else
		{
			if(line.indexOf("// ")== 0)
				return line.substring(3);
			else if(line.indexOf("//") == 0)
				return line.substring(2);
			else return line;
		}
	}

	protected String getPikeEditorActionId() {
		// TODO Auto-generated method stub
		return PikeEditorActionIds.TOGGLE_COMMENTS;
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}	
}