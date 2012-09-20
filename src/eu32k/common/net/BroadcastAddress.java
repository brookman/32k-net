package eu32k.common.net;

import java.net.InetAddress;

public class BroadcastAddress {

   private String interfaceName;
   private InetAddress address;

   public BroadcastAddress(String interfaceName, InetAddress address) {
      this.interfaceName = interfaceName;
      this.address = address;
   }

   public String getInterfaceName() {
      return interfaceName;
   }

   public void setInterfaceName(String interfaceName) {
      this.interfaceName = interfaceName;
   }

   public InetAddress getAddress() {
      return address;
   }

   public void setAddress(InetAddress address) {
      this.address = address;
   }

   @Override
   public String toString() {
      return address.getHostAddress();
   }
}
