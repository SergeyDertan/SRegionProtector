package Sergey_Dertan.SRegionProtector.Main;

import Sergey_Dertan.SRegionProtector.BlockEntity.BlockEntityHealer;
import Sergey_Dertan.SRegionProtector.Command.Admin.SaveCommand;
import Sergey_Dertan.SRegionProtector.Command.Creation.*;
import Sergey_Dertan.SRegionProtector.Command.Manage.Group.AddMemberCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.Group.AddOwnerCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.Group.RemoveMemberCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.Group.RemoveOwnerCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.Purchase.BuyRegionCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.Purchase.RegionPriceCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.Purchase.RegionRemoveFromSaleCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.Purchase.RegionSellCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.*;
import Sergey_Dertan.SRegionProtector.Command.RegionCommand;
import Sergey_Dertan.SRegionProtector.Economy.AbstractEconomy;
import Sergey_Dertan.SRegionProtector.Economy.OneBoneEconomyAPI;
import Sergey_Dertan.SRegionProtector.Event.RegionEventsHandler;
import Sergey_Dertan.SRegionProtector.Event.SelectorEventsHandler;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Provider.DataProvider;
import Sergey_Dertan.SRegionProtector.Provider.MySQLDataProvider;
import Sergey_Dertan.SRegionProtector.Provider.YAMLDataProvider;
import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import Sergey_Dertan.SRegionProtector.Settings.Settings;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.command.Command;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.ThreadCache;
import cn.nukkit.utils.Utils;

import java.io.File;
import java.util.Map;

import static Sergey_Dertan.SRegionProtector.Utils.Utils.compareVersions;
import static Sergey_Dertan.SRegionProtector.Utils.Utils.httpGetRequestJson;

@SuppressWarnings("WeakerAccess")
public final class SRegionProtectorMain extends PluginBase {

    public static final String MAIN_FOLDER = Server.getInstance().getDataPath() + "Sergey_Dertan_Plugins/SRegionProtector/";
    public static final String REGIONS_FOLDER = MAIN_FOLDER + "Regions/";
    public static final String FLAGS_FOLDER = MAIN_FOLDER + "Flags/";
    public static final String LANG_FOLDER = MAIN_FOLDER + "Lang/";

    public static final String VERSION_URL = "https://api.github.com/repos/SergeyDertan/SRegionProtector/releases/latest";

    private static SRegionProtectorMain instance;

    public boolean forceShutdown = false; //TODO

    private Settings settings;
    private DataProvider provider;
    private RegionManager regionManager;
    private ChunkManager chunkManager;
    private RegionSelector regionSelector;
    private Messenger messenger;

    private RegionCommand mainCmd;

    public static SRegionProtectorMain getInstance() {
        return SRegionProtectorMain.instance;
    }

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        if (!this.createDirectories()) {
            this.forceShutdown = true;
            this.getPluginLoader().disablePlugin(this);
            return;
        }
        if (!this.initMessenger()) return;

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.start", "@ver", this.getDescription().getVersion()));

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.settings"));
        if (!this.initSettings()) return;

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.data-provider"));
        if (!this.initDataProvider()) return;

        this.initChunks();

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.regions"));
        this.initRegions();

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.events-handlers"));
        this.initEventsHandlers();

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.commands"));
        this.initCommands();

        this.registerBlockEntities();

        this.initAutoSave();

        this.initSessionsClearTask();

        this.gc();

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.successful", "@time", Long.toString(System.currentTimeMillis() - start)));

        this.getServer().getScheduler().scheduleTask(this, this::checkUpdate, true);

        instance = this;
    }

    private void gc() {
        ThreadCache.clean();
        System.gc();
    }

    @SuppressWarnings("ConstantConditions")
    private boolean initDataProvider() {
        try {
            switch (this.settings.provider) {
                default:
                case YAML:
                    this.provider = new YAMLDataProvider(this.getLogger(), this.settings.multithreadedDataLoading, this.settings.dataLoadingThreads);
                    break;
                case MYSQL:
                    this.provider = new MySQLDataProvider(this.getLogger(), this.settings.mySQLSettings);
                    //this.provider = new YAMLDataProvider(this.getLogger(), this.settings.multithreadedDataLoading, this.settings.dataLoadingThreads); //TODO mysql
                    break;
                case SQLite3:
                    this.provider = new YAMLDataProvider(this.getLogger(), this.settings.multithreadedDataLoading, this.settings.dataLoadingThreads); //TODO sqlite
            }
            this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.data-provider", "@name", this.settings.provider.name));
            return true;
        } catch (Exception e) {
            this.getLogger().info(TextFormat.RED + this.messenger.getMessage("loading.error.data-provider-error", new String[]{"@err", "@provider"}, new String[]{e.getMessage(), this.settings.provider.name}));
            this.forceShutdown = true;
            this.getPluginLoader().disablePlugin(this);
            return false;
        }
    }

    private void initAutoSave() {
        if (!this.settings.autoSave) return;
        this.getServer().getScheduler().scheduleDelayedRepeatingTask(this, () -> this.save(SaveType.AUTO), this.settings.autoSavePeriod, this.settings.autoSavePeriod, true);
    }

    private void registerBlockEntities() {
        BlockEntity.registerBlockEntity(BlockEntityHealer.BLOCK_ENTITY_HEALER, BlockEntityHealer.class);
    }

    private void initSessionsClearTask() {
        this.getServer().getScheduler().scheduleRepeatingTask(this, () -> this.regionSelector.clear(), ((Number) this.settings.getConfig().get("select-session-clear-interval")).intValue() * 20, true);
    }

    private boolean createDirectories() {
        return
                this.createFolder(MAIN_FOLDER) &&
                        this.createFolder(REGIONS_FOLDER) &&
                        this.createFolder(FLAGS_FOLDER) &&
                        this.createFolder(LANG_FOLDER);
    }

    private boolean createFolder(String path) {
        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            this.forceShutdown = true;
            this.getLogger().warning(this.messenger.getMessage("loading.error.folder", "@path", path));
            return false;
        }
        return true;
    }

    private boolean initSettings() {
        try {
            this.settings = new Settings();
            return true;
        } catch (Exception e) {
            this.getLogger().info(TextFormat.RED + Messenger.getInstance().getMessage("loading.error.resource", "@err", e.getMessage()));
            this.forceShutdown = true;
            this.getLogger().alert(Utils.getExceptionMessage(e));
            this.getPluginLoader().disablePlugin(this);
            return false;
        }
    }

    private boolean initMessenger() {
        try {
            this.messenger = new Messenger();
            return true;
        } catch (Exception e) {
            this.getLogger().alert(TextFormat.RED + "Messenger initializing error");

            this.getLogger().alert(TextFormat.RED + Utils.getExceptionMessage(e));

            this.getLogger().alert(TextFormat.RED + "Disabling plugin...");
            this.forceShutdown = true;
            this.getPluginLoader().disablePlugin(this);
            return false;
        }
    }

    private void initRegions() {
        this.regionSelector = new RegionSelector(this.settings.selectorSessionLifetime, this.settings.borderBlock, this.settings.asyncCommands);
        this.regionManager = new RegionManager(this.provider, this.getLogger(), this.chunkManager);
        this.regionManager.init();
    }

    private void initChunks() {
        this.chunkManager = new ChunkManager(this.getLogger());
        this.chunkManager.init();
    }

    private void initEventsHandlers() {
        this.getServer().getPluginManager().registerEvents(new RegionEventsHandler(this.chunkManager, this.settings.regionSettings.flagsStatus, this.settings.regionSettings.needMessage, this.settings.prioritySystem), this);
        this.getServer().getPluginManager().registerEvents(new SelectorEventsHandler(this.regionSelector), this);
    }

    public void save(SaveType saveType) {
        this.save(saveType, null);
    }

    public synchronized void save(SaveType saveType, String initiator) {
        switch (saveType) {
            case AUTO:
                this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("auto-save-start"));
                break;
            case MANUAL:
                this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("manual-save-start", "@initiator", initiator));
                break;
            case DISABLING:
                this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("disabling-save-start"));
                break;
        }
        this.regionManager.save(saveType, initiator);
        this.gc();
        switch (saveType) {
            case AUTO:
                this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("auto-save-success"));
                break;
            case MANUAL:
                this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("manual-save-success", "@initiator", initiator));
                break;
            case DISABLING:
                this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("disabling.successful"));
                break;
        }
    }

    private void registerCommand(Command command) {
        if (!this.settings.hideCommands) this.getServer().getCommandMap().register(command.getName(), command);
        this.mainCmd.registerCommand(command);
    }

    private void initCommands() {
        this.mainCmd = new RegionCommand(this.settings.asyncCommands, this.settings.asyncCommandsThreads);
        this.getServer().getCommandMap().register(this.mainCmd.getName(), this.mainCmd);

        this.registerCommand(new SetPos1Command(this.regionSelector));

        this.registerCommand(new SetPos2Command(this.regionSelector));

        this.registerCommand(new CreateRegionCommand(this.regionSelector, this.regionManager, this.settings.regionSettings));

        this.registerCommand(new GetWandCommand());

        this.registerCommand(new RegionFlagCommand(this.regionManager));

        this.registerCommand(new RegionInfoCommand(this.regionManager, this.chunkManager, this.settings.regionSettings));

        this.registerCommand(new RegionListCommand(this.regionManager));

        this.registerCommand(new RegionRemoveCommand(this.regionManager));

        this.registerCommand(new RegionTeleportCommand(this.regionManager));

        this.registerCommand(new AddMemberCommand(this.regionManager));

        this.registerCommand(new AddOwnerCommand(this.regionManager));

        this.registerCommand(new RemoveMemberCommand(this.regionManager));

        this.registerCommand(new RemoveOwnerCommand(this.regionManager));

        this.registerCommand(new SaveCommand(this));

        this.registerCommand(new RegionSizeCommand(this.regionSelector));

        this.registerCommand(new ShowBorderCommand(this.regionSelector));

        this.registerCommand(new RegionSelectCommand(this.regionManager, this.regionSelector));

        this.registerCommand(new RemoveBordersCommand(this.regionSelector));

        this.registerCommand(new RegionExpandCommand(this.regionSelector));

        AbstractEconomy economy = null;
        if (this.getServer().getPluginManager().getPlugin("EconomyAPI") != null) economy = new OneBoneEconomyAPI();
        this.registerCommand(new BuyRegionCommand(this.regionManager, economy));

        this.registerCommand(new RegionPriceCommand(this.regionManager));

        this.registerCommand(new RegionSellCommand(this.regionManager));

        this.registerCommand(new RegionRemoveFromSaleCommand(this.regionManager));

        this.registerCommand(new LPos1Command(this.regionSelector, this.settings.lposMaxRadius));

        this.registerCommand(new LPos2Command(this.regionSelector, this.settings.lposMaxRadius));

        this.registerCommand(new SetPriorityCommand(this.regionManager, this.settings.prioritySystem));
    }

    private void checkUpdate() {
        try {
            Map<String, Object> response = httpGetRequestJson(VERSION_URL);
            String ver = (String) response.get("tag_name");
            String description = (String) response.get("name");
            if (ver.isEmpty()) return;
            if (compareVersions(this.getDescription().getVersion(), ver).equals(ver)) {
                this.getLogger().info(this.messenger.getMessage("loading.init.update-available", "@ver", ver));
                this.getLogger().info(this.messenger.getMessage("loading.init.update-description", "@description", description));
            }
        } catch (Exception ignore) {
        }
    }

    private void shutdownExecutors() {
        ((RegionCommand) this.getServer().getCommandMap().getCommand("region")).shutdownExecutor();
        ((SaveCommand) this.getServer().getCommandMap().getCommand("rgsave")).shutdownExecutor();
        if (this.provider instanceof YAMLDataProvider) ((YAMLDataProvider) this.provider).shutdownExecutor();
    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("disabling.start", "@ver", this.getDescription().getVersion()));
        this.shutdownExecutors();
        if (this.forceShutdown) {
            if (this.messenger != null) {
                this.getLogger().info(TextFormat.RED + this.messenger.getMessage("disabling.error"));
            }
            return;
        }
        this.save(SaveType.DISABLING);
    }

    public RegionManager getRegionManager() {
        return this.regionManager;
    }

    public ChunkManager getChunkManager() {
        return this.chunkManager;
    }

    public RegionSelector getRegionSelector() {
        return this.regionSelector;
    }

    public Settings getSettings() {
        return this.settings;
    }

    @SuppressWarnings("EmptyMethod")
    public void dataMigration() {
        //TODO
    }

    public enum SaveType {
        AUTO,
        MANUAL,
        DISABLING
    }
}
