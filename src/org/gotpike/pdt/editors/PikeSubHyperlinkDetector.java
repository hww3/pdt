package org.gotpike.pdt.editors;

import org.eclipse.jface.text.*;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;


/**
 * Installed in the PerlEditor to detect subroutine names whose declarations
 * can be navigated to by clicking them.
 * 
 * @see org.epic.perleditor.actions.OpenDeclarationAction
 * @author jploski
 */
public class PikeSubHyperlinkDetector implements IHyperlinkDetector
{
    private final PikeEditor editor;
    
    public PikeSubHyperlinkDetector(PikeEditor editor)
    {
        this.editor = editor;
    }
    
    public IHyperlink[] detectHyperlinks(
        ITextViewer textViewer,
        IRegion region,
        boolean canShowMultipleHyperlinks)
    {
        if (region == null) return null;
        
        IDocument document = textViewer.getDocument();
        if (document == null) return null;
        
        return detectHyperlinks(document, region.getOffset());
    }
    
    private IHyperlink[] detectHyperlinks(IDocument doc, int offset)
    {   
        // Check that the offset is located inside of a partition that can
        // represent a subroutine invocation
        ITypedRegion partition;
        
        try
        {
            partition = (ITypedRegion) doc.getPartition(offset);
            if (!PartitionTypes.DEFAULT.equals(partition.getType())) return null;
        }
        catch (BadLocationException e) { return null; }
        
        // If we get here, our partition type seems ok.
        // Create a PerlSubHyperlink for the partition:
        try
        {
            return new IHyperlink[] {
                new PikeSubHyperlink(
                    editor,
                    doc.get(partition.getOffset(), partition.getLength()),
                    partition)
                };
        }
        catch (BadLocationException e)
        {
            // should never occur
            return null;
        }
    }
    
    private static class PikeSubHyperlink implements IHyperlink
    {
        private final PikeEditor editor;
        private final String subName;
        private final IRegion subNameRegion;
        
        public PikeSubHyperlink(
        		PikeEditor editor, String subName, IRegion subNameRegion)
        {
            this.editor = editor;
            this.subName = subName;
            this.subNameRegion = subNameRegion;
        }
        
        public IRegion getHyperlinkRegion()
        {
            return subNameRegion;
        }

        public String getTypeLabel()
        {
            return null;
        }

        public String getHyperlinkText()
        {
            return null;
        }

        public void open()
        {
            OpenDeclarationAction action =
                (OpenDeclarationAction) editor.getAction(PikeEditorActionIds.OPEN_DECLARATION);
            
            if (action != null) action.run(
                new TextSelection(subNameRegion.getOffset(), subNameRegion.getLength()));
        }
    }
}
