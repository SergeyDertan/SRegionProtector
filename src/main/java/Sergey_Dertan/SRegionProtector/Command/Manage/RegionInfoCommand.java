package Sergey_Dertan.SRegionProtector.Command.Manage;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.Settings.RegionSettings;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class RegionInfoCommand extends SRegionProtectorCommand {

    private RegionManager regionManager;

    private ChunkManager chunkManager;

    private RegionSettings regionSettings;

    public RegionInfoCommand(String name, Map<String, String> messages, RegionManager regionManager, ChunkManager chunkManager, RegionSettings regionSettings) {
        super(name, messages);
        this.regionManager = regionManager;
        this.chunkManager = chunkManager;
        this.regionSettings = regionSettings;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            Messenger.getInstance().sendMessage(sender, "command.list.info");
            return false;
        }
        if (args.length < 1) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(this.usageMessage);
                return false;
            }

            Chunk chunk = this.chunkManager.getChunk((long) ((Player) sender).x, (long) ((Player) sender).z, ((Player) sender).level.getName(), true, false);
            if (chunk == null) {
                Messenger.getInstance().sendMessage(sender, "command.info.region-doesnt-exists", " {@region}", "");
                return false;
            }
            for (Region region : chunk.getRegions()) {
                if (!region.intersectsWith(((Player) sender).boundingBox)) continue;
                String name = region.getName();
                String level = region.getLevel();
                String owner = region.getCreator();
                String owners = String.join(", ", region.getOwners());
                String members = String.join(", ", region.getMembers());
                List<String> flags = new ArrayList<>();
                for (int i = 0; i < RegionFlags.FLAG_AMOUNT; ++i) {
                    if (!this.regionSettings.flagsStatus[i]) continue;
                    flags.add(RegionFlags.getFlagName(i) + ": " + (region.getFlagList().getFlagState(i) ? "enabled" : "disabled")); //TODO
                }
                Messenger.getInstance().sendMessage(sender, "command.info.info",
                        new String[]{"@region", "@creator", "@level", "@owners", "@members", "@flags"},
                        new String[]{name, owner, level, owners, members, String.join(", ", flags)}
                );
                return false;
            }
            Messenger.getInstance().sendMessage(sender, "command.info.region-doesnt-exists");
            return false;
        }
        String rgName = args[0].toLowerCase();
        if (rgName.equals("") || rgName.length() == 0) {
            Messenger.getInstance().sendMessage(sender, "command.info.region-doesnt-exists", "@region", rgName);
            return false;
        }
        Region region = this.regionManager.getRegion(rgName);
        if (region == null) {
            Messenger.getInstance().sendMessage(sender, "command.info.region-doesnt-exists", "@region", rgName);
            return false;
        }
        String name = region.getName();
        String level = region.getLevel();
        String owner = region.getCreator();
        String owners = String.join(", ", region.getOwners());
        String members = String.join(", ", region.getMembers());
        List<String> flags = new ArrayList<>();
        for (int i = 0; i < RegionFlags.FLAG_AMOUNT; ++i) {
            if (!this.regionSettings.flagsStatus[i]) continue;
            flags.add(RegionFlags.getFlagName(i) + ": " + (region.getFlagList().getFlagState(i) ? "enabled" : "disabled")); //TODO
        }
        Messenger.getInstance().sendMessage(sender, "command.info.info",
                new String[]{"@region", "@creator", "@level", "@owners", "@members", "@flags"},
                new String[]{name, owner, level, owners, members, String.join(", ", flags)}
        );
        return true;
    }
}