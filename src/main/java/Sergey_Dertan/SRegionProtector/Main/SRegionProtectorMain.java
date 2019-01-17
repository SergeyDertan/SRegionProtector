package Sergey_Dertan.SRegionProtector.Main;

import Sergey_Dertan.SRegionProtector.BlockEntity.BlockEntityHealer;
import Sergey_Dertan.SRegionProtector.Command.Admin.SaveCommand;
import Sergey_Dertan.SRegionProtector.Command.Creation.*;
import Sergey_Dertan.SRegionProtector.Command.Manage.Group.AddMemberCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.Group.AddOwnerCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.Group.RemoveMemberCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.Group.RemoveOwnerCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.*;
import Sergey_Dertan.SRegionProtector.Command.RegionCommand;
import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
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
import Sergey_Dertan.SRegionProtector.Task.AutoSaveTask;
import Sergey_Dertan.SRegionProtector.Task.ClearSessionsTask;
import cn.nukkit.Server;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.utils.ThreadCache;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class SRegionProtectorMain extends PluginBase {

    public static final String SRegionProtectorMainFolder = Server.getInstance().getDataPath() + "Sergey_Dertan_Plugins/SRegionProtector/";
    public static final String SRegionProtectorRegionsFolder = SRegionProtectorMainFolder + "Regions/";
    public static final String SRegionProtectorChunksFolder = SRegionProtectorMainFolder + "Chunks/";
    public static final String SRegionProtectorFlagsFolder = SRegionProtectorMainFolder + "Flags/";
    public static final String SRegionProtectorLangFolder = SRegionProtectorMainFolder + "Lang/";

    private static SRegionProtectorMain instance;

    public boolean forceShutdown = false; //TODO

    private Settings settings;
    private DataProvider provider;
    private RegionManager regionManager;
    private ChunkManager chunkManager;
    private RegionSelector regionSelector;
    private Messenger messenger;

    public static SRegionProtectorMain getInstance() {
        return SRegionProtectorMain.instance;
    }

    @Override
    public void onEnable() {
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

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.regions"));
        this.initRegions();

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.chunks"));
        this.initChunks();

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.events-handlers"));
        this.initEventsHandlers();

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.commands"));
        this.initCommands();

        this.registerBlockEntities();

        this.initAutoSave();

        this.initSessionsClearTask();

        this.gc();

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.successful"));

        instance = this;
    }

    private void gc() {
        ThreadCache.clean();
        System.gc();
    }

    private boolean initDataProvider() {
        try {
            switch (this.settings.provider) {
                default:
                case YAML:
                    this.provider = new YAMLDataProvider(this.getLogger());
                    break;
                case MYSQL:
                    //this.provider = new MySQLDataProvider(this.getLogger(), this.settings.mySQLSettings);
                    this.provider = new YAMLDataProvider(this.getLogger());
                    break;
                case SQLite3:
                    this.provider = new YAMLDataProvider(this.getLogger()); //TODO sqlite
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
        this.getServer().getScheduler().scheduleDelayedRepeatingTask(this, new AutoSaveTask(this), this.settings.autoSavePeriod, this.settings.autoSavePeriod, true);
    }

    private void registerBlockEntities() {
        BlockEntity.registerBlockEntity(BlockEntityHealer.BLOCK_ENTITY_HEALER, BlockEntityHealer.class);
    }

    private void initSessionsClearTask() {
        this.getServer().getScheduler().scheduleRepeatingTask(this, new ClearSessionsTask(this.regionSelector), ((Number) this.settings.getConfig().get("select-session-clear-interval")).intValue() * 20, true);
    }

    private boolean createDirectories() {
        return
                this.createFolder(SRegionProtectorMainFolder) &&
                        this.createFolder(SRegionProtectorRegionsFolder) &&
                        this.createFolder(SRegionProtectorChunksFolder) &&
                        this.createFolder(SRegionProtectorFlagsFolder) &&
                        this.createFolder(SRegionProtectorLangFolder);
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
            this.getPluginLoader().disablePlugin(this);
            return false;
        }
    }

    private boolean initMessenger() {
        try {
            this.messenger = new Messenger();
            return true;
        } catch (Exception e) {
            this.getLogger().alert(TextFormat.RED + "Messenger initializing error: " + e.getMessage());
            this.getLogger().alert(TextFormat.RED + "Disabling plugin...");
            this.forceShutdown = true;
            this.getPluginLoader().disablePlugin(this);
            return false;
        }
    }

    private void initRegions() {
        this.regionSelector = new RegionSelector(this.settings.selectorSessionLifetime, this.settings.borderBlock);
        this.regionManager = new RegionManager(this.provider, this.getLogger());
        this.regionManager.init();
    }

    private void initChunks() {
        this.chunkManager = new ChunkManager(this.provider, this.getLogger(), this.regionManager);
        this.chunkManager.init();
        this.regionManager.setChunkManager(chunkManager);
    }

    private void initEventsHandlers() {
        this.getServer().getPluginManager().registerEvents(new RegionEventsHandler(this.chunkManager, this.settings.regionSettings.flagsStatus, this.settings.regionSettings.needMessage), this);
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
        this.chunkManager.save(saveType, initiator);
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

    private void initCommands() { //TODO rewrite
        RegionCommand rg = new RegionCommand("region");
        rg.setDescription(this.messenger.getMessage("command.region.description"));
        rg.setPermission("sregionprotector.command.region");
        rg.setAliases(new String[]{"rg"});
        this.getServer().getCommandMap().register(rg.getName(), rg);

        SRegionProtectorCommand command;
        command = new SetPos1Command("pos1", this.regionSelector);
        command.setDescription(this.messenger.getMessage("command.pos1.description"));
        command.setPermission("sregionprotector.command.pos1");
        Map<String, CommandParameter[]> setPos1CommandParameters = new HashMap<>();
        command.setCommandParameters(setPos1CommandParameters);
        this.getServer().getCommandMap().register(command.getName(), command);
        rg.registerCommand(command);

        command = new SetPos2Command("pos2", this.regionSelector);
        command.setDescription(this.messenger.getMessage("command.pos2.description"));
        command.setPermission("sregionprotector.command.pos2");
        Map<String, CommandParameter[]> setPos2CommandParameters = new HashMap<>();
        command.setCommandParameters(setPos2CommandParameters);
        this.getServer().getCommandMap().register(command.getName(), command);
        rg.registerCommand(command);

        command = new CreateRegionCommand("rgcreate", this.regionSelector, this.regionManager, this.settings.regionSettings);
        command.setDescription(this.messenger.getMessage("command.create.description"));
        command.setPermission("sregionprotector.command.create");
        Map<String, CommandParameter[]> createRegionCommandParameters = new HashMap<>();
        createRegionCommandParameters.put("rgname", new CommandParameter[]{new CommandParameter("region", CommandParamType.STRING, false)});
        command.setCommandParameters(createRegionCommandParameters);
        this.getServer().getCommandMap().register(command.getName(), command);
        rg.registerCommand(command);

        command = new GetWandCommand("wand");
        command.setDescription(this.messenger.getMessage("command.wand.description"));
        command.setPermission("sregionprotector.command.wand");
        Map<String, CommandParameter[]> getWantCommandParameters = new HashMap<>();
        command.setCommandParameters(getWantCommandParameters);
        this.getServer().getCommandMap().register(command.getName(), command);
        rg.registerCommand(command);

        command = new RegionFlagCommand("rgflag", this.regionManager);
        command.setDescription(this.messenger.getMessage("command.flag.description"));
        command.setPermission("sregionprotector.command.flag");
        Map<String, CommandParameter[]> regionFlagCommandParameters = new HashMap<>();
        regionFlagCommandParameters.put("flagdata", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.STRING, false),
                        new CommandParameter("flag", CommandParamType.STRING, false),
                        new CommandParameter("state", CommandParamType.STRING, false)
                }
        );

        regionFlagCommandParameters.put("sell-flag", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.STRING, false),
                        new CommandParameter("flag", CommandParamType.STRING, false),
                        new CommandParameter("state", CommandParamType.STRING, false),
                        new CommandParameter("price", CommandParamType.INT, false)
                }
        );
        command.setCommandParameters(regionFlagCommandParameters);
        this.getServer().getCommandMap().register(command.getName(), command);
        rg.registerCommand(command);

        command = new RegionInfoCommand("rginfo", this.regionManager, this.chunkManager, this.settings.regionSettings);
        command.setDescription(this.messenger.getMessage("command.info.description"));
        command.setPermission("sregionprotector.command.info");
        Map<String, CommandParameter[]> regionInfoCommandParameters = new HashMap<>();
        regionInfoCommandParameters.put("rginfo", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.STRING, true)
                }
        );
        command.setCommandParameters(regionInfoCommandParameters);
        this.getServer().getCommandMap().register(command.getName(), command);
        rg.registerCommand(command);

        command = new RegionListCommand("rglist", this.regionManager);
        command.setDescription(this.messenger.getMessage("command.list.description"));
        command.setPermission("sregionprotector.command.list");
        Map<String, CommandParameter[]> regionListCommandParameters = new HashMap<>();
        regionListCommandParameters.put("list-type", new CommandParameter[]
                {
                        new CommandParameter("owner:member:creator", CommandParamType.TEXT, false)
                }
        );
        command.setCommandParameters(regionListCommandParameters);
        this.getServer().getCommandMap().register(command.getName(), command);
        rg.registerCommand(command);

        command = new RegionRemoveCommand("rgremove", this.regionManager);
        command.setDescription(this.messenger.getMessage("command.remove.description"));
        command.setPermission("sregionprotector.command.remove");
        Map<String, CommandParameter[]> regionRemoveCommandParameters = new HashMap<>();
        regionRemoveCommandParameters.put("rgremove-rg", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.TEXT, false)
                }
        );
        command.setCommandParameters(regionRemoveCommandParameters);
        this.getServer().getCommandMap().register(command.getName(), command);
        rg.registerCommand(command);

        command = new RegionTeleportCommand("rgtp", this.regionManager);
        command.setDescription(this.messenger.getMessage("command.teleport.description"));
        command.setPermission("sregionprotector.command.teleport");
        Map<String, CommandParameter[]> regionTeleportCommandParameters = new HashMap<>();
        regionTeleportCommandParameters.put("rgp-rg", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.TEXT, false)
                }
        );
        command.setCommandParameters(regionTeleportCommandParameters);
        this.getServer().getCommandMap().register(command.getName(), command);
        rg.registerCommand(command);

        command = new AddMemberCommand("rgaddmember", this.regionManager);
        command.setDescription(this.messenger.getMessage("command.addmember.description"));
        command.setPermission("sregionprotector.command.addmember");
        Map<String, CommandParameter[]> addMemberCommandParameters = new HashMap<>();
        addMemberCommandParameters.put("addmember", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.TEXT, false),
                        new CommandParameter("player", CommandParamType.TARGET, false)
                }
        );
        command.setCommandParameters(addMemberCommandParameters);
        this.getServer().getCommandMap().register(command.getName(), command);
        rg.registerCommand(command);

        command = new AddOwnerCommand("rgaddowner", this.regionManager);
        command.setDescription(this.messenger.getMessage("command.addowner.description"));
        command.setPermission("sregionprotector.command.addowner");
        Map<String, CommandParameter[]> addOwnerCommandParameters = new HashMap<>();
        addOwnerCommandParameters.put("addowner", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.TEXT, false),
                        new CommandParameter("player", CommandParamType.TARGET, false)
                }
        );
        command.setCommandParameters(addOwnerCommandParameters);
        this.getServer().getCommandMap().register(command.getName(), command);
        rg.registerCommand(command);

        command = new RemoveMemberCommand("rgremovemember", this.regionManager);
        command.setDescription(this.messenger.getMessage("command.removemember.description"));
        command.setPermission("sregionprotector.command.removemember");
        Map<String, CommandParameter[]> removeMemberCommandParameters = new HashMap<>();
        removeMemberCommandParameters.put("removemember", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.TEXT, false),
                        new CommandParameter("player", CommandParamType.TARGET, false)
                }
        );
        command.setCommandParameters(removeMemberCommandParameters);
        this.getServer().getCommandMap().register(command.getName(), command);
        rg.registerCommand(command);

        command = new RemoveOwnerCommand("rgremoveowner", this.regionManager);
        command.setDescription(this.messenger.getMessage("command.removeowner.description"));
        command.setPermission("sregionprotector.command.removeowner");
        Map<String, CommandParameter[]> removeOwnerCommandParameters = new HashMap<>();
        removeOwnerCommandParameters.put("removeowner", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.TEXT, false),
                        new CommandParameter("player", CommandParamType.TARGET, false)
                }
        );
        command.setCommandParameters(removeOwnerCommandParameters);
        this.getServer().getCommandMap().register(command.getName(), command);
        rg.registerCommand(command);

        command = new SaveCommand("rgsave", this);
        command.setDescription(this.messenger.getMessage("command.save.description"));
        command.setPermission("sregionprotector.command.save");
        Map<String, CommandParameter[]> saveCommandParameters = new HashMap<>();
        saveCommandParameters.put("rgsave", new CommandParameter[0]);
        command.setCommandParameters(saveCommandParameters);
        rg.registerCommand(command);
        this.getServer().getCommandMap().register(command.getName(), command);

        command = new RegionSizeCommand("rgsize", this.regionSelector);
        command.setDescription(this.messenger.getMessage("command.size.description"));
        command.setPermission("sregionprotector.command.size");
        Map<String, CommandParameter[]> sizeCommandParameters = new HashMap<>();
        sizeCommandParameters.put("rgsize", new CommandParameter[0]);
        command.setCommandParameters(sizeCommandParameters);
        rg.registerCommand(command);
        this.getServer().getCommandMap().register(command.getName(), command);

        command = new ShowBorderCommand("rgshowborder", this.regionSelector);
        command.setDescription(this.messenger.getMessage("command.show-border.description"));
        command.setPermission("sregionprotector.command.show-border");
        Map<String, CommandParameter[]> showBorderCommandParameters = new HashMap<>();
        showBorderCommandParameters.put("rgshowborder", new CommandParameter[0]);
        command.setCommandParameters(showBorderCommandParameters);
        rg.registerCommand(command);
        this.getServer().getCommandMap().register(command.getName(), command);

        command = new RegionSelectCommand("rgselect", this.regionManager, this.regionSelector);
        command.setDescription(this.messenger.getMessage("command.select.description"));
        command.setPermission("sregionprotector.command.select");
        Map<String, CommandParameter[]> regionSelectCommandParameters = new HashMap<>();
        regionSelectCommandParameters.put("rgselect", new CommandParameter[0]);
        command.setCommandParameters(regionSelectCommandParameters);
        rg.registerCommand(command);
        this.getServer().getCommandMap().register(command.getName(), command);

        command = new RemoveBordersCommand("rgremoveborders", this.regionSelector);
        command.setDescription(this.messenger.getMessage("command.remove-borders.description"));
        command.setPermission("sregionprotector.command.remove-borders");
        Map<String, CommandParameter[]> removeBordersCommandParameters = new HashMap<>();
        removeBordersCommandParameters.put("rgremoveborders", new CommandParameter[0]);
        command.setCommandParameters(removeBordersCommandParameters);
        rg.registerCommand(command);
        this.getServer().getCommandMap().register(command.getName(), command);

        command = new RegionExpandCommand("rgexpand", this.regionSelector);
        command.setDescription(this.messenger.getMessage("command.expand.description"));
        command.setPermission("sregionprotector.command.expand");
        Map<String, CommandParameter[]> expandCommandParameters = new HashMap<>();
        expandCommandParameters.put("rgexpand", new CommandParameter[0]);
        command.setCommandParameters(expandCommandParameters);
        rg.registerCommand(command);
        this.getServer().getCommandMap().register(command.getName(), command);
    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("disabling.start", "@ver", this.getDescription().getVersion()));
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

    public void dataMigration() {
        //TODO
    }

    public enum SaveType {
        AUTO,
        MANUAL,
        DISABLING
    }
}
