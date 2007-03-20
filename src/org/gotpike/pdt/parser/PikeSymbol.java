package org.gotpike.pdt.parser;

public class PikeSymbol extends java_cup.runtime.Symbol {
  private int line;
  private int column;
  int offset;
  
  public PikeSymbol(int type, int line, int column, int offset) {
    this(type, line, column, -1, -1, offset, null);
    
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

  public String getText()
  {
	  // TODO: fix this!
	return value.toString();  
  }
  
  public String toString() {
    return "line "+line+", column "+column;
  }

public void setOffset(int offset) {
	this.offset = offset;	
}
  
  
}


