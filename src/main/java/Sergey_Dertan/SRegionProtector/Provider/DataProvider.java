package Sergey_Dertan.SRegionProtector.Provider;

import Sergey_Dertan.SRegionProtector.Provider.DataObject.ChunkDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.FlagListDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.RegionDataObject;
import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.utils.Logger;

import java.util.Map;
import java.util.Set;

public abstract class DataProvider {

    protected Logger logger;

    public DataProvider(Logger logger) {
        this.logger = logger;
    }

    public final synchronized void saveChunkList(Map<String, Map<Long, Chunk>> chunks) {
        chunks.forEach((level, levelChunks) -> levelChunks.values().forEach(chunk -> this.saveChunk(chunk, level)));
    }

    public abstract void saveChunk(Chunk chunk, String level);

    public final synchronized void saveRegionList(Set<Region> regions) {
        regions.forEach(this::saveRegion);
    }

    public abstract void saveRegion(Region region);

    public abstract String getName();

    public abstract void saveFlags(Region region);

    public abstract Set<ChunkDataObject> loadChunkList();

    public abstract FlagListDataObject loadFlags(String region);

    public abstract Set<RegionDataObject> loadRegionList();

    public abstract RegionDataObject loadRegion(String name);

    public abstract ChunkDataObject loadChunk(long x, long z, String level);

    public abstract void removeChunk(Chunk chunk, String level);

    public abstract void removeRegion(String region);

    public final void removeRegion(Region region) {
        this.removeRegion(region.getName());
    }
}