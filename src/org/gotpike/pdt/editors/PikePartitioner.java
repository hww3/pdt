package org.gotpike.pdt.editors;

import org.eclipse.jface.text.rules.FastPartitioner;

public class PikePartitioner extends FastPartitioner {

	public PikePartitioner()
	{
       super(
			new PikePartitionScanner(),
			new String[] {
				PartitionTypes.RESERVED,
				PartitionTypes.AUTODOC,
				PartitionTypes.COMMENT,
				PartitionTypes.STRING,
				PartitionTypes.DEFAULT
				});
	}
}
