package eu32k.common.net;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Serializer {

   public static byte[] serialize(Serializable object) throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      ObjectOutputStream out = new ObjectOutputStream(bos);
      out.writeObject(object);
      out.close();
      bos.close();
      return bos.toByteArray();
   }

   public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
      ByteArrayInputStream bis = new ByteArrayInputStream(data);
      ObjectInputStream in = new ObjectInputStream(bis);
      Object object = in.readObject();
      in.close();
      bis.close();
      return object;
   }
}
