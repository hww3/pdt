package  org.gotpike.pdt.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.epic.core.model.*;
import org.epic.perleditor.PerlPluginImages;
import org.gotpike.pdt.util.PikePluginImages;

public class PikeOutlineLabelProvider extends LabelProvider
{	
	public Image getImage(Object element)
    {
        if (element instanceof Subroutine)
        {
            Subroutine sub = (Subroutine) element;
            if ("new".equals(sub.getName())) return PikePluginImages.get(PikePluginImages.IMG_ICON_CONSTRUCTOR);
            else return PikePluginImages.get(PikePluginImages.IMG_ICON_SUBROUTINE);
        }
        else if (element instanceof ModuleUse)
        {
            return PikePluginImages.get(PikePluginImages.IMG_ICON_USE);
        }
        else if (element instanceof PikeOutlineContentProvider.PackageElem)
        {
            PikeOutlineContentProvider.PackageElem elem =
                (PikeOutlineContentProvider.PackageElem) element;
            
            if (elem.name.equals(PikeOutlineContentProvider.SUBROUTINES))
                return PikePluginImages.get(PikePluginImages.IMG_ICON_SUBROUTINE_NODE);
            else if (elem.name.equals(PikeOutlineContentProvider.MODULES))
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
