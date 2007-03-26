package org.gotpike.pdt.parser;

import java.util.ArrayList;

public class ConstantList
{
  public ArrayList list; 

  public ConstantList(PikeSymbol s)
  {
	list  = new ArrayList();
	list.add(s);
  }
  
  public void add(PikeSymbol s)
  {
	list.add(s);
  }
}