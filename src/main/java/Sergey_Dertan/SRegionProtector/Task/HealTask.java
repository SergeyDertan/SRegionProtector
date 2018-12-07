package Sergey_Dertan.SRegionProtector.Task;

import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.AsyncTask;

public final class HealTask extends AsyncTask { //TODO rewrite

    private ChunkManager chunkManager;

    public HealTask(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }

    @Override
    public void onRun() {
        for (Player player : Server.getInstance().getOnlinePlayers().values()) {
            if (!player.isOnline() || !player.isSurvival()) continue;
            Region region = this.chunkManager.getRegion(player, player.level.getName());
            if (region == null) continue;
            if (!region.getFlagList().getFlagState(RegionFlags.FLAG_HEAL)) continue;
            player.heal(3F);
        }
    }
}
