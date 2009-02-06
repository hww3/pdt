package org.gotpike.pdt.parser;

public class PikeSymbol extends java_cup.runtime.Symbol {
  private int line;
  private int column;
  int offset;
  
  public PikeSymbol(int type, int line, int column, int offset) {
    this(type, line, column, -1, -1, offset, null);
    
  }
  
  // WARNING! This is a terrible kluge to get string concatenation to work.
  // in fact, it probably doesn't work and definitely breaks when the 
  public PikeSymbol(PikeSymbol a, PikeSymbol b)
  {
	  super(a.sym, a.line, (a.getText().length() + b.getText().length()), a.getText() + b.getText());
  }

  public PikeSymbol(int type, int line, int column, int offset, Object value) {
    this(type, line, column, -1, -1, offset, value);
  }

  public PikeSymbol(int type, int line, int column, int left, int right, int offset, Object value) {
    super(type, left, right, value);
    this.line = line;
    this.column = column;
    this.offset = offset;
  }

  public int getLine() {
    return line;
  }

  public int getColumn() {
    return column;
  }
  
  public int getOffset()
  {
  	return offset;
  }
  
  public int getType()
  {
	  return sym;
  }
  
  public int getLength()
  {
	  return left - right;
  }

  public boolean includes(int offset)
  {
      return (this.offset <= offset) && (offset < this.offset + getLength());
  }
  
  public String getText()
  {
	  // TODO: fix this!
	return value.toString();  
  }
  
  public void shift(int offsetDelta, int lineDelta)
  {
      offset += offsetDelta;
      line = (getLine() + lineDelta);
  }
 
  public String toString() {
    return "line "+line+", column "+column;
  }

public void setOffset(int offset) {
	this.offset = offset;	
}
  
  
}


