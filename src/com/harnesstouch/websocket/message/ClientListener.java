package com.harnesstouch.websocket.message;

import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.jwebsocket.listener.WebSocketServerTokenListener;
import org.jwebsocket.token.Token;

/**
 * @author sabari
 */
public class ClientListener implements WebSocketServerTokenListener {
   @Override
   public void processToken(WebSocketServerTokenEvent aEvent, Token aToken) {

   }

   @Override
   public void processOpened(WebSocketServerEvent aEvent) {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public void processPacket(WebSocketServerEvent aEvent, WebSocketPacket aPacket) {
      //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public void processClosed(WebSocketServerEvent aEvent) {
      //To change body of implemented methods use File | Settings | File Templates.
   }
}
