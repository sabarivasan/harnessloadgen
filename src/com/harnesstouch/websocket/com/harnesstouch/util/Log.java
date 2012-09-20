package com.harnesstouch.websocket.com.harnesstouch.util;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;

/**
 * @author sabari
 */
public class Log {
   private static Writer out = null;

   public static void log(String message) {
      try {
         initStream();
         out.write(DateFormat.getDateTimeInstance().format(new Date()) + ":" + message + "\n");
         out.flush();
      } catch(Exception e) {
         // Ignore
      }
   }

   private static void initStream() {
      try {
         out = new FileWriter("loadGenerator.log", true);
      } catch(Exception e) {
         out = new OutputStreamWriter(System.out);
      }
   }

   public static void logTrace(Exception e) {
      e.printStackTrace(new PrintWriter(out));
   }
}
