package Sergey_Dertan.SRegionProtector.Provider;

import Sergey_Dertan.SRegionProtector.Provider.DataObject.Converter;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.FlagListDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.RegionDataObject;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.TextFormat;
import org.datanucleus.enhancement.Detachable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.FLAGS_FOLDER;
import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.REGIONS_FOLDER;
import static Sergey_Dertan.SRegionProtector.Utils.Tags.DATA_TAG;

public final class YAMLDataProvider implements DataProvider {

    public static final String REGION_FILE_NAME = "{@region-name}.yml";
    public static final String FLAG_LIST_FILE_NAME = "{@region-name}.yml";

    public final boolean multithreadedDataLoading;
    public final int threads;

    private final ExecutorService executor;
    private final Logger logger;

    public YAMLDataProvider(Logger logger, boolean multithreadedDataLoading, int threads) {
        this.logger = logger;

        this.multithreadedDataLoading = multithreadedDataLoading;
        if (multithreadedDataLoading) {
            if (threads == -1) threads = Runtime.getRuntime().availableProcessors();
            this.executor = Executors.newFixedThreadPool(threads);
        } else {
            this.executor = null;
        }
        this.threads = threads;
    }

    @Override
    public String getName() {
        return "YAML";
    }

    @Override
    public RegionDataObject loadRegion(String name) {
        return Converter.toRegionDataObject(new Config(REGIONS_FOLDER + REGION_FILE_NAME.replace("{@region-name}", name), Config.YAML).getAll());
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions", "StatementWithEmptyBody"})
    public List<RegionDataObject> loadRegionList() {
        if (this.multithreadedDataLoading) {
            AtomicInteger done = new AtomicInteger();
            List<List<RegionDataObject>> result = new ArrayList<>();
            Utils.sliceArray(new File(REGIONS_FOLDER).listFiles(), this.threads, false).forEach(s -> {
                List<RegionDataObject> res = new ArrayList<>();
                result.add(res);
                this.executor.execute(() -> {
                            s.stream().filter(file -> !file.isDirectory() && file.getName().endsWith(".yml")).forEach(file -> {
                                Object o = new Config(file.getAbsolutePath(), Config.YAML).get("data");
                                if (o != null) res.add(Converter.toRegionDataObject((Map<String, Object>) o));
                            });
                            done.incrementAndGet();
                        }
                );
            });
            while (done.get() < result.size()) ;
            List<RegionDataObject> list = new ArrayList<>();
            result.forEach(list::addAll);
            return list;
        }

        List<RegionDataObject> result = new ArrayList<>();

        Arrays.stream(new File(REGIONS_FOLDER).listFiles()).filter(file -> !file.isDirectory() && file.getName().endsWith(".yml")).forEach(file -> {
            Object o = new Config(file.getAbsolutePath(), Config.YAML).get("data");
            if (o != null) result.add(Converter.toRegionDataObject((Map<String, Object>) o));
        });
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public FlagListDataObject loadFlags(String region) {
        Config file = new Config(FLAGS_FOLDER + FLAG_LIST_FILE_NAME.replace("{@region-name}", region), Config.YAML);
        return Converter.toDataObject((Map<String, Map<String, Object>>) file.get(DATA_TAG));
    }

    @Override
    public void saveFlags(Region region) {
        synchronized (region.lock) {
            Config file = new Config(FLAGS_FOLDER + FLAG_LIST_FILE_NAME.replace("{@region-name}", region.name), Config.YAML);
            file.set(DATA_TAG, region.flagsToMap());
            file.save();
        }
    }

    @Override
    public void saveRegion(Region region) {
        try {
            synchronized (region.lock) {
                Config file = new Config(REGIONS_FOLDER + REGION_FILE_NAME.replace("{@region-name}", region.name), Config.YAML);
                file.set(DATA_TAG, region.toMap());
                file.save();
            }
        } catch (RuntimeException e) {
            this.logger.warning(TextFormat.YELLOW + "Cant save region " + region.name + ": " + e.getMessage()); //TODO message
        }
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void removeRegion(Region region) {
        new File(REGIONS_FOLDER + REGION_FILE_NAME.replace("{@region-name}", region.name)).delete();
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void removeFlags(Region region) {
        new File(FLAGS_FOLDER + REGION_FILE_NAME.replace("{@region-name}", region.name)).delete();
    }

    public void shutdownExecutor() {
        if (this.executor != null) {
            this.executor.shutdown();
            try {
                this.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
            } catch (InterruptedException ignore) {
            }
        }
    }

    @Override
    public Type getType() {
        return Type.YAML;
    }
}
