package Sergey_Dertan.SRegionProtector.Provider;

import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Flags.FlagList;
import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.plugin.PluginLogger;

import java.util.List;
import java.util.Map;

public abstract class Provider {

    protected PluginLogger logger;

    public Provider(PluginLogger logger) {
        this.logger = logger;
    }

    public void saveChunkList(Map<String, Map<Long, Chunk>> chunks) {
        chunks.forEach((level, levelChunks) -> levelChunks.values().forEach(chunk -> this.saveChunk(chunk, level)));
    }

    public abstract void saveChunk(Chunk chunk, String level);

    public final void saveRegionList(List<Region> regions) {
        regions.forEach(this::saveRegion);
    }

    public abstract void saveRegion(Region region);

    public abstract String getName();

    public abstract void saveFlags(FlagList flags, String region);

    public abstract List<Map<String, Object>> loadChunkList();

    public abstract Map<String, Map<String, Object>> loadFlags(String region);

    public abstract List<Map<String, Object>> loadRegionList();

    public abstract void removeChunk(Chunk chunk, String level);

    public abstract void removeRegion(Region region);
}