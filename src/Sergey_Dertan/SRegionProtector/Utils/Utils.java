package Sergey_Dertan.SRegionProtector.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public final class Utils {

    private Utils() {
    }

    public static String serializeStringArray(final String[] data) throws RuntimeException {
        try (final ByteArrayOutputStream boas = new ByteArrayOutputStream();
             final ObjectOutputStream oos = new ObjectOutputStream(boas)) {
            oos.writeObject(data);
            return Base64.getEncoder().encodeToString(boas.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] deserializeStringArray(final String data) throws RuntimeException {
        try (final ByteArrayInputStream bias = new ByteArrayInputStream(Base64.getDecoder().decode(data));
             final ObjectInputStream ois = new ObjectInputStream(bias)) {
            return (String[]) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Cloneable> deepClone(List<Cloneable> arr) { //TODO rewrite
        List<Cloneable> copy = new ArrayList<>();
        for (Cloneable elem : arr) {
            copy.add(elem.clone());
        }
        return copy;
    }

    public static Cloneable[] deepClone(Cloneable[] arr) {
        return deepClone(Arrays.asList(arr)).toArray(new Cloneable[arr.length]);
    }
}