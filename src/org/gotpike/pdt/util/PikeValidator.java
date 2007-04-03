package org.gotpike.pdt.util;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.gotpike.pdt.model.SourceFile;

import redstone.xmlrpc.XmlRpcArray;
import redstone.xmlrpc.XmlRpcClient;
import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcFault;

public class PikeValidator {

	SourceFile sf;
	String fn;
	XmlRpcClient x;
	
	public PikeValidator(SourceFile sf, String fn)
	{
		this.sf = sf;
		this.fn = fn;
	}
	
	public void validate(String code) throws MalformedURLException, XmlRpcException, XmlRpcFault
	{
		if(x == null)
  		  x = new XmlRpcClient("http://localhost:8908", true);
		
		ArrayList<String> arglist = new ArrayList<String>();
		
		arglist.add(code);
		arglist.add(fn);
		
		Object result = x.invoke("validate", arglist);
		//System.out.println("result: " + result.toString());
		
		if(result instanceof XmlRpcArray)
		{
			XmlRpcArray r = (XmlRpcArray)result;
			for(int i = 0; i < r.size(); i++)
			{
				XmlRpcArray e = r.getArray(i);
			//	System.out.println("marking error " + i + " of "  + r.size() + ": "+ e.toString());
				sf.reportError(e.getString(2), e.getString(0), Integer.parseInt(e.getString(1)), 0, 1);
			}
		
		}
	}
}
