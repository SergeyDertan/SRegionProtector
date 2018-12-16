package Sergey_Dertan.SRegionProtector.Region.Chunk;

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
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

import java.util.*;

public final class ChunkManager {

    private Int2ObjectArrayMap<Long2ObjectOpenHashMap<Chunk>> chunks;
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

    /*public long bench() { //TODO remove
        final List<Long> results = new ArrayList<>();
        final int id = Server.getInstance().getLevelByName("world").getId();
        for (int i = 0; i < 1000; i++) {
            long start = System.nanoTime();
            for (int q = 0; q < 100000; q++) {
                getChunk(q, q, id, true,false);
            }
            results.add(System.nanoTime() - start);
        }
        Config q = new Config(Server.getInstance().getDataPath() + "res1.yml", Config.YAML);
        q.set("res", results);
        q.save();
        long w = 0L;
        for (long ww : results) {
            w += ww;
        }
        w = w / results.size();
        return w;
    }*/

    public void init() {
        this.chunks = new Int2ObjectArrayMap<>();
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
            /* TODO if (chunk.getRegions().size() == 0) continue;*/
            chunk.needUpdate = false;
            String level = (String) chunkData.get("level");
            Level lvl = Server.getInstance().getLevelByName(level);
            if (level == null) continue;
            if (!this.chunks.containsKey(lvl.getId())) this.chunks.put(lvl.getId(), new Long2ObjectOpenHashMap<>());
            this.chunks.get(lvl.getId()).put(chunk.getHash(), chunk);
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

    public synchronized void save(boolean auto) { //TODO
        this.removeEmptyChunks();
        int saved = 0;
        int amount = 0;

        for (Int2ObjectArrayMap.Entry<Long2ObjectOpenHashMap<Chunk>> level : this.chunks.int2ObjectEntrySet()) {
            ObjectIterator<Long2ObjectOpenHashMap.Entry<Chunk>> chunks = level.getValue().long2ObjectEntrySet().fastIterator();
            while (chunks.hasNext()) {
                Chunk chunk = chunks.next().getValue();
                ++amount;
                if (!chunk.needUpdate) continue;
                chunk.needUpdate = false;
                this.provider.saveChunk(chunk, Server.getInstance().getLevel(level.getIntKey()).getName());
                ++saved;
            }
        }
        if (auto) {
            this.logger.info(TextFormat.GREEN + this.messenger.getMessage("chunk-manager.chunks-auto-save", "@amount", String.valueOf(saved)));
        } else {
            this.logger.info(TextFormat.GREEN + this.messenger.getMessage("disabling.chunks-saved", "@amount", String.valueOf(amount)));
        }
    }

    public synchronized void save() {
        this.save(false);
    }

    public synchronized void removeEmptyChunks() { //TODO remove empty levels
        int amount = 0;
        for (Int2ObjectArrayMap.Entry<Long2ObjectOpenHashMap<Chunk>> level : this.chunks.int2ObjectEntrySet()) {
            ObjectIterator<Long2ObjectOpenHashMap.Entry<Chunk>> chunks = level.getValue().long2ObjectEntrySet().iterator();
            while (chunks.hasNext()) {
                Chunk chunk = chunks.next().getValue();
                if (chunk.getRegions().size() != 0) continue;
                chunks.remove();
                this.provider.removeChunk(chunk, Server.getInstance().getLevel(level.getIntKey()).getName());
                ++amount;
            }
        }
        this.logger.info(TextFormat.GREEN + this.messenger.getMessage("chunk-manager.empty-chunks-removed", "@amount", String.valueOf(amount)));
    }

    public Set<Chunk> getRegionChunks(Vector3f pos1, Vector3f pos2, int levelId, boolean create) {
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

    public Set<Chunk> getRegionChunks(Vector3f pos1, Vector3f pos2, int levelId) {
        return this.getRegionChunks(pos1, pos2, levelId, false);
    }

    public Region getRegion(Vector3 pos, int levelId) {
        Chunk chunk = this.getChunk(((long) pos.x), ((long) pos.z), levelId, true, false);
        if (chunk == null) return null;
        for (Region region : chunk.getRegions()) {
            if (!region.isVectorInside(pos)) continue;
            return region;
        }
        return null;
    }

    public Chunk getChunk(long x, long z, int levelId, boolean shiftRight, boolean create) { //TODO improve
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

    public Chunk getChunk(long x, long z, int levelId, boolean shiftRight) {
        return this.getChunk(x, z, levelId, shiftRight, true);
    }

    public Chunk getChunk(long x, long z, int levelId) {
        return this.getChunk(x, z, levelId, false, true);
    }
}