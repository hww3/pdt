package org.gotpike.pdt.editors;

/**
 * Types of partitions that can appear in a Perl document,
 * also referred to as ITypedRegions and "content types".
 * 
 * @author jploski
 */
public class PartitionTypes
{
    public final static String DEFAULT = "__dftl_partition_content_type";
    public final static String COMMENT = "COMMENT";
    public final static String AUTODOC = "AUTODOC";
    public final static String STRING = "STRING";
    public static final String RESERVED = "RESERVED";
	public static final String CHAR = "CHAR";
	
    private final static String[] contentTypes = new String[] {
        DEFAULT,
        AUTODOC,
        COMMENT,
        STRING,
        RESERVED
        };

	
    
    public static String[] getTypes()
    {
        String[] tmp = new String[contentTypes.length];
        System.arraycopy(contentTypes, 0, tmp, 0, tmp.length);
        return tmp;
    }
    
    private PartitionTypes() { }
}
