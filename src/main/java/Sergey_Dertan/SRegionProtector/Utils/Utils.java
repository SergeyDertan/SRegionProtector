package Sergey_Dertan.SRegionProtector.Utils;

import cn.nukkit.utils.Config;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public abstract class Utils {

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

    public static void copyResource(String fileName, String sourceFolder, String targetFolder, Class clazz, boolean fixMissingContents) throws Exception {
        if (sourceFolder.charAt(sourceFolder.length() - 1) != '/') sourceFolder += '/';
        if (targetFolder.charAt(targetFolder.length() - 1) != '/') targetFolder += '/';
        File file = new File(targetFolder + fileName);
        if (!file.exists())
            cn.nukkit.utils.Utils.writeFile(file, clazz.getClassLoader().getResourceAsStream(sourceFolder + fileName));
        if (!fixMissingContents) return;
        Config var3 = new Config(file.getAbsolutePath(), Config.YAML);
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);
        Map<String, Object> var1 = yaml.loadAs(clazz.getClassLoader().getResourceAsStream(sourceFolder + fileName), HashMap.class);
        for (Map.Entry<String, Object> var2 : var1.entrySet()) {
            if (var3.exists(var2.getKey())) continue;
            var3.set(var2.getKey(), var2.getValue());
            var3.save();
        }
    }

    public static void copyResource(String fileName, String sourceFolder, String targetFolder, Class clazz) throws Exception {
        copyResource(fileName, sourceFolder, targetFolder, clazz, true);
    }

    public static boolean resourceExists(String fileName, String folder, Class clazz) {
        if (folder.charAt(folder.length() - 1) != '/') folder += '/';
        return clazz.getClassLoader().getResource(folder + fileName) != null;
    }

/*
    public static <T extends Cloneable> List<T> deepClone(List<T> arr) { //TODO rewrite
        List<T> copy = new ArrayList<>();
        for (T elem : arr) {
            copy.add(elem.clone());
        }
        return copy;
    }

    public static <T extends Cloneable> T[] deepClone(T[] arr) {
        return deepClone(Arrays.asList(arr)).toArray(new Cloneable[]{});
    }*/
}