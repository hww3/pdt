package  org.gotpike.pdt.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import org.gotpike.pdt.model.Constant;
import org.gotpike.pdt.model.ISourceElement;
import org.gotpike.pdt.model.Inherit;
import org.gotpike.pdt.model.Method;
import org.gotpike.pdt.model.Class;
import org.gotpike.pdt.model.Variable;
import org.gotpike.pdt.util.PikePluginImages;

public class PikeOutlineLabelProvider extends LabelProvider
{	
	public Image getImage(Object element)
    {
        if (element instanceof Method)
        {
          //  Method sub = (Method) element;
          //  if ("create".equals(sub.getName())) return PikePluginImages.get(PikePluginImages.IMG_ICON_CONSTRUCTOR);
            return PikePluginImages.get(PikePluginImages.IMG_ICON_METHOD);
        }
        else if (element instanceof Constant)
        {
            return PikePluginImages.get(PikePluginImages.IMG_ICON_CONSTANT);
        }
        else if (element instanceof Inherit)
        {
            return PikePluginImages.get(PikePluginImages.IMG_ICON_INHERIT);
        }
        else if (element instanceof Variable)
        {
            return PikePluginImages.get(PikePluginImages.IMG_ICON_VARIABLE);
        }
        else if (element instanceof Class)
        {
        	if(((Class) element).getTop())
        		return PikePluginImages.get(PikePluginImages.IMG_ICON_CLASS);
        	else
        		return PikePluginImages.get(PikePluginImages.IMG_ICON_CLASS_INNER);
        }

        return null;
	}

	public String getText(Object element)
    {
		if (element instanceof ISourceElement)
            return ((ISourceElement) element).getName();
        else
            return element.toString();
	}

	public void dispose()
    {	
	}
}
