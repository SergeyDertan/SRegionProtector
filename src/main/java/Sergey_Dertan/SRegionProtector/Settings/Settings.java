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

        Map<String, Object> config = this.getConfig();

        this.selectorSessionLifetime = ((Number) config.get("session-life-time")).intValue();
        this.autoSavePeriod = ((Number) config.get("auto-save-period")).intValue() * 20;

        this.hideCommands = (boolean) config.getOrDefault("hide-commands", false);
        this.asyncCommands = (boolean) config.getOrDefault("async-commands", false);
        this.multithreadedChunkLoading = (boolean) config.getOrDefault("multithreaded-loading", true);
        this.chunkLoadingThreads = ((Number) config.getOrDefault("multithreaded-loading-threads", -1)).intValue();
        String border = (String) config.get("border-block");
        int id;
        int meta;
        if (border.split(":").length == 2) {
            id = Integer.parseInt(border.split(":")[0]);
            meta = Integer.parseInt(border.split(":")[1]);
        } else {
            id = Integer.valueOf(border);
            meta = 0;
        }
        this.borderBlock = Block.get(id, meta);

        this.provider = ProviderType.fromString((String) this.getConfig().get("provider"));

        this.mySQLSettings = new MySQLSettings(new Config(SRegionProtectorMainFolder + "mysql.yml", Config.YAML).getAll());
        this.regionSettings = new RegionSettings(config, new Config(SRegionProtectorMainFolder + "region-settings.yml", Config.YAML).getAll());
    }

    public Map<String, Object> getConfig() {
        return new Config(SRegionProtectorMainFolder + "config.yml", Config.YAML).getAll();
    }
}