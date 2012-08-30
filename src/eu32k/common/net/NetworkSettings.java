package eu32k.common.net;

public class NetworkSettings {
   public static byte[] magicBytes = new byte[] {};
   public static boolean compressData = false;
   public static int version = 0;
   public static int port = 31337;
   public static long announceInterval = 3000;
   public static long listUpdateInterval = 200;

   public static void setMagicWord(String word) {
      magicBytes = word.getBytes();
   }

   static {
      setMagicWord("generic-packet|");
   }
}
