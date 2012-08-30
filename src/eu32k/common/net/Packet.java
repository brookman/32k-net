package eu32k.common.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Packet implements Serializable {
   private static final long serialVersionUID = -8622809686993789558L;

   private int version = NetworkSettings.version;
   private long id = 0;
   private int type = 0;
   private String name = "no name";
   private byte[] payload = new byte[] {};

   private byte[] serializeToArray() throws IOException {
      byte[] output = Serializer.serialize(this);
      if (NetworkSettings.compressData) {
         byte[] temp = new byte[output.length];
         Deflater compresser = new Deflater();
         compresser.setInput(output);
         compresser.finish();
         int compressedDataLength = compresser.deflate(temp);
         output = Arrays.copyOfRange(temp, 0, compressedDataLength);
      }

      byte[] concat = new byte[NetworkSettings.magicBytes.length + output.length];
      System.arraycopy(NetworkSettings.magicBytes, 0, concat, 0, NetworkSettings.magicBytes.length);
      System.arraycopy(output, 0, concat, NetworkSettings.magicBytes.length, output.length);

      return concat;
   }

   private void deserializeFromArray(byte[] data) throws IOException, DataFormatException, ClassNotFoundException {
      if (data.length <= NetworkSettings.magicBytes.length) {
         throw new IOException("Wrong packet.");
      }

      if (!Arrays.equals(Arrays.copyOfRange(data, 0, NetworkSettings.magicBytes.length), NetworkSettings.magicBytes)) {
         throw new IOException("Wrong packet.");
      }

      data = Arrays.copyOfRange(data, NetworkSettings.magicBytes.length, data.length);

      if (NetworkSettings.compressData) {
         Inflater decompresser = new Inflater();
         decompresser.setInput(data, 0, data.length);
         byte[] result = new byte[4096];
         int resultLength = decompresser.inflate(result);
         decompresser.end();
         data = Arrays.copyOfRange(result, 0, resultLength);
      }

      Packet packet = (Packet) Serializer.deserialize(data);

      if (packet.getVersion() != NetworkSettings.version) {
         throw new IOException("Wrong packet.");
      }
      setVersion(packet.getVersion());
      setId(packet.getId());
      setType(packet.getType());
      setName(packet.getName());
      setPayload(packet.getPayload());
   }

   public DatagramPacket toDatagramPacket() throws IOException {
      byte[] data = serializeToArray();
      return new DatagramPacket(data, data.length);
   }

   public void fromDatagramPacket(DatagramPacket datagramPacket) throws IOException, ClassNotFoundException, DataFormatException {
      deserializeFromArray(datagramPacket.getData());
   }

   public long getId() {
      return id;
   }

   public void setId(long id) {
      this.id = id;
   }

   public int getType() {
      return type;
   }

   public void setType(int type) {
      this.type = type;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public byte[] getPayload() {
      return payload;
   }

   public void setPayload(byte[] payload) {
      this.payload = payload;
   }

   public int getVersion() {
      return version;
   }

   public void setVersion(int version) {
      this.version = version;
   }
}
