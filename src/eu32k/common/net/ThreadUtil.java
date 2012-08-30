package eu32k.common.net;

public class ThreadUtil {
   public static Thread startLoopThread(final Runnable runnable, final long repeatInterval) {
      Thread thread = new Thread(new Runnable() {
         @Override
         public void run() {
            while (true) {
               runnable.run();
               if (repeatInterval > 0) {
                  try {
                     Thread.sleep(repeatInterval);
                  } catch (InterruptedException e) {
                     // NOP
                  }
               }
            }
         }
      });
      thread.start();
      return thread;
   }
}
