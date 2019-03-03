package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.Settings.RegionSettings;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.math.Vector3;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.Map;

public final class RegionInfoCommand extends SRegionProtectorCommand {

    private final RegionManager regionManager;
    private final ChunkManager chunkManager;
    private final RegionSettings regionSettings;

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
            Region region = this.chunkManager.getRegion((Vector3) sender, ((Player) sender).level.getName());
            if (region != null) {
                this.showRegionInfo(sender, region);
                return true;
            }
            this.messenger.sendMessage(sender, "command.info.region-doesnt-exists", "{@region}", "");
            return false;
        }
        Region region = this.regionManager.getRegion(args[0]);
        if (region == null) {
            this.messenger.sendMessage(sender, "command.info.region-doesnt-exists", "@region", args[0]);
            return false;
        }
        this.showRegionInfo(sender, region);
        return true;
    }

    private void showRegionInfo(CommandSender sender, Region region) {
        String name = region.name;
        String level = region.level;
        String owner = region.getCreator();
        String owners = String.join(", ", region.getOwners());
        String members = String.join(", ", region.getMembers());
        String size = Long.toString(Math.round((region.maxX - region.minX) * (region.maxY - region.minY) * (region.maxZ - region.minZ)));
        List<String> flags = new ObjectArrayList<>();
        for (int i = 0; i < RegionFlags.FLAG_AMOUNT; ++i) {
            if (!this.regionSettings.flagsStatus[i] || !this.regionSettings.display[i]) continue;
            flags.add(RegionFlags.getFlagName(i) + ": " + (region.getFlagState(i) ? this.messenger.getMessage("region.flag.state.enabled") : this.messenger.getMessage("region.flag.state.disabled")));
        }
        this.messenger.sendMessage(sender, "command.info.info",
                new String[]{"@region", "@creator", "@level", "@owners", "@members", "@flags", "@size"},
                new String[]{name, owner, level, owners, members, String.join(", ", flags), size}
        );
    }
}
