package org.gotpike.pdt.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.gotpike.pdt.PDTPlugin;
import org.gotpike.pdt.model.SourceFile;


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
		String command = PDTPlugin.getDefault().getPreferenceStore().getString(PDTPlugin.PIKE_EXECUTABLE_PREFERENCE);
		try {
			File f = new File(command);
			command = f.getAbsolutePath();
			System.out.println(command);
			
			/*
			 * Bundle bundle = Platform.getBundle(yourPluginId);
   Path path = new Path("icons/sample.gif");
   URL fileURL = FileLocator.find(bundle, path, null);
   InputStream in = fileURL.openStream();
			 */
			
			Random r = new Random();
			validatorPort = validatorPort + r.nextInt(1048);
			
			URL baseURL =FileLocator.find(PDTPlugin.getDefault().getBundle(), new Path("/validator.pike"), null);
			baseURL = FileLocator.toFileURL(baseURL);
			String scriptname = baseURL.getPath();
			System.out.println("VALIDATOR Script IS " + scriptname);
			p = Runtime.getRuntime().exec(command + " " + scriptname + " " + validatorPort);
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
			    public void run() { p.destroy(); }
			});
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        ProcessOutputHandler handler = new ProcessOutputHandler(p) {
	            public void handleOutput(byte[] buf, int count) {
	                output.append(new String(buf, 0, count));
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
			System.out.println("calling validate.");
			result = x.invoke("validate", arglist);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
		
		if(result instanceof XmlRpcArray)
		{
			XmlRpcArray r = (XmlRpcArray)result;
			for(int i = 0; i < r.size(); i++)
			{
				XmlRpcArray e = r.getArray(i);
			//	System.out.println("marking error " + i + " of "  + r.size() + ": "+ e.toString());
				sf.reportError(e.getString(2), e.getString(0), Integer.parseInt(e.getString(1)), 0, 0);
			}
		
		}
	}
}
