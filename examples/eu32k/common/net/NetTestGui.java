package eu32k.common.net;

import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTextArea;

public class NetTestGui extends JFrame {
   private static final long serialVersionUID = -3239276345916475379L;

   private NetworkModule net;

   public NetTestGui(NetworkModule net) {
      this.net = net;
      updateTitle();
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      final JTextArea area = new JTextArea();
      area.setEditable(false);
      add(area);
      pack();
      setSize(400, 200);
      setLocationRelativeTo(null);
      setVisible(true);

      ThreadUtil.startLoopThread(new Runnable() {
         @Override
         public void run() {
            String newText = "";
            Map<Long, Client> clients = NetTestGui.this.net.getKnownClients();
            synchronized (clients) {
               for (Client client : clients.values()) {
                  newText += client.isActive() ? "" : "! ";
                  newText += client.getPacket().getName() + " ";
                  newText += "(" + client.getPacket().getType() + ") ";
                  newText += client.getPacket().getId() + "\n";
               }
            }
            area.setText(newText);
         }
      }, 100);
   }

   public void updateTitle() {
      setTitle(net.getPacket().getName() + "(" + net.getPacket().getId() + ")");
   }
}
