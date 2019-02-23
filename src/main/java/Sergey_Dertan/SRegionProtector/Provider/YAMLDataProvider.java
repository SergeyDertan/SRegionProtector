package Sergey_Dertan.SRegionProtector.Provider;

import Sergey_Dertan.SRegionProtector.Provider.DataObject.Converter;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.FlagListDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.RegionDataObject;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.TextFormat;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.FLAGS_FOLDER;
import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.REGIONS_FOLDER;
import static Sergey_Dertan.SRegionProtector.Utils.Tags.DATA_TAG;

public final class YAMLDataProvider extends DataProvider { //TODO ??

    public static final String REGION_FILE_NAME = "{@region-name}.yml";
    public static final String FLAG_LIST_FILE_NAME = "{@region-name}.yml";

    public final boolean async;

    private ExecutorService executor;
    private int threads;

    public YAMLDataProvider(Logger logger, boolean async, int threads) {
        super(logger);
        this.async = async;
        if (async) {
            if (threads == -1) threads = Runtime.getRuntime().availableProcessors();
            this.executor = Executors.newFixedThreadPool(threads);
            this.threads = threads;
        }
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
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public List<RegionDataObject> loadRegionList() {
        if (this.async) {
            AtomicInteger done = new AtomicInteger();
            List<List<RegionDataObject>> result = new ObjectArrayList<>();
            Utils.sliceArray(new File(REGIONS_FOLDER).listFiles(), this.threads, false).forEach(s -> {
                List<RegionDataObject> res = new ObjectArrayList<>();
                result.add(res);
                this.executor.execute(() -> {
                            s.forEach(f -> {
                                if (!f.isDirectory() && f.getName().endsWith(".yml")) {
                                    Object o = new Config(f.getAbsolutePath(), Config.YAML).get("data");
                                    if (o != null) res.add(Converter.toRegionDataObject((Map<String, Object>) o));
                                }
                            });
                            done.incrementAndGet();
                        }
                );
            });
            while (done.get() < result.size()) {
            }
            List<RegionDataObject> list = new ObjectArrayList<>();
            result.forEach(list::addAll);
            return list;
        }

        List<RegionDataObject> list = new ObjectArrayList<>();
        for (File file : new File(REGIONS_FOLDER).listFiles()) {
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
        Config file = new Config(FLAGS_FOLDER + FLAG_LIST_FILE_NAME.replace("{@region-name}", region), Config.YAML);
        return Converter.toDataObject((Map<String, Map<String, Object>>) file.get(DATA_TAG));
    }

    @Override
    public synchronized void saveFlags(Region region) {
        synchronized (region.lock) {
            Config file = new Config(FLAGS_FOLDER + FLAG_LIST_FILE_NAME.replace("{@region-name}", region.name), Config.YAML);
            file.set(DATA_TAG, region.flagsToMap());
            file.save();
        }
    }

    @Override
    public synchronized void saveRegion(Region region) {
        try {
            synchronized (region.lock) {
                Config file = new Config(REGIONS_FOLDER + REGION_FILE_NAME.replace("{@region-name}", region.name), Config.YAML);
                file.set(DATA_TAG, region.toMap());
                file.save();
                this.saveFlags(region);
            }
        } catch (RuntimeException e) {
            this.logger.warning(TextFormat.YELLOW + "Cant save region " + region.name + ": " + e.getMessage()); //TODO message
        }
    }

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void removeRegion(String region) {
        new File(REGIONS_FOLDER + REGION_FILE_NAME.replace("{@region-name}", region)).delete();
        new File(FLAGS_FOLDER + REGION_FILE_NAME.replace("{@region-name}", region)).delete();
    }
}
