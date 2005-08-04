/*******************************************************************************
* Copyright (c) 2005 Prashant Deva and Gerd Castan
*
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License - v 1.0
* which is available at http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package org.gotpike.pdt.editors;

import java.util.ArrayList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.swt.widgets.Display;

public class PikeReconciliationStrategy implements IReconcilingStrategy,
               IReconcilingStrategyExtension {

       private PikeEditor editor;

       private IDocument fDocument;

       /** holds the calculated positions */
       protected final ArrayList fPositions = new ArrayList();

       /** The offset of the next character to be read */
       protected int fOffset;

       /** The end offset of the range to be scanned */
       protected int fRangeEnd;

       /**
        * @return Returns the editor.
        */
       public PikeEditor getEditor() {
               return editor;
       }

       public void setEditor(PikeEditor editor) {
               this.editor = editor;
       }

       /*
        * (non-Javadoc)
        *
        * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
        */
       public void setDocument(IDocument document) {
               this.fDocument = document;

       }

       /*
        * (non-Javadoc)
        *
        * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion,
        *      org.eclipse.jface.text.IRegion)
        */
       public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
               initialReconcile();
       }

       /*
        * (non-Javadoc)
        *
        * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
        */
       public void reconcile(IRegion partition) {
               initialReconcile();
       }

       /*
        * (non-Javadoc)
        *
        * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#setProgressMonitor(org.eclipse.core.runtime.IProgressMonitor)
        */
       public void setProgressMonitor(IProgressMonitor monitor) {
               // TODO Auto-generated method stub

       }

       /*
        * (non-Javadoc)
        *
        * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#initialReconcile()
        */
       public void initialReconcile() {
               fOffset = 0;
               fRangeEnd = fDocument.getLength();
               calculatePositions();

       }

       /**
        * next character position - used locally and only valid while
        * {@link #calculatePositions()} is in progress.
        */
       protected int cNextPos = 0;

       /** number of newLines found by {@link #classifyTag()} */
       protected int cNewLines = 0;

       protected char cLastNLChar = ' ';

 
       /**
        * uses {@link #fDocument}, {@link #fOffset} and {@link #fRangeEnd} to
        * calculate {@link #fPositions}. About syntax errors: this method is not a
        * validator, it is useful.
        */
       protected void calculatePositions() {
               fPositions.clear();
               cNextPos = fOffset;

               try {
                       recursiveTokens(0);
               } catch (BadLocationException e) {
                       e.printStackTrace();
               }
               // Collections.sort(fPositions, new RangeTokenComparator());

               Display.getDefault().asyncExec(new Runnable() {
                       public void run() {
                               editor.updateFoldingStructure(fPositions);
                       }

               });
       }

       /**
        * emits tokens to {@link #fPositions}.
        *
        * @return number of newLines
        * @throws BadLocationException
        */
       protected int recursiveTokens(int depth) throws BadLocationException 
	   {
               int newLines = 0;
               int inarray = 0;
               int level = 0;
               int hadchars = 0;
               int lastNewLine = 0;
               int startOffset = 0;
               while (cNextPos < fRangeEnd) {
               	PikeToken token = read_token();
               	
               }
	   }
       
       
       PikeToken read_token()
       {
               	while (cNextPos < fRangeEnd) {
                               char ch = fDocument.getChar(cNextPos++);
                               switch (ch) {
                               case '(':
                               			inarray++;
                               			break;
                               case ')':
                               			inarray--;
                               			break;
                               case '{':
                               		if(inarray > 0) continue;
                               		if(level > 0) { 
                                   		level++;
                               			continue;
                               		}
                               		level++;
                          //     		startOffset = cNextPos - 1;
                            		startOffset = lastNewLine;
 
                                      
                                    String tagString = fDocument.get(startOffset, Math.min(cNextPos - startOffset, fRangeEnd - startOffset)); // this is to see where we are in the debugger
                                    
                                       
                                    break;
                               case '}':
                               		if(inarray > 0) continue;
                               		if(level > 0)
                               		{
                                   		level--;
                               		}
                               		level--;
                               		emitPosition(startOffset, cNextPos - startOffset);
                                    break;
                               case ' ':
                               case '\t':
                               		break;
                               case '\r':
                               case '\n':
                               		if(hadchars==0) continue;
                               		hadchars = 0;
                               		lastNewLine = cNextPos -1;
                               		break;
                               default:
                               		hadchars = 1;
                                    break;
                               }
                       }

               }
               return newLines;
       }

       protected void emitPosition(int startOffset, int length) {
               fPositions.add(new Position(startOffset, length));
       }
}

