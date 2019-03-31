package Sergey_Dertan.SRegionProtector.Settings;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Provider.ProviderType;
import cn.nukkit.block.Block;
import cn.nukkit.utils.Config;

import java.util.Map;

import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.MAIN_FOLDER;
import static Sergey_Dertan.SRegionProtector.Utils.Utils.copyResource;

public final class Settings {

    public final long selectorSessionLifetime;

    public final int autoSavePeriod;
    public final boolean autoSave;

    public final Block borderBlock;

    public final boolean hideCommands;

    public final MySQLSettings mySQLSettings;
    public final RegionSettings regionSettings;
    public final ProviderType provider;

    public final boolean asyncCommands;
    public final int asyncCommandsThreads;

    public final boolean multithreadedDataLoading;
    public final int dataLoadingThreads;

    public final int lposMaxRadius;

    public final boolean prioritySystem;

    public Settings() throws Exception {
        copyResource("config.yml", "resources/", MAIN_FOLDER, SRegionProtectorMain.class);
        copyResource("mysql.yml", "resources/", MAIN_FOLDER, SRegionProtectorMain.class);
        copyResource("region-settings.yml", "resources/", MAIN_FOLDER, SRegionProtectorMain.class);

        Map<String, Object> config = this.getConfig();

        this.selectorSessionLifetime = ((Number) config.get("session-life-time")).longValue() * 1000L;

        this.autoSavePeriod = ((Number) config.get("auto-save-period")).intValue() * 20;
        this.autoSave = (boolean) config.get("auto-save");

        this.hideCommands = (boolean) config.get("hide-commands");

        this.asyncCommands = (boolean) config.get("async-commands");
        this.asyncCommandsThreads = ((Number) config.getOrDefault("async-commands-threads", -1)).intValue();

        this.multithreadedDataLoading = (boolean) config.get("multithreaded-loading");
        this.dataLoadingThreads = ((Number) config.get("multithreaded-loading-threads")).intValue();

        this.lposMaxRadius = ((Number) config.get("lpos-max-radius")).intValue();

        this.prioritySystem = (boolean) config.get("priority-system");

        String border = (String) config.get("border-block");
        int id;
        int meta;
        if (border.split(":").length == 2) {
            id = Integer.parseInt(border.split(":")[0]);
            meta = Integer.parseInt(border.split(":")[1]);
        } else {
            id = Integer.parseInt(border);
            meta = 0;
        }
        this.borderBlock = Block.get(id, meta);

        this.provider = ProviderType.fromString((String) config.get("provider"));

        this.mySQLSettings = new MySQLSettings(new Config(MAIN_FOLDER + "mysql.yml", Config.YAML).getAll());
        this.regionSettings = new RegionSettings(config, new Config(MAIN_FOLDER + "region-settings.yml", Config.YAML).getAll());
    }

    public Map<String, Object> getConfig() {
        return new Config(MAIN_FOLDER + "config.yml", Config.YAML).getAll();
    }
}
