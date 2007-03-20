package  org.gotpike.pdt.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import org.gotpike.pdt.model.ISourceElement;
import org.gotpike.pdt.model.Inherit;
import org.gotpike.pdt.model.Method;
import org.gotpike.pdt.util.PikePluginImages;

public class PikeOutlineLabelProvider extends LabelProvider
{	
	public Image getImage(Object element)
    {
        if (element instanceof Method)
        {
            Method sub = (Method) element;
            if ("new".equals(sub.getName())) return PikePluginImages.get(PikePluginImages.IMG_ICON_CONSTRUCTOR);
            else return PikePluginImages.get(PikePluginImages.IMG_ICON_SUBROUTINE);
        }
        else if (element instanceof Inherit)
        {
            return PikePluginImages.get(PikePluginImages.IMG_ICON_USE);
        }
        else if (element instanceof PikeOutlineContentProvider.ClassElem)
        {
            PikeOutlineContentProvider.ClassElem elem =
                (PikeOutlineContentProvider.ClassElem) element;
            
            if (elem.name.equals(PikeOutlineContentProvider.METHODS))
                return PikePluginImages.get(PikePluginImages.IMG_ICON_SUBROUTINE_NODE);
            else if (elem.name.equals(PikeOutlineContentProvider.INHERITS))
                return PikePluginImages.get(PikePluginImages.IMG_ICON_USE_NODE);
        }
        else if (element instanceof Package)
        {
            return PikePluginImages.get(PikePluginImages.IMG_ICON_PACKAGE_NODE);
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
