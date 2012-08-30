package eu32k.common.net;

import java.net.InetAddress;

public class BroadcastAddress {

   private String interfaceName;
   private InetAddress address;
   private boolean enabled = true;

   public BroadcastAddress(String interfaceName, InetAddress address, boolean enabled) {
      this.interfaceName = interfaceName;
      this.address = address;
      this.enabled = enabled;
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

   public boolean isEnabled() {
      return enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

}
