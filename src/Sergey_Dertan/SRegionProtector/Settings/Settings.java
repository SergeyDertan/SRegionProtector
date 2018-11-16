package Sergey_Dertan.SRegionProtector.Settings;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.SRegionProtectorMainFolder;

public final class Settings {

    public MySQLSettings mySQLSettings;
    public RegionSettings regionSettings;
    public int selectorSessionLifetime;

    public void init(SRegionProtectorMain main) {
        try {
            load("messages.yml", true);
            load("config.yml", true);
            load("region-settings.yml", true);
        } catch (Exception e) {
            main.getLogger().alert(TextFormat.RED + "Cant load resource: " + e.getMessage());
            main.getLogger().alert(TextFormat.RED + "Disabling plugin...");
            main.forceShutdown = true;
            main.getServer().getPluginManager().disablePlugin(main);
            return;
        }
        this.selectorSessionLifetime = (int) this.getConfig().get("session-life-time");
        this.regionSettings = new RegionSettings(this.getConfig(), new Config(SRegionProtectorMainFolder + "region-settings.yml", Config.YAML).getAll());
    }

    private void load(String fileName, boolean checkMissing) throws Exception {
        File file = new File(SRegionProtectorMainFolder + fileName);
        if (!file.exists()) Utils.writeFile(file, SRegionProtectorMain.class.getClassLoader().getResourceAsStream(fileName));
        if (!checkMissing) return;
        Config var3 = new Config(file.getAbsolutePath(), Config.YAML);
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(dumperOptions);
        Map<String, Object> var1 = yaml.loadAs(SRegionProtectorMain.class.getClassLoader().getResourceAsStream(fileName), HashMap.class);
        for (Map.Entry<String, Object> var2 : var1.entrySet()) {
            if (var3.exists(var2.getKey())) continue;
            var3.set(var2.getKey(), var2.getValue());
            var3.save();
        }
    }

    private void load(String fileName) throws Exception {
        this.load(fileName, false);
    }

    public ConfigSection getMessages() {
        return new Config(SRegionProtectorMainFolder + "messages.yml", Config.YAML).getSections();
    }

    public Map<String, Object> getConfig() {
        return new Config(SRegionProtectorMainFolder + "config.yml", Config.YAML).getAll();
    }
}