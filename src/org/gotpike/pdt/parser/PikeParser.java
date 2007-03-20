package org.gotpike.pdt.parser;

import java.io.*;


/**
 * Simple test driver for the java parser. Just runs it on some
 * input files, gives no useful output.
 */
public class PikeParser {

  public static void main(String argv[]) {

    for (int i = 0; i < argv.length; i++) {
      try {
        System.out.println("Parsing ["+argv[i]+"]");
        FileReader file = new FileReader(argv[i]);
        PikeScanner s = new PikeScanner(new FileReader(argv[i]), argv[i]);
        parser p = new parser(s);
        p.parse();
        
        System.out.println("No errors.");
      }
      catch (Exception e) {
        e.printStackTrace(System.out);
        System.exit(1);
      }
    }
  }

}