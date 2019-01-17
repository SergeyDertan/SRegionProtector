package Sergey_Dertan.SRegionProtector.Provider;

import Sergey_Dertan.SRegionProtector.Provider.DataObject.ChunkDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.Converter;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.FlagListDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.RegionDataObject;
import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.*;
import static Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager.chunkHash;
import static Sergey_Dertan.SRegionProtector.Utils.Tags.DATA_TAG;
import static Sergey_Dertan.SRegionProtector.Utils.Tags.LEVEL_TAG;

public final class YAMLDataProvider extends DataProvider { //TODO ??

    public static final String CHUNK_FILE_NAME = "{@level}.{@hash}.yml";
    public static final String REGION_FILE_NAME = "{@region-name}.yml";
    public static final String FLAG_LIST_FILE_NAME = "{@region-name}.yml";

    public YAMLDataProvider(Logger logger) {
        super(logger);
    }

    @Override
    public String getName() {
        return "YAML";
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public Set<ChunkDataObject> loadChunkList() {
        Set<ChunkDataObject> list = new HashSet<>();
        for (File file : (new File(SRegionProtectorChunksFolder).listFiles())) {
            if (file.isDirectory()) continue;
            list.add(Converter.toChunkDataObject((Map<String, Object>) new Config(file.getAbsolutePath(), Config.YAML).get(DATA_TAG)));
        }
        return list;
    }

    @Override
    public ChunkDataObject loadChunk(long x, long z, String level) {
        return Converter.toChunkDataObject(new Config(SRegionProtectorChunksFolder + CHUNK_FILE_NAME.replace("{@level}", level).replace("{@hash}", String.valueOf(chunkHash(x, z))), Config.YAML).getAll());
    }

    @Override
    public RegionDataObject loadRegion(String name) {
        return Converter.toRegionDataObject(new Config(SRegionProtectorRegionsFolder + REGION_FILE_NAME.replace("{@region-name}", name), Config.YAML).getAll());
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public Set<RegionDataObject> loadRegionList() {
        Set<RegionDataObject> list = new HashSet<>();
        for (File file : (new File(SRegionProtectorRegionsFolder).listFiles())) {
            if (file.isDirectory() || !file.getName().endsWith(".yml")) continue;
            Object o = new Config(file.getAbsolutePath(), Config.YAML).get("data");
            if (o == null) {
                this.logger.alert(TextFormat.RED + "Error while loading region from file " + file.getName()); //TODO message
                continue;
            }
            list.add(Converter.toRegionDataObject((Map<String, Object>) o));
        }
        return list;
    }

    @Override
    @SuppressWarnings("unchecked")
    public FlagListDataObject loadFlags(String region) {
        Config file = new Config(SRegionProtectorFlagsFolder + FLAG_LIST_FILE_NAME.replace("{@region-name}", region), Config.YAML);
        return Converter.toDataObject((Map<String, Map<String, Object>>) file.get(DATA_TAG));
    }

    @Override
    public synchronized void saveChunk(Chunk chunk, String level) {
        try {
            synchronized (chunk.lock) {
                Config file = new Config(SRegionProtectorChunksFolder + CHUNK_FILE_NAME.replace("{@level}", level).replace("{@hash}", String.valueOf(chunkHash(chunk.getX(), chunk.getZ()))), Config.YAML);
                Map<String, Object> data = chunk.toMap();
                data.put(LEVEL_TAG, level);
                file.set(DATA_TAG, data);
                file.save();
            }
        } catch (RuntimeException e) {
            this.logger.warning(TextFormat.YELLOW + "Cant save chunk(x: " + chunk.getX() + ", z: " + chunk.getZ() + ", level: " + level + ": " + e.getMessage()); //TODO message
        }
    }

    @Override
    public synchronized void saveFlags(Region region) {
        synchronized (region.lock) {
            Config file = new Config(SRegionProtectorFlagsFolder + FLAG_LIST_FILE_NAME.replace("{@region-name}", region.name), Config.YAML);
            file.set(DATA_TAG, region.flagsToMap());
            file.save();
        }
    }

    @Override
    public synchronized void saveRegion(Region region) {
        try {
            synchronized (region.lock) {
                Config file = new Config(SRegionProtectorRegionsFolder + REGION_FILE_NAME.replace("{@region-name}", region.name), Config.YAML);
                file.set(DATA_TAG, region.toMap());
                file.save();
                this.saveFlags(region);
            }
        } catch (RuntimeException e) {
            this.logger.warning(TextFormat.YELLOW + "Cant save region " + region.getName() + ": " + e.getMessage()); //TODO message
        }
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void removeChunk(Chunk chunk, String level) {
        new File(SRegionProtectorChunksFolder + level + "." + chunkHash(chunk.getX(), chunk.getZ()) + ".yml").delete();
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void removeRegion(String region) {
        new File(SRegionProtectorRegionsFolder + REGION_FILE_NAME.replace("{@region-name}", region)).delete();
        new File(SRegionProtectorFlagsFolder + REGION_FILE_NAME.replace("{@region-name}", region)).delete();
    }
}