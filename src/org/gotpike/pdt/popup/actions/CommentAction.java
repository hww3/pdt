package org.gotpike.pdt.popup.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.gotpike.pdt.actions.AbstractReplaceAction;
import org.gotpike.pdt.editors.PikeEditor;

public class CommentAction extends AbstractReplaceAction {

	 private IEditorPart editorPart;
	 private IViewPart viewPart = null;
	
	/**
	 * Constructor for Action1.
	 */
	public CommentAction() {
		super();
		System.out.println("CommentAction()");

	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */

	public void setActiveEditor(IAction action, IEditorPart targetPart) {
		editorPart = targetPart;
	
	}
	/**
	 * @see IActionDelegate#run(IAction)
	 */
	/*
	public void run(IAction action) {
		PikeEditor editor = (PikeEditor)editorPart;
		String text = null;
		ITextSelection ts;
		
		ISelection selection = editor.getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection) {
			ts  = (ITextSelection) selection;
			text = ts.getText();
		//	System.out.println(text);
		}

		if(text != null)
		{
			boolean adding = true;
			StringBuffer buf = new StringBuffer();
			String[] x = text.split("\n");
			for(int i = 0; i < x.length; i++)
			{
				if(i == 0)
				{
					if(x[i].indexOf("//")==0)
						adding = false;
				}
				if(adding)
  				  buf.append("// " + x[i]);
			}
		


		}
		
		Shell shell = new Shell();
		MessageDialog.openInformation(
			shell,
			"Pike Development Tools",
			"New Action was executed.");
	}
*/
	
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

}
