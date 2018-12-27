package Sergey_Dertan.SRegionProtector.Region.Chunk;

import Sergey_Dertan.SRegionProtector.Main.SaveType;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Provider.Provider;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.TextFormat;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ChunkManager {

    private Object2ObjectArrayMap<String,Long2ObjectOpenHashMap<Chunk>> chunks;
    private RegionManager regionManager;
    private Provider provider;
    private PluginLogger logger;
    private Messenger messenger;

    public ChunkManager(Provider provider, PluginLogger logger, RegionManager regionManager) {
        this.provider = provider;
        this.logger = logger;
        this.regionManager = regionManager;
        this.messenger = Messenger.getInstance();
    }

    public void init() {
        this.chunks = new Object2ObjectArrayMap<>();
        int chunkAmount = 0;
        for (Map<String, Object> chunkData : this.provider.loadChunkList()) {
            long x = ((Integer) chunkData.get("x")).longValue();
            long z = ((Integer) chunkData.get("z")).longValue();
            String[] regions;
            try {
                regions = Utils.deserializeStringArray((String) chunkData.get("regions"));
            } catch (RuntimeException e) {
                this.logger.alert(TextFormat.RED + this.messenger.getMessage(
                        "loading.error.chunks",
                        new String[]{"@x", "@z", "@level"},
                        new String[]{String.valueOf(x), String.valueOf(z), (String) chunkData.get("level")}));
                continue;
            }
            Chunk chunk = new Chunk(x, z, this.chunkHash(x, z));
            for (String rgName : regions) {
                Region region = this.regionManager.getRegion(rgName);
                if (region == null) continue;
                chunk.addRegion(region);
            }
            if (chunk.getRegions().size() == 0) continue;
            chunk.needUpdate = false;
            String level = (String) chunkData.get("level");
            //Level lvl = Server.getInstance().getLevelByName(level); //TODO
            //if (level == null) continue;
            if (!this.chunks.containsKey(level)) this.chunks.put(level, new Long2ObjectOpenHashMap<>());
            this.chunks.get(level).put(chunk.getHash(), chunk);
            ++chunkAmount;
        }
        this.logger.info(TextFormat.GREEN + this.messenger.getMessage("loading.chunks.success", "@count", String.valueOf(chunkAmount)));
    }

    public long chunkHash(long x, long z) {
        return x << 32 | z & 4294967295L;
    }

    public long chunkHash(int x, int z) {
        return this.chunkHash((long) x, (long) z);
    }

    public synchronized void save(SaveType saveType, String initiator) {
        this.removeEmptyChunks();

        int saved = 0;
        int amount = 0;

        for (Object2ObjectArrayMap.Entry<String,Long2ObjectOpenHashMap<Chunk>> level : this.chunks.object2ObjectEntrySet()) {
            ObjectIterator<Long2ObjectOpenHashMap.Entry<Chunk>> chunks = level.getValue().long2ObjectEntrySet().fastIterator();
            while (chunks.hasNext()) {
                Chunk chunk = chunks.next().getValue();
                synchronized (chunk.lock) {
                    ++amount;
                    if (!chunk.needUpdate) continue;
                    chunk.needUpdate = false;
                    this.provider.saveChunk(chunk, level.getKey());
                    ++saved;
                }
            }
        }
        switch (saveType) {
            case DISABLING:
                this.logger.info(TextFormat.GREEN + this.messenger.getMessage("disabling.chunks-saved", "@amount", String.valueOf(amount)));
                break;
            case MANUAL:
                this.logger.info(TextFormat.GREEN + this.messenger.getMessage("chunk-manager.chunks-manual-save", new String[]{"@amount", "@initiator"}, new String[]{String.valueOf(saved), initiator}));
                break;
            case AUTO:
                this.logger.info(TextFormat.GREEN + this.messenger.getMessage("chunk-manager.chunks-auto-save", "@amount", String.valueOf(saved)));
                break;
        }
    }

    public void save(SaveType saveType) {
        this.save(saveType, null);
    }

    public synchronized void removeEmptyChunks() { //TODO remove empty levels
        int amount = 0;
        for (Object2ObjectArrayMap.Entry<String,Long2ObjectOpenHashMap<Chunk>> level : this.chunks.object2ObjectEntrySet()) {
            ObjectIterator<Long2ObjectOpenHashMap.Entry<Chunk>> chunks = level.getValue().long2ObjectEntrySet().iterator();
            while (chunks.hasNext()) {
                Chunk chunk = chunks.next().getValue();
                if (chunk.getRegions().size() != 0) continue;
                chunks.remove();
                this.provider.removeChunk(chunk, level.getKey());
                ++amount;
            }
        }
        this.logger.info(TextFormat.GREEN + this.messenger.getMessage("chunk-manager.empty-chunks-removed", "@amount", String.valueOf(amount)));
    }

    public Set<Chunk> getRegionChunks(Vector3f pos1, Vector3f pos2, String levelId, boolean create) {
        Set<Chunk> chunks = new HashSet<>();

        long minX = (long) Math.min(pos1.x, pos2.x); //TODO types
        long minZ = (long) Math.min(pos1.z, pos2.z);

        long maxX = (long) Math.max(pos1.x, pos2.x);
        long maxZ = (long) Math.max(pos1.z, pos2.z);

        long x = minX;

        while (x <= maxX) { //TODO
            long z = minZ;
            while (z <= maxZ) {
                Chunk chunk = this.getChunk(x, z, levelId, true, create);
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

    public Set<Chunk> getRegionChunks(Vector3f pos1, Vector3f pos2, String levelId) {
        return this.getRegionChunks(pos1, pos2, levelId, false);
    }

    public Region getRegion(Vector3 pos, String levelId) {
        Chunk chunk = this.getChunk(((long) pos.x), ((long) pos.z), levelId, true, false);
        if (chunk == null) return null;
        for (Region region : chunk.getRegions()) {
            if (!region.isVectorInside(pos)) continue;
            return region;
        }
        return null;
    }

    public Chunk getChunk(long x, long z, String levelId, boolean shiftRight, boolean create) { //TODO improve
        if (shiftRight) {
            x = x >> 4;
            z = z >> 4;
        }
        long hash = this.chunkHash(x, z);
        Long2ObjectOpenHashMap<Chunk> levelChunks = this.chunks.get(levelId);
        if (levelChunks == null && !create) return null;
        levelChunks = this.chunks.computeIfAbsent(levelId, s -> new Long2ObjectOpenHashMap<>());
        Chunk chunk = levelChunks.get(hash);
        if (chunk != null) return chunk;
        if (!create) return null;
        chunk = new Chunk(x, z, hash);
        chunk.needUpdate = true;
        levelChunks.put(hash, chunk);
        return chunk;
    }

    public Chunk getChunk(long x, long z, String levelId, boolean shiftRight) {
        return this.getChunk(x, z, levelId, shiftRight, true);
    }

    public Chunk getChunk(long x, long z, String levelId) {
        return this.getChunk(x, z, levelId, false, true);
    }
}