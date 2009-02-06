package org.gotpike.pdt.parser;

public class StringSymbol extends PikeSymbol {
	    
	    public StringSymbol(int type, int line, int column, int offset, String value) {
		// TODO Auto-generated constructor stub
	    	super(type, line, column, -1, -1, offset, value);
	        this.value = value;
	    }

	    public StringSymbol(int type, int line, int column, int offset, Object value) {
			// TODO Auto-generated constructor stub
		    	super(type, line, column, -1, -1, offset, value);
		        this.value = value;
		    }

	    // TODO: we should have a much better approach for generating StringSymbol objects.
	    //  probably ought to be in the lexer.
	    public StringSymbol(PikeSymbol a)
	    {
	    	super(a.sym, a.getLine(), a.left, a.offset-a.getText().length(), a.getText() );
	    }
	    
	    public StringSymbol(PikeSymbol a, PikeSymbol b)
	    {
	    	super(a.sym, a.getLine(), a.left, b.right - a.left, a.getText() + b.getText());
	    }
	    
	    public String toString()
	    {
	        return (String)value;
	    }
	
}
