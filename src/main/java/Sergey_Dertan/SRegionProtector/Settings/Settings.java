package Sergey_Dertan.SRegionProtector.Settings;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Provider.ProviderType;
import cn.nukkit.block.Block;
import cn.nukkit.utils.Config;

import java.util.Map;

import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.SRegionProtectorMainFolder;
import static Sergey_Dertan.SRegionProtector.Utils.Utils.copyResource;

public final class Settings {

    public final int selectorSessionLifetime;
    public final int autoSavePeriod;
    public final Block borderBlock;

    public final boolean hideCommands;

    public final MySQLSettings mySQLSettings;
    public final RegionSettings regionSettings;
    public final ProviderType provider;

    public final boolean asyncCommands;

    public final boolean multithreadedChunkLoading;
    public final int chunkLoadingThreads;

    public Settings() throws Exception {
        copyResource("config.yml", "resources/", SRegionProtectorMainFolder, SRegionProtectorMain.class);
        copyResource("mysql.yml", "resources/", SRegionProtectorMainFolder, SRegionProtectorMain.class);
        copyResource("region-settings.yml", "resources/", SRegionProtectorMainFolder, SRegionProtectorMain.class);

        this.selectorSessionLifetime = ((Number) this.getConfig().get("session-life-time")).intValue();
        this.autoSavePeriod = ((Number) this.getConfig().get("auto-save-period")).intValue() * 20;

        this.hideCommands = (boolean) this.getConfig().getOrDefault("hide-commands", false);
        this.asyncCommands = (boolean) this.getConfig().getOrDefault("async-commands", false);
        this.multithreadedChunkLoading = (boolean) this.getConfig().getOrDefault("multithreaded-loading", true);
        this.chunkLoadingThreads = ((Number) this.getConfig().getOrDefault("multithreaded-loading-threads", -1)).intValue();
        String border = (String) getConfig().get("border-block");
        int id;
        int meta;
        if (border.split(":").length == 2) {
            id = Integer.valueOf(border.split(":")[0]);
            meta = Integer.valueOf(border.split(":")[1]);
        } else {
            id = Integer.valueOf(border);
            meta = 0;
        }
        this.borderBlock = Block.get(id, meta);

        switch (((String) this.getConfig().get("provider")).toLowerCase()) {
            case "yaml":
            case "yml":
            default:
                this.provider = ProviderType.YAML;
                break;
            case "mysql":
                this.provider = ProviderType.MYSQL;
                break;
            case "sqlite":
            case "sqlite3":
                this.provider = ProviderType.YAML; //TODO change to sqlite
                break;
        }

        this.mySQLSettings = new MySQLSettings(new Config(SRegionProtectorMainFolder + "mysql.yml", Config.YAML).getAll());
        this.regionSettings = new RegionSettings(this.getConfig(), new Config(SRegionProtectorMainFolder + "region-settings.yml", Config.YAML).getAll());
    }

    public Map<String, Object> getConfig() {
        return new Config(SRegionProtectorMainFolder + "config.yml", Config.YAML).getAll();
    }
}