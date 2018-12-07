package Sergey_Dertan.SRegionProtector.Messenger;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Settings.Settings;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.Utils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.SRegionProtectorLangFolder;

public final class Messenger {

    private final Map<String, String> messages;
    public static final String DEFAULT_LANGUAGE = "eng";
    public final String language;

    private static Messenger instance;

    public Messenger() throws IOException {
        String lang = Server.getInstance().getLanguage().getName();
        if (!(new File(SRegionProtectorMain.SRegionProtectorLangFolder + lang + ".yml")).exists()) lang = DEFAULT_LANGUAGE;
        this.language = lang;
        Settings.copyResource("eng.yml", "resources/lang", SRegionProtectorLangFolder);
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);
        this.messages = (Map<String, String>) yaml.loadAs(Utils.readFile(new File(SRegionProtectorMain.SRegionProtectorLangFolder + lang + ".yml")), HashMap.class);
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
                msg = msg.replace("{" + search[i] + "}", replace[i]);
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
