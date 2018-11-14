package Sergey_Dertan.SRegionProtector.Main;

import Sergey_Dertan.SRegionProtector.Command.Creation.CreateRegionCommand;
import Sergey_Dertan.SRegionProtector.Command.Creation.GetWandCommand;
import Sergey_Dertan.SRegionProtector.Command.Creation.SetPos1Command;
import Sergey_Dertan.SRegionProtector.Command.Creation.SetPos2Command;
import Sergey_Dertan.SRegionProtector.Command.Manage.Group.AddMemberCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.Group.AddOwnerCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.Group.RemoveMemberCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.Group.RemoveOwnerCommand;
import Sergey_Dertan.SRegionProtector.Command.Manage.*;
import Sergey_Dertan.SRegionProtector.Event.RegionEventsHandler;
import Sergey_Dertan.SRegionProtector.Event.SelectorEventsHandler;
import Sergey_Dertan.SRegionProtector.Provider.Provider;
import Sergey_Dertan.SRegionProtector.Provider.YAMLProvider;
import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import Sergey_Dertan.SRegionProtector.Settings.Settings;
import Sergey_Dertan.SRegionProtector.Task.ClearSessionsTask;
import cn.nukkit.Server;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public final class SRegionProtectorMain extends PluginBase {

    public static final String SRegionProtectorMainFolder = Server.getInstance().getDataPath() + "Sergey_Dertan_Plugins/SRegionProtector/";
    public static final String SRegionProtectorRegionsFolder = SRegionProtectorMainFolder + "Regions/";
    public static final String SRegionProtectorChunksFolder = SRegionProtectorMainFolder + "Chunks/";
    public static final String SRegionProtectorFlagsFolder = SRegionProtectorMainFolder + "Flags/";

    private static SRegionProtectorMain instance;

    public boolean forceShutdown = false; //TODO

    private Settings settings;
    private Provider provider;
    private RegionManager regionManager;
    private ChunkManager chunkManager;
    private RegionSelector regionSelector;

    public static SRegionProtectorMain getInstance() {
        return SRegionProtectorMain.instance;
    }

    @Override
    public void onEnable() {
        this.getLogger().info(TextFormat.GREEN + "Initializing SRegionProtector V_" + this.getDescription().getVersion() + " by Sergey Dertan...");

        this.getLogger().info(TextFormat.GREEN + "Creating folders...");
        if (!this.createDirectories()) return;

        this.getLogger().info(TextFormat.GREEN + "Settings initializing...");
        this.initSettings();

        this.getLogger().info(TextFormat.GREEN + "Data provider initializing...");
        this.initDataProvider("yml"); //TODO add more providers

        this.getLogger().info(TextFormat.GREEN + "Regions initializing...");
        this.initRegions();

        this.getLogger().info(TextFormat.GREEN + "Chunks initializing...");
        this.initChunks();

        this.getLogger().info(TextFormat.GREEN + "Events handlers initializing...");
        this.initEventsHandlers();

        this.getLogger().info(TextFormat.GREEN + "Commands initializing...");
        this.initCommands();

        this.initSessionsClearTask();
        SRegionProtectorMain.instance = this;

        this.getLogger().info(TextFormat.GREEN + "SRegionProtector initialized.");
    }

    private void initSessionsClearTask() {
        this.getServer().getScheduler().scheduleRepeatingTask(this, new ClearSessionsTask(this.regionSelector), (int) this.settings.getConfig().get("select-session-clear-interval") * 20);
    }

    private boolean createDirectories() {
        return
                this.createFolder(SRegionProtectorMainFolder) &&
                        this.createFolder(SRegionProtectorRegionsFolder) &&
                        this.createFolder(SRegionProtectorChunksFolder) &&
                        this.createFolder(SRegionProtectorFlagsFolder);
    }

    private boolean createFolder(String path) {
        File folder = new File(path);
        if (!folder.exists() && !folder.mkdirs()) {
            this.forceShutdown = true;
            this.getLogger().warning("Can`t create folder " + path + ", disabling plugin...");
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
        }
    }

    private void initRegions() {
        this.regionSelector = new RegionSelector();
        this.regionManager = new RegionManager(this.provider, this.getLogger());
        this.regionManager.init();
    }

    private void initChunks() {
        this.chunkManager = new ChunkManager(this.provider, this.getLogger(), this.regionManager);
        this.chunkManager.init();
        this.regionManager.setChunkManager(chunkManager);
    }

    private void initEventsHandlers() {
        this.getServer().getPluginManager().registerEvents(new RegionEventsHandler(this.chunkManager, this.settings.regionSettings.flagsStatus), this);
        this.getServer().getPluginManager().registerEvents(new SelectorEventsHandler(this.regionSelector), this);
    }

    private void initCommands() { //TODO rewrite
        ConfigSection messages = this.settings.getMessages();

        SetPos1Command setPos1Command = new SetPos1Command("pos1", (Map<String, String>) messages.getOrDefault("pos1", new HashMap<>()), this.regionSelector);
        setPos1Command.setDescription("set first pos");
        setPos1Command.setPermission("sregionprotector.command.pos1");
        Map<String, CommandParameter[]> setPos1CommandParameters = new HashMap<>();
        setPos1Command.setCommandParameters(setPos1CommandParameters);
        this.getServer().getCommandMap().register("pos1", setPos1Command);

        SetPos2Command setPos2Command = new SetPos2Command("pos2", (Map<String, String>) messages.getOrDefault("pos2", new HashMap<>()), this.regionSelector);
        setPos2Command.setDescription("set second pos");
        setPos2Command.setPermission("sregionprotector.command.pos2");
        Map<String, CommandParameter[]> setPos2CommandParameters = new HashMap<>();
        setPos2Command.setCommandParameters(setPos2CommandParameters);
        this.getServer().getCommandMap().register("pos2", setPos2Command);

        CreateRegionCommand createRegionCommand = new CreateRegionCommand("rgcreate", (Map<String, String>) messages.getOrDefault("create", new HashMap<>()), this.regionSelector, this.regionManager, this.settings.regionSettings);
        createRegionCommand.setDescription("create new region");
        createRegionCommand.setPermission("sregionprotector.command.create");
        Map<String, CommandParameter[]> createRegionCommandParameters = new HashMap<>();
        createRegionCommandParameters.put("rgname", new CommandParameter[]{new CommandParameter("region", CommandParamType.STRING, false)});
        createRegionCommand.setCommandParameters(createRegionCommandParameters);
        this.getServer().getCommandMap().register("rgcreate", createRegionCommand);

        GetWandCommand getWandCommand = new GetWandCommand("wand", (Map<String, String>) messages.getOrDefault("wand", new HashMap<>()));
        getWandCommand.setDescription("get a wand for region selecting");
        getWandCommand.setPermission("sregionprotector.command.wand");
        Map<String, CommandParameter[]> getWantCommandParameters = new HashMap<>();
        getWandCommand.setCommandParameters(getWantCommandParameters);
        this.getServer().getCommandMap().register("wand", getWandCommand);

        RegionFlagCommand regionFlagCommand = new RegionFlagCommand("rgflag", (Map<String, String>) messages.getOrDefault("flag", new HashMap<>()), this.regionManager);
        regionFlagCommand.setDescription("change the region flag state");
        regionFlagCommand.setPermission("sregionprotector.command.flag");
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
        regionFlagCommand.setCommandParameters(regionFlagCommandParameters);
        this.getServer().getCommandMap().register("rgflag", regionFlagCommand);

        RegionInfoCommand regionInfoCommand = new RegionInfoCommand("rginfo", (Map<String, String>) messages.getOrDefault("info", new HashMap<>()), this.regionManager, this.chunkManager, this.settings.regionSettings);
        regionInfoCommand.setDescription("region info");
        regionInfoCommand.setPermission("sregionprotector.command.info");
        Map<String, CommandParameter[]> regionInfoCommandParameters = new HashMap<>();
        regionInfoCommandParameters.put("rginfo", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.STRING, true)
                }
        );
        regionInfoCommand.setCommandParameters(regionInfoCommandParameters);
        this.getServer().getCommandMap().register("rginfo", regionInfoCommand);

        RegionListCommand regionListCommand = new RegionListCommand("rglist", (Map<String, String>) messages.getOrDefault("list", new HashMap<>()), this.regionManager);
        regionListCommand.setDescription("your regions");
        regionListCommand.setPermission("sregionprotector.command.list");
        Map<String, CommandParameter[]> regionListCommandParameters = new HashMap<>();
        regionListCommandParameters.put("list-type", new CommandParameter[]
                {
                        new CommandParameter("owner:member:creator", CommandParamType.TEXT, false)
                }
        );
        regionListCommand.setCommandParameters(regionListCommandParameters);
        this.getServer().getCommandMap().register("rglist", regionListCommand);

        RegionRemoveCommand regionRemoveCommand = new RegionRemoveCommand("rgremove", (Map<String, String>) messages.getOrDefault("remove", new HashMap<>()), this.regionManager);
        regionRemoveCommand.setDescription("remove region");
        regionRemoveCommand.setPermission("sregionprotector.command.remove");
        Map<String, CommandParameter[]> regionRemoveCommandParameters = new HashMap<>();
        regionRemoveCommandParameters.put("rgremove-rg", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.TEXT, false)
                }
        );
        regionRemoveCommand.setCommandParameters(regionRemoveCommandParameters);
        this.getServer().getCommandMap().register("rgremove", regionRemoveCommand);

        RegionTeleportCommand regionTeleportCommand = new RegionTeleportCommand("rgtp", (Map<String, String>) messages.getOrDefault("teleport", new HashMap<>()), this.regionManager);
        regionTeleportCommand.setDescription("teleport to region");
        regionTeleportCommand.setPermission("sregionprotector.command.teleport");
        Map<String, CommandParameter[]> regionTeleportCommandParameters = new HashMap<>();
        regionTeleportCommandParameters.put("rgp-rg", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.TEXT, false)
                }
        );
        regionTeleportCommand.setCommandParameters(regionTeleportCommandParameters);
        this.getServer().getCommandMap().register("rgtp", regionTeleportCommand);

        AddMemberCommand addMemberCommand = new AddMemberCommand("rgaddmember", (Map<String, String>) messages.getOrDefault("addmember", new HashMap<>()), this.regionManager);
        addMemberCommand.setDescription("add region member");
        addMemberCommand.setPermission("sregionprotector.command.addmember");
        Map<String, CommandParameter[]> addMemberCommandParameters = new HashMap<>();
        addMemberCommandParameters.put("addmember", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.TEXT, false),
                        new CommandParameter("player", CommandParamType.TARGET, false)
                }
        );
        addMemberCommand.setCommandParameters(addMemberCommandParameters);
        this.getServer().getCommandMap().register("rgaddmember", addMemberCommand);

        AddOwnerCommand addOwnerCommand = new AddOwnerCommand("rgaddowner", (Map<String, String>) messages.getOrDefault("addowner", new HashMap<>()), this.regionManager);
        addOwnerCommand.setDescription("add region owner");
        addOwnerCommand.setPermission("sregionprotector.command.addmember");
        Map<String, CommandParameter[]> addOwnerCommandParameters = new HashMap<>();
        addOwnerCommandParameters.put("addowner", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.TEXT, false),
                        new CommandParameter("player", CommandParamType.TARGET, false)
                }
        );
        addOwnerCommand.setCommandParameters(addOwnerCommandParameters);
        this.getServer().getCommandMap().register("rgaddowner", addOwnerCommand);

        RemoveMemberCommand removeMemberCommand = new RemoveMemberCommand("rgremovemember", (Map<String, String>) messages.getOrDefault("removemember", new HashMap<>()), this.regionManager);
        removeMemberCommand.setDescription("remove region member");
        removeMemberCommand.setPermission("sregionprotector.command.addmember");
        Map<String, CommandParameter[]> removeMemberCommandParameters = new HashMap<>();
        removeMemberCommandParameters.put("removemember", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.TEXT, false),
                        new CommandParameter("player", CommandParamType.TARGET, false)
                }
        );
        removeMemberCommand.setCommandParameters(removeMemberCommandParameters);
        this.getServer().getCommandMap().register("rgremovemember", removeMemberCommand);

        RemoveOwnerCommand removeOwnerCommand = new RemoveOwnerCommand("rgremoveowner", (Map<String, String>) messages.getOrDefault("removeowner", new HashMap<>()), this.regionManager);
        removeOwnerCommand.setDescription("remove region owner");
        removeOwnerCommand.setPermission("sregionprotector.command.addmember");
        Map<String, CommandParameter[]> removeOwnerCommandParameters = new HashMap<>();
        removeOwnerCommandParameters.put("removeowner", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.TEXT, false),
                        new CommandParameter("player", CommandParamType.TARGET, false)
                }
        );
        removeOwnerCommand.setCommandParameters(removeOwnerCommandParameters);
        this.getServer().getCommandMap().register("rgremoveowner", removeOwnerCommand);
    }

    @Override
    public void onDisable() {
        this.getLogger().info(TextFormat.GREEN + "Disabling SRegionProtector V_" + this.getDescription().getVersion() + " by Sergey Dertan...");
        if (this.forceShutdown) return;

        this.getLogger().info(TextFormat.GREEN + "Saving regions...");
        this.regionManager.save();

        this.getLogger().info(TextFormat.GREEN + "Saving chunks...");
        this.chunkManager.save();

        this.getLogger().info(TextFormat.GREEN + "SRegionProtector disabled.");
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