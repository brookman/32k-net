package eu32k.common.net;

import java.net.InetAddress;
import java.util.Random;

public class Client {

   private InetAddress address;
   private Packet packet;
   private long lastSeen;
   private boolean active = true;

   public Client() {

   }

   public Client(String name, int type) {
      packet = new Packet();
      packet.setId(new Random().nextLong());
      packet.setName(name);
      packet.setType(type);
   }

   public InetAddress getAddress() {
      return address;
   }

   public void setAddress(InetAddress address) {
      this.address = address;
   }

   public Packet getPacket() {
      return packet;
   }

   public void setPacket(Packet packet) {
      this.packet = packet;
   }

   public long getLastSeen() {
      return lastSeen;
   }

   public void setLastSeen(long lastSeen) {
      this.lastSeen = lastSeen;
   }

   public boolean isActive() {
      return active;
   }

   public void setActive(boolean active) {
      this.active = active;
   }
}
