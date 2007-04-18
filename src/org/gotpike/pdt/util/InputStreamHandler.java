package org.gotpike.pdt.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;


/** Handle process output. */

public class InputStreamHandler extends Thread {
    private InputStream stream = null;

    public InputStreamHandler(InputStream stream) {
        super("Process Input Stream Handler");
        setDaemon(true);
        this.stream = new BufferedInputStream(stream);
    }

    /** Override this method to do something meaningful with the output. */
    public void handleBytes(byte[] buf, int count) { }

    // WARNING: closing a process output stream prematurely may result in 
    // the Process object never detecting its termination!
    private void close() {
        try { stream.close(); } 
        catch(IOException io) { io.printStackTrace(); }
    }

    public void run() {
        int BUFSIZE = 256;
        byte[] buf = new byte[BUFSIZE];
        while(true) {
            try {
                int count = stream.read(buf, 0, buf.length);
                if (count == -1) {

                    break;
                }
                else if (count == 0) {
                    try { sleep(100); } 
                    catch (InterruptedException e) { e.printStackTrace(); }
                }
                else if (count > 0) {
                    handleBytes(buf, count);
                }
            }
            catch(IOException io) {
                // we'll get this when the stream closes
                break;
            }
        }
        close();

    }
}

