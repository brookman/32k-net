package eu32k.common.net;

import java.io.IOException;

public class NetTest {

   public static final int CLIENT_TYPE_CONTROLLER = 1;
   public static final int CLIENT_TYPE_ARCHITECT = 2;
   public static final int CLIENT_TYPE_SCREEN = 3;

   public NetTest() {
      // settings
      NetworkSettings.compressData = false;
      NetworkSettings.setMagicWord("net-test-packet|");

      try {
         for (int i = 0; i < 3; i++) {
            NetworkModule net = new NetworkModule("some controller", CLIENT_TYPE_CONTROLLER);
            net.limitBroadcastAdresses("/192\\.168\\.85\\..*");
            net.start();
            new NetTestGui(net);
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static void main(String[] args) {
      new NetTest();
   }

}
