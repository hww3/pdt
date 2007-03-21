package org.gotpike.pdt.editors;

import java.io.IOException;
import java.util.*;


import org.eclipse.core.runtime.*;
import org.eclipse.jface.text.*;

import org.gotpike.pdt.PDTPlugin;
import org.gotpike.pdt.parser.CurlySymbol;
import org.gotpike.pdt.parser.PikeScanner;
import org.gotpike.pdt.parser.PikeSymbol;

import org.gotpike.pdt.parser.sym;

/**
 * 
 * @author jploski
 */
public class PikePartitioner implements
    IDocumentPartitioner,
    IDocumentPartitionerExtension,
    IDocumentPartitionerExtension2,
    IDocumentPartitionerExtension3
{
    private final Object TOKENS_LOCK = new Object();
    private final PikeScanner scanner = new PikeScanner();
    private final ILog log;
    
    private IDocument doc;
    private DocumentRewriteSession activeRewriteSession;
    private boolean initialized;
    private SymbolsList tokens;
    private int lastUnaffectedTokenI = -1;
    private int syncTokenI = -1;
    
    public PikePartitioner(ILog log)
    {
        this.log = log;
        tokens = new SymbolsList();
    }    
    
    public ITypedRegion[] computePartitioning(int offset, int length)
    {
        return computePartitioning(offset, length, false);
    }
    
    public ITypedRegion[] computePartitioning(
        int offset, int length, boolean includeZeroLengthPartitions)
    {
        // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=49264
        // for explanation of includeZeroLengthPartitions
        
        List typedRegions = new ArrayList();
        if (!initialized) initialize();
        
        if (tokens.isEmpty())
        {
            return new ITypedRegion[] { new TypedRegion(
                0, doc.getLength(), PartitionTypes.DEFAULT) };
        }
                       
        int tokenCount = tokens.size();
        int prevRegionEnd = -1;
        int rOffset, rLength;
        
        for (int i = Math.max(tokens.getTokenIndexPreceding(offset), 0);
             i < tokenCount;
             i++)
        {
        	PikeSymbol t = (PikeSymbol) tokens.get(i);
            if (t.getOffset() >= offset + length) break;
            
            if (prevRegionEnd == -1)
            {
                if (t.includes(offset))
                {
                    // add the right piece of the leftmost token
                    rOffset = offset;
                    rLength = t.getOffset() + t.getLength() - rOffset;
                    typedRegions.add(new TypedRegion(
                        rOffset, rLength, getTokenContentType(t, i)));
                    if (includeZeroLengthPartitions) typedRegions.add(new TypedRegion(
                        rOffset + rLength, 0, PartitionTypes.DEFAULT));
                }
                else
                {
                    rOffset = offset;
                    rLength = 0;
                }
            }
            else
            {
                if (t.getOffset() - prevRegionEnd > 0)
                {
                    // add gap before the current position
                    rOffset = prevRegionEnd;
                    rLength = t.getOffset() - rOffset;
                    typedRegions.add(new TypedRegion(
                        rOffset, rLength, PartitionTypes.DEFAULT));
                }
                rOffset = t.getOffset();
                rLength = Math.min(t.getLength(), offset+length-rOffset);
                typedRegions.add(new TypedRegion(
                    rOffset, rLength, getTokenContentType(t, i)));
                if (includeZeroLengthPartitions) typedRegions.add(new TypedRegion(
                    rOffset + rLength, 0, PartitionTypes.DEFAULT));
            }
            prevRegionEnd = rOffset + rLength;
        }
        
        if (prevRegionEnd < offset + length)
        {
            // add gap after the rightmost position
            typedRegions.add(new TypedRegion(
                prevRegionEnd,
                offset + length - prevRegionEnd,
                PartitionTypes.DEFAULT));
        }
        
        ITypedRegion[] ret = (ITypedRegion[]) typedRegions.toArray(
            new ITypedRegion[typedRegions.size()]);
        
        //dumpPartitioning(offset, length, includeZeroLengthPartitions, ret);        
        return ret;
    }

    public void connect(IDocument document)
    {
        connect(document, false);
    }
    
    public void connect(IDocument document, boolean delayInitialization)
    {
        doc = document;
        initialized = false;
        
        if (!delayInitialization) initialize();
    }

    public void disconnect()
    {
        doc = null;
    }

    public void documentAboutToBeChanged(DocumentEvent event)
    {
        // note: this is not ALWAYS called before documentChanged.. just sometimes
        computeLastUnaffectedTokenI(event);
        computeSyncTokenI(event);
    }

    public boolean documentChanged(DocumentEvent event)
    {
        return documentChanged2(event) != null;
    }
    
    public IRegion documentChanged2(DocumentEvent event)
    {
        try
        {
            synchronized (TOKENS_LOCK)
            {
                IRegion ret = documentChanged2Impl(event);
                //tokens.dump();
                return ret;
            }
        }
        finally
        {
            syncTokenI = lastUnaffectedTokenI = -1;
        }
    }
    
    public DocumentRewriteSession getActiveRewriteSession()
    {
        return activeRewriteSession;
    }
    
    public String getContentType(int offset)
    {
        return getContentType(offset, false);
    }
    
    public String getContentType(int offset, boolean preferOpenPartitions)
    {
        return getPartition(offset, preferOpenPartitions).getType();
    }
    
    public String[] getLegalContentTypes()
    {
        return PartitionTypes.getTypes();
    }
    
    public String[] getManagingPositionCategories()
    {
        return null;
    }

    public ITypedRegion getPartition(int offset)
    {
        return getPartition(offset, false);
    }

    public ITypedRegion getPartition(int offset, boolean preferOpenPartitions)
    {
        ITypedRegion ret = getPartitionImpl(offset, preferOpenPartitions);
        assert ret.getOffset() + ret.getLength() <= doc.getLength() :
            "assertion failed: getPartition returned invalid result for offset " +
            offset + ": " + ret.getOffset() + ":" + ret.getLength() + ":" + ret.getType();
        return ret;
    }
    
    public List getTokens()
    {
        return Collections.unmodifiableList(tokens);
    }
    
    public Object getTokensLock()
    {
        return TOKENS_LOCK;
    }

    public void startRewriteSession(DocumentRewriteSession session)
        throws IllegalStateException
    {
        if (activeRewriteSession != null) throw new IllegalStateException(
            "another rewrite session is already active");
        else
            activeRewriteSession = session;
    }

    public void stopRewriteSession(DocumentRewriteSession session)
    {
        activeRewriteSession = null;
        initialize(); // reset state, e.g. after a series of find-replace operations
    }
    
    private void computeLastUnaffectedTokenI(DocumentEvent event)
    {
        try
        {
            lastUnaffectedTokenI =
                tokens.getTokenIndexPreceding(event.getOffset());
            
            if (lastUnaffectedTokenI >= 0)
            {
            	PikeSymbol t = (PikeSymbol) tokens.get(lastUnaffectedTokenI);
                if (t.includes(event.getOffset())) lastUnaffectedTokenI--;                
            }
            else lastUnaffectedTokenI = -1;
            
            // scan back for a curly brace
            while (lastUnaffectedTokenI >= 0)
            {
            	PikeSymbol t = (PikeSymbol) tokens.get(lastUnaffectedTokenI);
            	PikeSymbol prevT = lastUnaffectedTokenI > 0
                    ? (PikeSymbol) tokens.get(lastUnaffectedTokenI-1)
                    : null;
    
                if (t.getType() == sym.LBRACE &&
                    (prevT == null || prevT.getType() != sym.TOK_ARROW))
                {
                    break;
                }
                else lastUnaffectedTokenI--;
            }
        }
        finally
        {
            assert lastUnaffectedTokenI == -1 ||
                   tokens.get(lastUnaffectedTokenI) instanceof CurlySymbol;
        }
    }
    
    private void computeSyncTokenI(DocumentEvent event)
    {
        int offset = event.getOffset() + event.getLength();
        syncTokenI = tokens.getTokenIndexPreceding(offset);
        
        if (syncTokenI < 0) syncTokenI = -1;
        else
        {
            syncTokenI++;
            if (syncTokenI >= tokens.size()) syncTokenI = -1;
        }
    }
    
    private IRegion documentChanged2Impl(DocumentEvent event)
    {
        assert event.getDocument() == doc;

        String repl = event.getText();
        if (repl == null) repl = "";
        int shiftDelta = repl.length() - event.getLength();

        PikeSymbol sync;
        if (syncTokenI >= 0 && syncTokenI < tokens.size())
        {          
            sync = (PikeSymbol) tokens.get(syncTokenI);
            sync.shift(shiftDelta, 0);
            tokens.markSync(syncTokenI+1);
        }
        else
        {
            sync = null;
        }

        CurlySymbol parseStartCurly;
        int parseStartOffset;
        if (lastUnaffectedTokenI >= 0)
        {
            parseStartCurly = (CurlySymbol) tokens.get(lastUnaffectedTokenI);
            parseStartOffset = parseStartCurly.getOffset();
            tokens.truncate(lastUnaffectedTokenI);
        }
        else
        {
            parseStartOffset = 0;
            parseStartCurly = null;
            tokens.truncate(0);
        }
        
        scanner.reset(null, doc, parseStartCurly);
        try
        {
        	PikeSymbol t;
            while ((t = nextToken(scanner)).getType() != sym.EOF)
            {
                if (t.equals(sync))
                {
                    int lineShiftDelta = t.getLine() - sync.getLine();
                    tokens.add(t);

                    // add remaining tokens unchanged, except for shifted
                    // token offsets and possibly adjusted bracket
                    // nesting levels
                    
                    int start = tokens.size();
                    tokens.addSync();
                    int pc = scanner.getCurlyLevel();
                    int tokenCount = tokens.size();

                    for (int i = start; i < tokenCount; i++)
                    {
                    	PikeSymbol pt = (PikeSymbol) tokens.get(i);
                        pt.shift(shiftDelta, lineShiftDelta);
                        if (pt instanceof CurlySymbol)
                        {
                            if (pt.getType() == sym.LBRACE)
                            {
                                ((CurlySymbol) pt).setLevel(pc);
                                pc++;
                            }
                            else
                            {
                                pc--;
                                ((CurlySymbol) pt).setLevel(pc);
                            }
                        }
                    }
                    assert tokens.noOverlaps();
                    return new Region(parseStartOffset, sync.getOffset() - parseStartOffset);
                }
                else tokens.add(t);
            }
        }
        catch(Exception e)
        {
/*            if (e.getMessage().indexOf("unrecognized character at document offset") != -1)
            {
                log.log(new Status(
                    IStatus.ERROR,
                    PDTPlugin.getPluginId(),
                    IStatus.OK,
                    "Could not parse source file due to an unrecognized character. " +
                    "Check if the text file encoding is set correctly in Preferences/Editors.",
                    e
                    ));
            }*/
            //else
            {
                log.log(new Status(
                    IStatus.ERROR,
                    PDTPlugin.getPluginId(),
                    IStatus.OK,
                    "Could not parse source file. Report this exception as " +
                    "a bug, including the text fragment which triggers it, " +
                    "if possible.",
                    e
                    ));
            }
        }
        
        initialized = true;
        return new Region(parseStartOffset, doc.getLength() - parseStartOffset);    
    }
    
    private ITypedRegion getPartitionImpl(int offset, boolean preferOpenPartitions)
    {
        if (!initialized) initialize();
        if (offset == doc.getLength()) return new TypedRegion(offset, 0, PartitionTypes.DEFAULT);

        int i = tokens.getTokenIndexPreceding(offset);
        
        if (i == -1) // offset lies before the first token
        {
            if (!tokens.isEmpty())
                return new TypedRegion(
                    0,
                    ((PikeSymbol) tokens.get(0)).getOffset(),
                    PartitionTypes.DEFAULT);
            else
                return new TypedRegion(0, 0, PartitionTypes.DEFAULT);
        }
        else
        {
        	PikeSymbol t = (PikeSymbol) tokens.get(i);
            if (t.includes(offset))
            {
                if (preferOpenPartitions && t.getOffset() == offset)
                {
                    // imaginary zero-length open partition before each token
                    return new TypedRegion(offset, 0, PartitionTypes.DEFAULT);
                }   
                else return token2Region(t, i);
                
            }

            if (i < tokens.size() - 1)
            {
                // offset lies in a gap between tokens
            	PikeSymbol t2 = (PikeSymbol) tokens.get(i+1);
                return new TypedRegion(
                    t.getOffset() + t.getLength(),
                    t2.getOffset() - (t.getOffset() + t.getLength()),
                    PartitionTypes.DEFAULT);
            }
            else                
            {
                // offset lies after the last token
                return new TypedRegion(
                    t.getOffset() + t.getLength(),
                    doc.getLength() - (t.getOffset() + t.getLength()),
                    PartitionTypes.DEFAULT);
            }
        }
    }
    
    private String getTokenContentType(PikeSymbol t, int i)
    {   
        switch (t.sym)
        {
        case sym.TOK_COMMENT:
            return PartitionTypes.COMMENT;

        default:
            return PartitionTypes.DEFAULT;
        }
    }
    
    private void initialize()
    {
        documentChanged2(new DocumentEvent(doc, 0, doc.getLength(), doc.get()));
    }
    
    private void dumpPartitioning(
        int offset,
        int length,
        boolean includeZeroLengthPartitions,
        ITypedRegion[] p)
    {
        System.err.println(
            "computePartitioning " + includeZeroLengthPartitions +
            " " + offset + ":" + length + " start...");

        for (int i = 0; i < p.length; i++)
        {
            System.err.println(
                i + ": " + p[i].getOffset() + ":" +
                p[i].getLength() + ":" + p[i].getType());
        }
        System.err.println("---- end of computePartitioning");

    }
    
    private PikeSymbol nextToken(PikeScanner scanner)
    {
            try {
				return (PikeSymbol)(scanner.next_token());
			} catch (IOException e) {
				// TODO Auto-generated catch block
			     log.log(new Status(
		                    IStatus.ERROR,
		                    PDTPlugin.getPluginId(),
		                    IStatus.OK,
		                    "Could not parse source file. Report this exception as " +
		                    "a bug, including the text fragment which triggers it, " +
		                    "if possible.",
		                    e
		                    ));
			     return null;
			}

    }
    
    private TypedRegion token2Region(PikeSymbol t, int i)
    {
        return new TypedRegion(
            t.getOffset(),
            t.getLength(),
            getTokenContentType(t, i));
    }
}