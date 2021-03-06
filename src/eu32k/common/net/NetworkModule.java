package eu32k.common.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkModule extends Client {

   private MulticastSocket socket;
   private List<BroadcastAddress> broadcastAddresses = new ArrayList<BroadcastAddress>();
   private Map<Long, Client> knownClients = new HashMap<Long, Client>();
   private List<NetworkListener> listeners = new ArrayList<NetworkListener>();

   public NetworkModule(String name, int type, BroadcastAddress broadcastAddress) throws IOException {
      super(name, type);
      broadcastAddresses.add(broadcastAddress);
      socket = new MulticastSocket(NetworkSettings.port);
      socket.setTrafficClass(0x10); // low delay
      socket.setReuseAddress(true);
   }

   public NetworkModule(String name, int type, List<BroadcastAddress> broadcastAddresses) throws IOException {
      super(name, type);
      this.broadcastAddresses = broadcastAddresses;
      socket = new MulticastSocket(NetworkSettings.port);
   }

   public void start() {
      ThreadUtil.startLoopThread(new Runnable() {
         @Override
         public void run() {
            receive();
         }
      }, 0);
      ThreadUtil.startLoopThread(new Runnable() {
         @Override
         public void run() {
            broadcast();
         }
      }, NetworkSettings.announceInterval);
      ThreadUtil.startLoopThread(new Runnable() {
         @Override
         public void run() {
            updateList();
         }
      }, NetworkSettings.listUpdateInterval);
   }

   private void receive() {
      byte[] dataBuffer = new byte[4096];
      DatagramPacket datagramPacket = new DatagramPacket(dataBuffer, dataBuffer.length);
      try {
         socket.receive(datagramPacket);
         Packet packet = new Packet();
         packet.fromDatagramPacket(datagramPacket);

         if (packet.getId() == getPacket().getId()) {
            return; // ignore packets from self
         }

         if (packet.getVersion() != NetworkSettings.version) {
            return; // ignore wrong version
         }

         boolean newClientFound = false;

         synchronized (knownClients) {
            Client otherClient = knownClients.get(packet.getId());
            if (otherClient == null) {
               otherClient = new Client();
               knownClients.put(packet.getId(), otherClient);
               newClientFound = true;
            }
            otherClient.setAddress(datagramPacket.getAddress());
            otherClient.setPacket(packet);
            otherClient.setActive(true);
            otherClient.setLastSeen(System.currentTimeMillis());
         }

         if (newClientFound) {
            broadcast();
         }

         for (NetworkListener listener : listeners) {
            listener.packetReceived(packet);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   private void updateList() {
      synchronized (knownClients) {
         List<Client> clientsToRemove = new ArrayList<Client>();
         for (Client client : knownClients.values()) {
            long timeDifference = System.currentTimeMillis() - client.getLastSeen();
            if (timeDifference > NetworkSettings.announceInterval + 500) {
               client.setActive(false);
               if (timeDifference > NetworkSettings.announceInterval * 3) {
                  clientsToRemove.add(client);
               }
            }
         }
         for (Client client : clientsToRemove) {
            knownClients.remove(client.getPacket().getId());
         }
      }
   }

   public void broadcast() {
      broadcast(null);
   }

   public void broadcast(Serializable object) {
      for (BroadcastAddress address : broadcastAddresses) {
         try {
            send(address.getAddress(), object);
         } catch (IOException e) {
            e.printStackTrace();
         }
      }
   }

   // private void send(int type, Serializable object) throws IOException {
   // send(new int[] { type }, object);
   // }
   //
   // private void send(int[] types, Serializable object) throws IOException {
   // synchronized (knownClients) {
   // for (Client client : knownClients.values()) {
   // for (int type : types) {
   // if (client.getPacket().getType() == type) {
   // send(client.getAddress(), object);
   // }
   // }
   // }
   // }
   // }
   //
   // private void send(String name, Serializable object) throws IOException {
   // synchronized (knownClients) {
   // for (Client client : knownClients.values()) {
   // if (client.getPacket().getName().equals(name)) {
   // send(client.getAddress(), object);
   // }
   // }
   // }
   // }

   private void send(InetAddress address, Serializable object) throws IOException {
      send(address, Serializer.serialize(object));
   }

   private synchronized void send(InetAddress address, byte[] data) {
      try {
         Packet packet = getPacket();
         packet.setPayload(data);
         DatagramPacket datagramPacket = packet.toDatagramPacket();
         datagramPacket.setAddress(address);
         datagramPacket.setPort(NetworkSettings.port);
         socket.send(datagramPacket);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public static List<BroadcastAddress> findBroadcastAddresses() throws SocketException {
      List<BroadcastAddress> adresses = new ArrayList<BroadcastAddress>();
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
         NetworkInterface networkInterface = interfaces.nextElement();
         if (networkInterface.isLoopback()) {
            continue; // Don't want to broadcast to the loopback interface
         }
         for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
            InetAddress broadcast = interfaceAddress.getBroadcast();
            if (broadcast != null) {
               // String name = networkInterface.getDisplayName().toLowerCase();
               // boolean enabled = !name.contains("tap-win32") && !name.contains("vmware") && !name.contains("virtual");
               adresses.add(new BroadcastAddress(networkInterface.getDisplayName(), broadcast));
            }
         }
      }
      return adresses;
   }

   public void setBroadcastAddresses(List<BroadcastAddress> broadcastAddresses) {
      this.broadcastAddresses = broadcastAddresses;
   }

   public List<BroadcastAddress> getBroadcastAddresses() {
      return broadcastAddresses;
   }

   public Map<Long, Client> getKnownClients() {
      return knownClients;
   }

   public void addNetworkListener(NetworkListener listener) {
      listeners.add(listener);
   }

   public void removeNetworkListener(NetworkListener listener) {
      listeners.remove(listener);
   }
}
