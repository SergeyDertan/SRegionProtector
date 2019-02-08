package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.Settings.RegionSettings;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class RegionInfoCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;

    private ChunkManager chunkManager;

    private RegionSettings regionSettings;

    public RegionInfoCommand(RegionManager regionManager, ChunkManager chunkManager, RegionSettings regionSettings) {
        super("rginfo", "info");
        this.regionManager = regionManager;
        this.chunkManager = chunkManager;
        this.regionSettings = regionSettings;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("rginfo", new CommandParameter[]
                {
                        new CommandParameter("region", CommandParamType.STRING, true)
                }
        );
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.list.info");
            return false;
        }
        if (args.length < 1) {
            if (!(sender instanceof Player)) {
                this.messenger.sendMessage(sender, "command.list.usage");
                return false;
            }
            Chunk chunk = this.chunkManager.getChunk((long) ((Player) sender).x, (long) ((Player) sender).z, ((Player) sender).level.getName(), true, false);
            if (chunk == null) {
                this.messenger.sendMessage(sender, "command.info.region-doesnt-exists", "@region", "");
                return false;
            }
            for (Region region : chunk.getRegions()) {
                if (!region.intersectsWith(((Player) sender).boundingBox)) continue;
                this.showRegionInfo(sender, region);
                return false;
            }
            this.messenger.sendMessage(sender, "command.info.region-doesnt-exists", "@region", "");
            return false;
        }
        String rgName = args[0];
        if (rgName.isEmpty()) {
            this.messenger.sendMessage(sender, "command.info.region-doesnt-exists", "@region", rgName);
            return false;
        }
        Region region = this.regionManager.getRegion(rgName);
        if (region == null) {
            this.messenger.sendMessage(sender, "command.info.region-doesnt-exists", "@region", rgName);
            return false;
        }
        this.showRegionInfo(sender, region);
        return true;
    }

    private void showRegionInfo(CommandSender sender, Region region) {
        String name = region.getName();
        String level = region.level;
        String owner = region.getCreator();
        String owners = String.join(", ", region.getOwners());
        String members = String.join(", ", region.getMembers());
        String size = String.valueOf(Math.round((region.maxX - region.minX) * (region.maxY - region.minY) * (region.maxZ - region.minZ)));
        Set<String> flags = new HashSet<>();
        for (int i = 0; i < RegionFlags.FLAG_AMOUNT; ++i) {
            if (!this.regionSettings.flagsStatus[i]) continue;
            flags.add(RegionFlags.getFlagName(i) + ": " + (region.getFlagState(i) ? this.messenger.getMessage("region.flag.state.enabled") : this.messenger.getMessage("region.flag.state.disabled")));
        }
        this.messenger.sendMessage(sender, "command.info.info",
                new String[]{"@region", "@creator", "@level", "@owners", "@members", "@flags", "@size"},
                new String[]{name, owner, level, owners, members, String.join(", ", flags), size}
        );
    }
}
