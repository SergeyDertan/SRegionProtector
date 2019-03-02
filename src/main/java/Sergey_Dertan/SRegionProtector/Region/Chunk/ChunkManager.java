package Sergey_Dertan.SRegionProtector.Region.Chunk;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.Server;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.TextFormat;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.Iterator;
import java.util.Set;

public final class ChunkManager {

    private final Object lock = new Object();
    private final Object2ObjectArrayMap<String, Long2ObjectOpenHashMap<Chunk>> chunks;
    private final Logger logger;
    private final Messenger messenger;

    public ChunkManager(Logger logger) {
        this.logger = logger;
        this.messenger = Messenger.getInstance();

        this.chunks = new Object2ObjectArrayMap<>();
    }

    public static long chunkHash(long x, long z) {
        return x << 32 | z & 4294967295L;
    }

    public void init() {
        Server.getInstance().getScheduler().scheduleDelayedRepeatingTask(SRegionProtectorMain.getInstance(), this::removeEmptyChunks, 360 * 20, 360 * 20, true);
    }

    public int getChunkAmount() {
        int amount = 0;
        for (Long2ObjectMap<Chunk> chunks : this.chunks.values()) amount += chunks.size();
        return amount;
    }

    public void removeEmptyChunks() {
        synchronized (this.lock) {
            int amount = 0;
            Iterator<Object2ObjectArrayMap.Entry<String, Long2ObjectOpenHashMap<Chunk>>> it = this.chunks.object2ObjectEntrySet().fastIterator();
            while (it.hasNext()) {
                Object2ObjectArrayMap.Entry<String, Long2ObjectOpenHashMap<Chunk>> level = it.next();
                if (level.getValue().size() == 0) {
                    it.remove();
                    continue;
                }
                Iterator<Long2ObjectOpenHashMap.Entry<Chunk>> chunks = level.getValue().long2ObjectEntrySet().fastIterator();
                while (chunks.hasNext()) {
                    Chunk chunk = chunks.next().getValue();
                    if (chunk.getRegions().size() != 0) continue;
                    chunks.remove();
                    ++amount;
                }
            }
            this.logger.info(TextFormat.GREEN + this.messenger.getMessage("chunk-manager.empty-chunks-removed", "@amount", Integer.toString(amount)));
        }
    }

    public Set<Chunk> getRegionChunks(Vector3 pos1, Vector3 pos2, String levelId, boolean create) {
        Set<Chunk> chunks = new ObjectArraySet<>();

        long minX = (long) Math.min(pos1.x, pos2.x);
        long minZ = (long) Math.min(pos1.z, pos2.z);

        long maxX = (long) Math.max(pos1.x, pos2.x);
        long maxZ = (long) Math.max(pos1.z, pos2.z);

        long x = minX;

        while (x <= maxX) { //TODO rework?
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

    public Set<Chunk> getRegionChunks(Vector3 pos1, Vector3 pos2, String levelId) {
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

    public Chunk getChunk(long x, long z, String levelId, boolean shiftRight, boolean create) {
        Long2ObjectOpenHashMap<Chunk> levelChunks = this.chunks.get(levelId);
        if (levelChunks == null && !create) return null;

        if (shiftRight) {
            x = x >> 4;
            z = z >> 4;
        }
        long hash = chunkHash(x, z);

        synchronized (this.lock) {
            levelChunks = this.chunks.computeIfAbsent(levelId, s -> new Long2ObjectOpenHashMap<>());
            Chunk chunk = levelChunks.get(hash);
            if (chunk != null || !create) return chunk;
            chunk = new Chunk(x, z);
            levelChunks.put(hash, chunk);
            return chunk;
        }
    }

    public Chunk getChunk(long x, long z, String levelId, boolean shiftRight) {
        return this.getChunk(x, z, levelId, shiftRight, true);
    }

    public Chunk getChunk(long x, long z, String levelId) {
        return this.getChunk(x, z, levelId, false, true);
    }
}
