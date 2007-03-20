package org.gotpike.pdt.parser;

public class CurlySymbol extends PikeSymbol {
	    
	    public CurlySymbol(int type, int line, int column, int offset, int value) {
		// TODO Auto-generated constructor stub
	    	super(type, line, column, -1, -1, offset, value);
	        this.value = new Integer(value);
	    }


		public void setLevel(int level)
	    {
	        this.value = new Integer(level);
	    }
	    
	    public int getLevel()
	    {
	        return ((Integer)value).intValue();
	    }
	    
	    public String toString()
	    {
	        return super.toString() + ",level=" + ((Integer)value).intValue();
	    }
	
}
