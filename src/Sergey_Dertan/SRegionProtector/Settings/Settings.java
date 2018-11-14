package Sergey_Dertan.SRegionProtector.Settings;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.SRegionProtectorMainFolder;

public final class Settings {

    public MySQLSettings mySQLSettings;
    public RegionSettings regionSettings;

    public void init(SRegionProtectorMain main) {
        try {
            File file;
            file = new File(SRegionProtectorMainFolder + "messages.yml");
            if (!file.exists()) Utils.writeFile(file, main.getResource("messages.yml"));

            file = new File(SRegionProtectorMainFolder + "config.yml");
            if (!file.exists()) Utils.writeFile(file, main.getResource("config.yml"));

            file = new File(SRegionProtectorMainFolder + "region-settings.yml");
            if (!file.exists()) Utils.writeFile(file, main.getResource("region-settings.yml"));
        } catch (IOException e) {
            main.getLogger().alert(TextFormat.RED + "Cant load resource: " + e.getMessage());
            main.getLogger().alert(TextFormat.RED + "Disabling plugin...");
            main.forceShutdown = true;
            main.getServer().getPluginManager().disablePlugin(main);
            return;
        }
        this.regionSettings = new RegionSettings(this.getConfig(), new Config(SRegionProtectorMainFolder + "region-settings.yml", Config.YAML).getAll());
    }

    public ConfigSection getMessages() {
        return new Config(SRegionProtectorMainFolder + "messages.yml", Config.YAML).getSections();
    }

    public Map<String, Object> getConfig() {
        return new Config(SRegionProtectorMainFolder + "config.yml", Config.YAML).getAll();
    }
}