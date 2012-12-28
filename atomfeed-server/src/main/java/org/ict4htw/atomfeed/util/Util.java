package org.ict4htw.atomfeed.util;

import org.postgresql.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Util {

    public static String stringify(Object object) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // TODO: specify proper encoding here
        return Base64.encodeBytes(baos.toByteArray());
//        return new String(baos.toByteArray());
    }
}
