/*
 * @author skeet
 * Created At: 8/28/22, 9:29 AM
 * Project: Copper
 */

package com.skitbet.botapi.utils;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;

public class SerializerUtils {

    public static String hashToBase64(HashMap hashMap) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutputStream data = new ObjectOutputStream(outputStream);

            data.writeObject(hashMap);

            data.close();

            return Base64.getEncoder().encodeToString(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static HashMap hashFrom64(String data) {
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(Base64.getDecoder().decode(data));
            ObjectInputStream dataInput = new ObjectInputStream(input);

            HashMap toReturn = (HashMap) dataInput.readObject();
            dataInput.close();
            return toReturn;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
