package org.gotpike.pdt.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;

public class PikeDocumentProvider extends FileDocumentProvider {

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner =
				new DefaultPartitioner(
					new PikePartitionScanner(),
					new String[] {
						EditorConstants.PIKE_RESERVED,
						EditorConstants.PIKE_AUTODOC,
						EditorConstants.PIKE_COMMENT,
						EditorConstants.PIKE_STRING,
						EditorConstants.PIKE_CHAR,
						EditorConstants.DEFAULT
						});
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}
}