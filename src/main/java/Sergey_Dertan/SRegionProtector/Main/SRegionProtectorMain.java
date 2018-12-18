package Sergey_Dertan.SRegionProtector.Main;

import Sergey_Dertan.SRegionProtector.BlockEntity.BlockEntityHealer;
import Sergey_Dertan.SRegionProtector.Command.Creation.CreateRegionCommand;
import Sergey_Dertan.SRegionProtector.Command.Creation.GetWandCommand;
import Sergey_Dertan.SRegionProtector.Command.Creation.SetPos1Command;
import Sergey_Dertan.SRegionProtector.Command.Creation.SetPos2Command;
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
import Sergey_Dertan.SRegionProtector.Provider.Provider;
import Sergey_Dertan.SRegionProtector.Provider.YAMLProvider;
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
    private Provider provider;
    private RegionManager regionManager;
    private ChunkManager chunkManager;
    private RegionSelector regionSelector;
    private Messenger messenger;

    public static SRegionProtectorMain getInstance() {
        return SRegionProtectorMain.instance;
    }

    @Override
    public void onEnable() {
        if (!this.createDirectories()) return;
        this.initMessenger();

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.start", "@ver", this.getDescription().getVersion()));

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.settings"));
        this.initSettings();

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("loading.init.data-provider"));
        this.initDataProvider("yml"); //TODO add more providers

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

    private void initAutoSave() {
        this.getServer().getScheduler().scheduleDelayedRepeatingTask(this, new AutoSaveTask(this.chunkManager, this.regionManager, this.getLogger()), this.settings.autoSavePeriod, this.settings.autoSavePeriod, true);
    }

    private void registerBlockEntities() {
        BlockEntity.registerBlockEntity(BlockEntityHealer.BLOCK_ENTITY_HEALER, BlockEntityHealer.class);
    }

    private void initSessionsClearTask() {
        this.getServer().getScheduler().scheduleRepeatingTask(this, new ClearSessionsTask(this.regionSelector), (int) this.settings.getConfig().get("select-session-clear-interval") * 20);
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

    private void initSettings() {
        this.settings = new Settings();
        this.settings.init(this);
    }

    private void initDataProvider(String provider) {
        switch (provider) {
            case "yaml":
            case "yml":
            default:
                this.provider = new YAMLProvider(this.getLogger());
                break;
        }
    }

    private void initMessenger() {
        try {
            this.messenger = new Messenger();
        } catch (Exception e) {
            this.getLogger().alert(TextFormat.RED + "Messenger initializing error: " + e.getMessage());
            this.getLogger().alert(TextFormat.RED + "Disabling plugin...");
            this.forceShutdown = true;
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void initRegions() {
        this.regionSelector = new RegionSelector(this.settings.selectorSessionLifetime);
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
        command.setPermission("sregionprotector.command.addmember");
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
        command.setPermission("sregionprotector.command.addmember");
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
        command.setPermission("sregionprotector.command.addmember");
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
    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("disabling.start", "@ver", this.getDescription().getVersion()));
        if (this.forceShutdown) return; //TODO message

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("disabling.regions"));
        this.regionManager.save();

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("disabling.chunks"));
        this.chunkManager.save();

        this.getLogger().info(TextFormat.GREEN + this.messenger.getMessage("disabling.successful"));
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
}