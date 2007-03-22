package org.gotpike.pdt.parser;

class Block
{
  public CurlySymbol start;
  public CurlySymbol end;
  
  Block(CurlySymbol s, CurlySymbol e)
  {
    start = s;
    end = e;
  }
}