package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.RegionGroup;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import Sergey_Dertan.SRegionProtector.Region.Selector.SelectorSession;
import Sergey_Dertan.SRegionProtector.Settings.RegionSettings;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.level.Position;

import java.util.Map;

public final class CreateRegionCommand extends SRegionProtectorCommand {

    private RegionSelector selector;
    private RegionManager regionManager;
    private RegionSettings regionSettings;

    public CreateRegionCommand(String name, Map<String, String> messages, RegionSelector selector, RegionManager regionManager, RegionSettings regionSettings) {
        super(name, messages);
        this.selector = selector;
        this.regionManager = regionManager;
        this.regionSettings = regionSettings;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermission(sender)) return false;
        if (!(sender instanceof Player)) {
            this.sendMessage(sender, "in-game");
            return false;
        }

        if (args.length < 1) {
            sender.sendMessage(this.usageMessage);
            return false;
        }

        SelectorSession session = this.selector.getSession((Player) sender);
        Position pos1 = session.pos1;
        Position pos2 = session.pos2;
        String name = args[0].toLowerCase();

        if (name.isEmpty()) {
            sender.sendMessage(this.usageMessage);
            return false;
        }
        if (name.length() < this.regionSettings.minRegionNameLength || name.length() > regionSettings.maxRegionNameLength) {
            this.sendMessage(sender, "incorrect-name");
            return false;
        }
        if (this.regionManager.regionExists(name)) {
            this.sendMessage(sender, "region-exists");
            return false;
        }
        if (pos1 == null || pos2 == null) {
            this.sendMessage(sender, "two-positions-required");
            return false;
        }
        if (pos1.level != pos2.level) {
            this.sendMessage(sender, "positions-in-different-worlds");
            return false;
        }

        if (!this.regionSettings.hasAmountPermission(sender, this.regionManager.getPlayerRegionAmount((Player) sender, RegionGroup.CREATOR) + 1)) {
            this.sendMessage(sender, "too-much-regions");
            return false;
        }

        if (!this.regionSettings.hasSizePermission(sender, session.calculateRegionSize())) {
            this.sendMessage(sender, "too-large");
            return false;
        }

        if (this.regionManager.checkOverlap(pos1.asVector3f(), pos2.asVector3f(), pos1.level.getName())) {
            this.sendMessage(sender, "regions-overlap");
            return false;
        }

        this.regionManager.createRegion(name, sender.getName().toLowerCase(), pos1.asVector3f(), pos2.asVector3f(), pos1.level.getName());

        this.sendMessage(sender, "region-created", "@region", name);
        return true;
    }
}