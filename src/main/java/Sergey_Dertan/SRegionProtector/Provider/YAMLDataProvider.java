package Sergey_Dertan.SRegionProtector.Provider;

import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.Converter;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.FlagListDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.RegionDataObject;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.FLAGS_FOLDER;
import static Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain.REGIONS_FOLDER;
import static Sergey_Dertan.SRegionProtector.Utils.Tags.DATA_TAG;

@SuppressWarnings("WeakerAccess")
public final class YAMLDataProvider implements DataProvider {

    public static final String REGION_FILE_NAME = "{@region-name}.yml";
    public static final String FLAG_LIST_FILE_NAME = "{@region-name}.yml";

    public final boolean multithreadedDataLoading;
    public final int threads;

    private final Logger logger;
    private final Messenger messenger;

    public YAMLDataProvider(Logger logger, boolean multithreadedDataLoading, int threads) {
        this.logger = logger;
        this.messenger = Messenger.getInstance();

        this.multithreadedDataLoading = multithreadedDataLoading;
        this.threads = threads == -1 ? Runtime.getRuntime().availableProcessors() : threads;
    }

    @Override
    public RegionDataObject loadRegion(String name) {
        return Converter.toRegionDataObject(new Config(REGIONS_FOLDER + REGION_FILE_NAME.replace("{@region-name}", name), Config.YAML).getAll());
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public List<RegionDataObject> loadRegionList() {
        if (this.multithreadedDataLoading) {
            ExecutorService executor = Executors.newFixedThreadPool(this.threads);
            List<List<RegionDataObject>> result = new ArrayList<>();
            Utils.sliceArray(new File(REGIONS_FOLDER).listFiles((dir, name) -> name.endsWith(".yml")), this.threads, false).forEach(s -> {
                List<RegionDataObject> res = new ArrayList<>();
                result.add(res);
                executor.execute(() ->
                        s.stream().filter(File::isFile).forEach(file -> {
                            Object o = new Config(file.getAbsolutePath(), Config.YAML).get("data");
                            try {
                                if (o != null) res.add(Converter.toRegionDataObject((Map<String, Object>) o));
                            } catch (Exception e) {
                                this.logger.warning("Error loading region from file " + file.getName(), e); //TODO message
                            }
                        })
                );
            });
            executor.shutdown();
            try {
                executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            List<RegionDataObject> list = new ArrayList<>();
            result.forEach(list::addAll);
            return list;
        }

        List<RegionDataObject> result = new ArrayList<>();

        Arrays.stream(new File(REGIONS_FOLDER).listFiles((dir, name) -> name.endsWith(".yml"))).filter(File::isFile).forEach(file -> {
            Object o = new Config(file.getAbsolutePath(), Config.YAML).get("data");
            try {
                if (o != null) result.add(Converter.toRegionDataObject((Map<String, Object>) o));
            } catch (Exception e) {
                this.logger.warning("Error loading region from file " + file.getName(), e); //TODO message
            }
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
            this.logger.warning(TextFormat.RED + this.messenger.getMessage("provider.save-error-region", "@region", region.name));
            this.logger.warning(cn.nukkit.utils.Utils.getExceptionMessage(e));
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

    @Override
    public Type getType() {
        return Type.YAML;
    }
}
