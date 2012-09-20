package com.harnesstouch.websocket.com.harnesstouch.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * @author sabari
 */
public class MyProperties extends Properties {

   public int i(String name, int defaultValue) {
      try {
         return Integer.valueOf(getProperty(name));
      } catch (NumberFormatException nfe) {
         return defaultValue;
      }
   }

   public int i(String name) {
      return i(name, -1);
   }

   public static MyProperties load() {
      File configFile = new File("config.props");
      if (!configFile.exists()) {
         Log.log("Could not find config file " + configFile.getAbsolutePath());
         System.exit(1);
      }
      MyProperties config = new MyProperties();
      try {
         config.load(new FileInputStream(configFile));
      } catch (Exception e) {
         Log.log("Could not load " + configFile.getAbsolutePath());
         Log.logTrace(e);
         System.exit(1);
      }

      return config;
   }
}
