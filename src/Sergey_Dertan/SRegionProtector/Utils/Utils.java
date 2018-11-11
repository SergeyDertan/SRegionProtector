package Sergey_Dertan.SRegionProtector.Utils;

import java.io.*;
import java.util.Base64;

public final class Utils {

    private Utils() {
    }

    public static String serializeArray(final String[] data) throws IOException {
        try (final ByteArrayOutputStream boas = new ByteArrayOutputStream();
             final ObjectOutputStream oos = new ObjectOutputStream(boas)) {
            oos.writeObject(data);
            return Base64.getEncoder().encodeToString(boas.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] deserializeArray(final String data) throws IOException {
        try (final ByteArrayInputStream bias = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             final ObjectInputStream ois = new ObjectInputStream(bias)) {
            return (String[]) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}