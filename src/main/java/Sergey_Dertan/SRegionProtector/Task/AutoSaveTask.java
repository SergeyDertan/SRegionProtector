package Sergey_Dertan.SRegionProtector.Task;

import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.utils.TextFormat;

public final class AutoSaveTask extends AsyncTask {

    private ChunkManager chunkManager;
    private RegionManager regionManager;
    private PluginLogger logger;
    private Messenger messenger;

    public AutoSaveTask(ChunkManager chunkManager, RegionManager regionManager, PluginLogger logger) {
        this.chunkManager = chunkManager;
        this.regionManager = regionManager;
        this.logger = logger;
        this.messenger = Messenger.getInstance();
    }

    @Override
    public void onRun() {
        this.logger.info(TextFormat.GREEN + this.messenger.getMessage("auto-save-start"));
        this.chunkManager.save(true);
        this.regionManager.save(true);
        this.logger.info(TextFormat.GREEN + this.messenger.getMessage("auto-save-success"));
    }
}
