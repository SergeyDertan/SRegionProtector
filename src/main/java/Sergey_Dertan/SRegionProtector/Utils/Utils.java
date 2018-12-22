package Sergey_Dertan.SRegionProtector.Utils;

import cn.nukkit.utils.Config;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

public abstract class Utils {

    private Utils() {
    }

    public static String serializeStringArray(final String[] data) throws RuntimeException {
        try (final ByteArrayOutputStream boas = new ByteArrayOutputStream(); final ObjectOutputStream oos = new ObjectOutputStream(boas)) {
            oos.writeObject(data);
            return Base64.getEncoder().encodeToString(boas.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String[] deserializeStringArray(final String data) throws RuntimeException {
        try (final ByteArrayInputStream bias = new ByteArrayInputStream(Base64.getDecoder().decode(data)); final ObjectInputStream ois = new ObjectInputStream(bias)) {
            return (String[]) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copyResource(String fileName, String sourceFolder, String targetFolder, Class clazz, boolean fixMissingContents) throws Exception {
        if (sourceFolder.charAt(sourceFolder.length() - 1) != '/') sourceFolder += '/';
        if (targetFolder.charAt(targetFolder.length() - 1) != '/') targetFolder += '/';
        File file = new File(targetFolder + fileName);
        if (!file.exists()) {
            cn.nukkit.utils.Utils.writeFile(file, clazz.getClassLoader().getResourceAsStream(sourceFolder + fileName));
            return;
        }
        if (!fixMissingContents) return;
        Config var3 = new Config(file.getAbsolutePath(), Config.YAML);
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);
        Map<String, Object> var1 = yaml.loadAs(clazz.getClassLoader().getResourceAsStream(sourceFolder + fileName), HashMap.class);
        for (Map.Entry<String, Object> var2 : var1.entrySet()) {
            if (!var3.exists(var2.getKey())) {
                var3.set(var2.getKey(), var2.getValue());
            } else if (var2.getValue() instanceof Map && var3.get(var2.getKey()) instanceof Map) {
                for (Map.Entry<String, Object> var4 : ((Map<String, Object>) var2.getValue()).entrySet()) {
                    if (((Map<String, Object>) var3.get(var2.getKey())).containsKey(var4.getKey())) continue;
                    ((Map<String, Object>) var3.get(var2.getKey())).put(var4.getKey(), var4.getValue());
                }
            }
        }
        var3.save();
    }

    public static void copyResource(String fileName, String sourceFolder, String targetFolder, Class clazz) throws Exception {
        copyResource(fileName, sourceFolder, targetFolder, clazz, true);
    }

    public static boolean resourceExists(String fileName, String folder, Class clazz) {
        if (folder.charAt(folder.length() - 1) != '/') folder += '/';
        return clazz.getClassLoader().getResource(folder + fileName) != null;
    }

    public static List<Cloneable> deepClone(List<Cloneable> arr) {
        List<Cloneable> copy = new ArrayList<>();
        for (Cloneable elem : arr) {
            copy.add(elem.clone());
        }
        return copy;
    }
}