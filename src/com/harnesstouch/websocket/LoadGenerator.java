package com.harnesstouch.websocket;

import com.harnesstouch.websocket.com.harnesstouch.util.Log;
import com.harnesstouch.websocket.com.harnesstouch.util.MyProperties;
import com.harnesstouch.websocket.message.Message;
import com.harnesstouch.websocket.message.SimpleRandomMessage;

import java.util.Random;

/**
 * @author sabari
 */
public class LoadGenerator implements Runnable {
   private int minNumConnectedClients;
   private int numMessages;
   private int lowLength;
   private int hiLength;
   private int thinkTime;
   private Connector connector;

   public LoadGenerator(int numClients, int numMessages, int lowLength, int hiLength, int thinkTime, Connector connector) {
      this.minNumConnectedClients = numClients;
      this.numMessages = numMessages;
      this.lowLength = lowLength;
      this.hiLength = hiLength;
      this.thinkTime = thinkTime;
      this.connector = connector;
   }

   public static void main(String[] args) throws InterruptedException {
      Connector connector = null;
      try {
         MyProperties cfg = MyProperties.load();
         connector = Connector.get();
         connector.start();

         LoadGenerator generator = new LoadGenerator(cfg.i("minNumConnectedClients"), cfg.i("numMessages"), cfg.i("lowLength"), cfg.i("hiLength"), cfg.i("thinkTime"), connector);
         generator.run();

         connector.waitFor(cfg.i("totalTestTime"));
      } finally {
         if (connector != null)
            connector.stop();
      }
   }

   @Override
   public void run() {
      try {
         int ctr = 0;
         String namePrefix = Thread.currentThread().getName() + "_";
         Random random = new Random();

         connector.waitForClients(minNumConnectedClients);
         boolean sufficientClientsConnected = true;
         do  {
            String id = namePrefix + (++ctr);
            Message message = new SimpleRandomMessage(id, lowLength + random.nextInt(hiLength - lowLength));
            Log.log("Broadcasting token " + message);
            connector.broadcast(message);

            //Thread.sleep(random.nextInt(thinkTime));
            Thread.sleep(thinkTime);
            sufficientClientsConnected = connector.getNumConnectedClients() >= minNumConnectedClients;
         } while(sufficientClientsConnected && ctr < numMessages);

         if (!sufficientClientsConnected) {
            Log.log("TEST FAILURE BECAUSE OF INSUFFICIENT NUMBER OF CLIENTS!!!!");
         }
      } catch (InterruptedException ie) {
         Log.log("Thread " + Thread.currentThread().getName() + " interrupted.");
      }
   }

}
