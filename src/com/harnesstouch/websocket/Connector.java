package com.harnesstouch.websocket;

import com.harnesstouch.websocket.com.harnesstouch.util.Log;
import com.harnesstouch.websocket.message.Message;
import org.jwebsocket.api.WebSocketConnector;
import org.jwebsocket.api.WebSocketPacket;
import org.jwebsocket.config.JWebSocketConfig;
import org.jwebsocket.config.JWebSocketServerConstants;
import org.jwebsocket.factory.JWebSocketFactory;
import org.jwebsocket.instance.JWebSocketInstance;
import org.jwebsocket.kit.WebSocketServerEvent;
import org.jwebsocket.listener.WebSocketServerTokenEvent;
import org.jwebsocket.listener.WebSocketServerTokenListener;
import org.jwebsocket.server.TokenServer;
import org.jwebsocket.token.JSONToken;
import org.jwebsocket.token.MapToken;
import org.jwebsocket.token.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sabari
 */
public class Connector {

   private static Connector instance = new Connector();
   private TokenServer server;
   private ClientListener listener;

   private Connector() {
   }

   public static Connector get() {
      return instance;
   }

   public void start() {
      try {
         JWebSocketConfig.initForConsoleApp(null);
         JWebSocketFactory.start();
         //JWebSocketFactory.start(System.getenv(JWebSocketServerConstants.JWEBSOCKET_HOME) + "conf", System.getenv(JWebSocketServerConstants.JWEBSOCKET_HOME));

         // get the token server
         server = (TokenServer) JWebSocketFactory.getServer("ts0");
         if (server != null) {
            // and add the sample listener to the server's listener chain
            listener = new ClientListener();
            server.addListener(listener);
         }
      } catch (Exception lEx) {
         Log.log(lEx.getClass().getSimpleName() + " on starting jWebsocket server: " + lEx.getMessage());
         lEx.printStackTrace();
      }
   }

   public void waitFor(int timeToLive) {
      if (timeToLive < 0)
         timeToLive = Integer.MAX_VALUE;

      Log.log("Waiting for " + timeToLive + "ms");
      // remain here until shut down request
      long t1 = System.currentTimeMillis();
      while (JWebSocketInstance.getStatus() != JWebSocketInstance.SHUTTING_DOWN && System.currentTimeMillis() - t1 < timeToLive) {
         try {
            Thread.sleep(500);
         } catch (InterruptedException lEx) {
            // no handling required here
         }
      }
   }

   public void listenToDisconnected(ConnectCallBack callBack) {
      listener.listenToDisconnected(callBack);
   }

   public int getNumConnectedClients() {
      return listener.numConnectedClients.intValue();
   }

   public void stop() {
      JWebSocketFactory.stop();
   }

   public void waitForClients(int numClients) throws InterruptedException {
      Log.log("Waiting for " + numClients + " clients to connect...");
      while (listener.numConnectedClients.get() < numClients) {
         Thread.sleep(1000);
      }
   }

   public void broadcast(Message message) {
      MapToken aToken = new MapToken("com.harnesstouch", "message");
      aToken.setString("key1", "value1");
      aToken.setString("key2", "value2");
      aToken.setString("data", message.serialize(1));
      server.broadcastToken(aToken);
   }


   private class ClientListener implements WebSocketServerTokenListener {

      private AtomicInteger numConnectedClients = new AtomicInteger(0);
      private AtomicInteger numProcessedPackets = new AtomicInteger(0);
      private AtomicInteger numProcessedTokens = new AtomicInteger(0);

      private ConnectCallBack callBack;

      private final List<WebSocketConnector> clients = new ArrayList<WebSocketConnector>(5);

      @Override
      public void processOpened(WebSocketServerEvent aEvent) {
         Log.log(aEvent.getConnector().getId() + " connected");
         synchronized (clients) {
            clients.add(aEvent.getConnector());
         }
         numConnectedClients.incrementAndGet();
      }

      @Override
      public void processClosed(WebSocketServerEvent aEvent) {
         Log.log(aEvent.getConnector().getId() + " disconnected");
         numConnectedClients.decrementAndGet();
         if (callBack != null)
            callBack.disconnected(aEvent.getConnector().getNodeId(), numConnectedClients.get());
      }


      @Override
      public void processToken(WebSocketServerTokenEvent aEvent, Token aToken) {
         numProcessedTokens.incrementAndGet();
      }

      @Override
      public void processPacket(WebSocketServerEvent aEvent, WebSocketPacket aPacket) {
         numProcessedPackets.incrementAndGet();
      }

      public void listenToDisconnected(ConnectCallBack callBack) {
         this.callBack = callBack;
      }
   }

   public interface ConnectCallBack {

      void disconnected(String id, int numConnectedClients);
   }

}
