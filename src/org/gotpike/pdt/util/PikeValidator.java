package org.gotpike.pdt.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.gotpike.pdt.PDTPlugin;
import org.gotpike.pdt.model.SourceFile;
import org.osgi.framework.Bundle;


import redstone.xmlrpc.XmlRpcArray;
import redstone.xmlrpc.XmlRpcClient;
import redstone.xmlrpc.XmlRpcException;
import redstone.xmlrpc.XmlRpcFault;

public class PikeValidator implements Runnable {

	XmlRpcClient x;
	Process p;
	StringBuffer output = new StringBuffer();
	private String code;
	private SourceFile sf;
	private String fn;
	int validatorPort = 9080;
	
	// TODO: we need to get a better way of picking a port. Right now, we just pick a random
	// port from a starting point and run with it.
	
	public PikeValidator()
	{
		Bundle b = Platform.getBundle(PDTPlugin.getPluginId());
		URL u = null;
		
		try {
			u = FileLocator.toFileURL(b.getEntry("/validator.pike"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String command = PDTPlugin.getDefault().getPreferenceStore().getString(PDTPlugin.PIKE_EXECUTABLE_PREFERENCE);
		String script;
		try {
			File f = new File(command);
			command = f.getAbsolutePath();

			System.out.println(command);

			try {
				  f = new File(u.toURI());
				} catch(URISyntaxException e) {
				  f = new File(u.getPath());
				}
			script = f.getAbsolutePath();
				
			System.out.println("running " + command + " " + script);
			p = Runtime.getRuntime().exec(command + " " + script + " " + validatorPort);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() { p.destroy(); }
		});

	        ProcessOutputHandler handler = new ProcessOutputHandler(p) {
	            public void handleOutput(byte[] buf, int count) {
	                System.out.println(new String(buf, 0, count));
	            }
	        };
	}
	
	public synchronized void validate(String code, SourceFile sf, String fn) throws MalformedURLException, XmlRpcException, XmlRpcFault
	{
		this.code = code;
		this.sf = sf;
		this.fn = fn;
		
  		  Thread t = new Thread(this);
		  t.start();
	}
	
	public synchronized void run()
	{
		if(x == null)
			try {
				System.out.println("http://localhost:" + validatorPort);
				x = new XmlRpcClient("http://localhost:" + validatorPort , true);
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		ArrayList<String> arglist = new ArrayList<String>();
		
		arglist.add(code);
		arglist.add(fn);
		
		Object result = null;
		try {
			result = x.invoke("validate", arglist);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			System.out.println(e1.getMessage());
		} 
		
		if(result instanceof XmlRpcArray)
		{
			XmlRpcArray r = (XmlRpcArray)result;
			for(int i = 0; i < r.size(); i++)
			{
				XmlRpcArray e = r.getArray(i);
				System.out.println("marking error " + i + " of "  + r.size() + ": "+ e.toString());
				sf.reportError(e.getString(2), e.getString(0), Integer.parseInt(e.getString(1)), 0, 0);
			}
		
		}
	}
}
