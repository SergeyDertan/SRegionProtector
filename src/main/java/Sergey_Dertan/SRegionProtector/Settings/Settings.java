package Sergey_Dertan.SRegionProtector.Settings;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.SRegionProtectorLangFolder;
import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.SRegionProtectorMainFolder;

public final class Settings {

    public MySQLSettings mySQLSettings;
    public RegionSettings regionSettings;
    public int selectorSessionLifetime;

    public void init(SRegionProtectorMain main) {
        try {
            copyResource("messages.yml", "resources", SRegionProtectorMainFolder);
            copyResource("config.yml", "resources", SRegionProtectorMainFolder);
            copyResource("region-settings.yml", "resources", SRegionProtectorMainFolder);
        } catch (Exception e) {
            main.getLogger().alert(TextFormat.RED + Messenger.getInstance().getMessage("loading.error.resource", "@err", e.getMessage()));
            main.forceShutdown = true;
            main.getServer().getPluginManager().disablePlugin(main);
            return;
        }
        this.selectorSessionLifetime = (int) this.getConfig().get("session-life-time");
        this.regionSettings = new RegionSettings(this.getConfig(), new Config(SRegionProtectorMainFolder + "region-settings.yml", Config.YAML).getAll());
    }

    public static void copyResource(String fileName, String sourceFolder, String targetFolder, boolean fixMissingContents) throws IOException {
        File file = new File(targetFolder + fileName);
        if (!file.exists()) Utils.writeFile(file, SRegionProtectorMain.class.getClassLoader().getResourceAsStream(sourceFolder + fileName));
        if (!fixMissingContents) return;
        Config var3 = new Config(file.getAbsolutePath(), Config.YAML);
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);
        Map<String, Object> var1 = yaml.loadAs(SRegionProtectorMain.class.getClassLoader().getResourceAsStream(sourceFolder + fileName), HashMap.class);
        for (Map.Entry<String, Object> var2 : var1.entrySet()) {
            if (var3.exists(var2.getKey())) continue;
            var3.set(var2.getKey(), var2.getValue());
            var3.save();
        }
    }

    public static void copyResource(String fileName, String sourceFolder, String targetFolder) throws IOException {
        copyResource(fileName, sourceFolder, targetFolder, true);
    }

    public ConfigSection getMessages() {
        return new Config(SRegionProtectorMainFolder + "messages.yml", Config.YAML).getSections();
    }

    public Map<String, Object> getConfig() {
        return new Config(SRegionProtectorMainFolder + "config.yml", Config.YAML).getAll();
    }
}