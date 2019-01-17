package Sergey_Dertan.SRegionProtector.Messenger;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Utils;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.SRegionProtectorLangFolder;
import static Sergey_Dertan.SRegionProtector.Utils.Utils.copyResource;
import static Sergey_Dertan.SRegionProtector.Utils.Utils.resourceExists;

public final class Messenger {

    public static final String DEFAULT_LANGUAGE = "eng";
    private static Messenger instance;
    public final String language;
    private final Object2ObjectMap<String, String> messages;

    @SuppressWarnings("unchecked")
    public Messenger() throws Exception {
        String lang = Server.getInstance().getLanguage().getLang();
        if (!resourceExists(lang + ".yml", "resources/lang", SRegionProtectorMain.class)) lang = DEFAULT_LANGUAGE;
        this.language = lang;
        copyResource(lang + ".yml", "resources/lang", SRegionProtectorLangFolder, SRegionProtectorMain.class);
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);
        this.messages = new Object2ObjectAVLTreeMap<>((Map<String, String>) yaml.loadAs(Utils.readFile(new File(SRegionProtectorLangFolder + lang + ".yml")), HashMap.class));
        instance = this;
    }

    public static Messenger getInstance() {
        return instance;
    }

    public String getLanguage() {
        return this.language;
    }

    public String getMessage(String message, String[] search, String[] replace) {
        String msg = this.messages.getOrDefault(message, message);
        if (search.length == replace.length) {
            for (int i = 0; i < search.length; ++i) {
                String var1 = search[i];
                if (var1.charAt(0) != '{') var1 = '{' + var1;
                if (var1.charAt(var1.length() - 1) != '}') var1 += '}';
                msg = msg.replace(var1, replace[i]);
            }
        }
        return msg;
    }

    public String getMessage(String message, String search, String replace) {
        return this.getMessage(message, new String[]{search}, new String[]{replace});
    }

    public String getMessage(String message) {
        return this.getMessage(message, new String[0], new String[0]);
    }

    public void sendMessage(CommandSender target, String message, String[] search, String[] replace) {
        target.sendMessage(this.getMessage(message, search, replace));
    }

    public void sendMessage(CommandSender target, String message, String search, String replace) {
        this.sendMessage(target, message, new String[]{search}, new String[]{replace});
    }

    public void sendMessage(CommandSender target, String message) {
        this.sendMessage(target, message, new String[0], new String[0]);
    }
}
