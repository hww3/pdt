package org.gotpike.pdt.parser;

public class InheritItem {

	public int modifiers;
	public PikeSymbol ref;
	public PikeSymbol name;
	
	public InheritItem(PikeSymbol ref, PikeSymbol name, int modifiers)
	{
		this.ref = ref;
		this.name = name;
		this.modifiers = modifiers;
	}
}
