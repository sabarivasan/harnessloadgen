package com.harnesstouch.websocket.message;

/**
 * @author sabari
 */
public interface Message {

   String id();

   int length();

   String serialize(int version);

}
