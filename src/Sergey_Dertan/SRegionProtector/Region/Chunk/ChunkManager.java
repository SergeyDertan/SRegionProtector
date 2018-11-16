package Sergey_Dertan.SRegionProtector.Region.Chunk;

import Sergey_Dertan.SRegionProtector.Provider.Provider;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.math.Vector3f;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ChunkManager {

    private Map<String, Map<Long, Chunk>> chunks;
    private RegionManager regionManager;
    private Provider provider;
    private PluginLogger logger;

    public ChunkManager(Provider provider, PluginLogger logger, RegionManager regionManager) {
        this.provider = provider;
        this.logger = logger;
        this.regionManager = regionManager;
    }

    public void init() {
        this.chunks = new HashMap<>();
        int chunkAmount = 0;
        for (Map<String, Object> chunkData : this.provider.loadChunkList()) {
            long x = new Integer((int) chunkData.get("x")).longValue();
            long z = new Integer((int) chunkData.get("z")).longValue();
            String[] regions;
            try {
                regions = Utils.deserializeStringArray((String) chunkData.get("regions"));
            } catch (RuntimeException e) {
                this.logger.alert(TextFormat.RED + "Cant load chunk regions: " + e.getMessage());
                this.logger.alert(TextFormat.RED + "Check chunk: x: " + x + ", z: " + z + ", level: " + chunkData.get("level"));
                continue;
            }
            Chunk chunk = new Chunk(x, z, this.chunkHash(x, z));
            for (String rgName : regions) {
                Region region = this.regionManager.getRegion(rgName);
                if (region == null) continue;
                chunk.addRegion(region);
            }
            String level = (String) chunkData.get("level");
            if (!this.chunks.containsKey(level)) this.chunks.put(level, new HashMap<>());
            this.chunks.get(level).put(chunk.getHash(), chunk);
            ++chunkAmount;
        }
        this.logger.info(TextFormat.GREEN + "Loaded " + chunkAmount + " chunks.");
    }

    public long chunkHash(long x, long z) {
        return x << 32 | z & 4294967295L;
    }

    public long chunkHash(int x, int z) {
        return this.chunkHash((long) x, (long) z);
    }

    public void save() {
        for (Map.Entry<String, Map<Long, Chunk>> level : this.chunks.entrySet()) {
            for (Map.Entry<Long, Chunk> chunk : level.getValue().entrySet()) {
                if (chunk.getValue().getRegions().size() == 0)
                    this.provider.removeChunk(chunk.getValue(), level.getKey());
            }
        }
        this.provider.saveChunkList(this.chunks);
    }

    public Set<Chunk> getRegionChunks(Vector3f pos1, Vector3f pos2, String level, boolean create) {
        Set<Chunk> chunks = new HashSet<>();

        long minX = (long) Math.min(pos1.x, pos2.x);
        long minZ = (long) Math.min(pos1.z, pos2.z);

        long maxX = (long) Math.max(pos1.x, pos2.x);
        long maxZ = (long) Math.max(pos1.z, pos2.z);

        long x = minX;

        while (x <= maxX) {
            long z = minZ;
            while (z <= maxZ) {
                Chunk chunk = this.getChunk(x, z, level, true, create);
                if (chunk != null) chunks.add(chunk);
                if (z == maxZ) break;
                z += 16L;
                if (z > maxZ) z = maxZ;
            }
            if (x == maxX) break;
            x += 16L;
            if (x > maxX) x = maxX;
        }
        return chunks;
    }

    public Set<Chunk> getRegionChunks(Vector3f pos1, Vector3f pos2, String level) {
        return this.getRegionChunks(pos1, pos2, level, false);
    }

    public Chunk getChunk(long x, long z, String level, boolean shiftRight, boolean create) {
        if (shiftRight) {
            x = x >> 4;
            z = z >> 4;
        }
        long hash = this.chunkHash(x, z);
        Map<Long, Chunk> levelChunks = this.chunks.get(level);
        if (levelChunks == null && !create) return null;
        if (levelChunks == null) {
            levelChunks = new HashMap<>();
            this.chunks.put(level, levelChunks);
        }
        Chunk chunk = levelChunks.get(hash);
        if (chunk != null) return chunk;
        if (!create) return null;
        chunk = new Chunk(x, z, hash);
        levelChunks.put(hash, chunk);
        return chunk;
    }

    public Chunk getChunk(long x, long z, String level, boolean shiftRight) {
        return this.getChunk(x, z, level, shiftRight, true);
    }

    public Chunk getChunk(long x, long z, String level) {
        return this.getChunk(x, z, level, false, true);
    }
}