package com.harnesstouch.websocket.message;

import java.util.Random;

/**
 * @author sabari
 */
public class SimpleRandomMessage implements Message {

   private String id;
   private int length;
   private String serialized = null;

   public SimpleRandomMessage(String id, int length) {
      this.id = id;
      this.length = length;
   }

   @Override
   public String id() {
      return id;
   }

   @Override
   public int length() {
      return length;
   }

   @Override
   public String serialize(int version) {
      if (serialized == null) {
         Random random = new Random();
         StringBuilder sb = new StringBuilder(length);
         if (length > id.length()) {
            sb.append(id);
         }
         while (sb.length() < length) {
            sb.append('a' + random.nextInt('z' - 'a'));
         }
         serialized = sb.toString();
      }

      return serialized;
   }

   @Override
   public String toString() {
      return id + "(l=" + length + ")";
   }

   @Override
   public int hashCode() {
      return id.hashCode() + 31*length;
   }
}
