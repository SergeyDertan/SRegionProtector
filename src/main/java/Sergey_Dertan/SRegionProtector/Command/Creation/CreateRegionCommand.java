package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Region.RegionGroup;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import Sergey_Dertan.SRegionProtector.Region.Selector.SelectorSession;
import Sergey_Dertan.SRegionProtector.Settings.RegionSettings;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.Position;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public final class CreateRegionCommand extends SRegionProtectorCommand {

    private final RegionSelector selector;
    private final RegionManager regionManager;
    private final RegionSettings regionSettings;

    public CreateRegionCommand(RegionSelector selector, RegionManager regionManager, RegionSettings regionSettings) {
        super("rgcreate", "create");
        this.selector = selector;
        this.regionManager = regionManager;
        this.regionSettings = regionSettings;

        Map<String, CommandParameter[]> parameters = new Object2ObjectArrayMap<>();
        parameters.put("rgname", new CommandParameter[]{new CommandParameter("region", CommandParamType.STRING, false)});
        this.setCommandParameters(parameters);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "command.create.permission");
            return false;
        }
        if (!(sender instanceof Player)) {
            this.messenger.sendMessage(sender, "command.create.in-game");
            return false;
        }

        if (args.length < 1) {
            this.messenger.sendMessage(sender, "command.create.usage");
            return false;
        }

        SelectorSession session = this.selector.getSession((Player) sender);
        Position pos1 = session.pos1;
        Position pos2 = session.pos2;
        String name = args[0];

        if (name.replace(" ", "").isEmpty()) {
            this.messenger.sendMessage(sender, "command.create.usage");
            return false;
        }
        if (name.length() < this.regionSettings.minRegionNameLength || name.length() > regionSettings.maxRegionNameLength || !name.matches("[a-zA-Z0-9]*")) {
            this.messenger.sendMessage(sender, "command.create.incorrect-name");
            return false;
        }
        if (this.regionManager.regionExists(name)) {
            this.messenger.sendMessage(sender, "command.create.region-exists");
            return false;
        }
        if (pos1 == null || pos2 == null) {
            this.messenger.sendMessage(sender, "command.create.two-positions-required");
            return false;
        }
        if (pos1.level != pos2.level) {
            this.messenger.sendMessage(sender, "command.create.positions-in-different-worlds");
            return false;
        }

        if (!this.regionSettings.hasAmountPermission(sender, this.regionManager.getPlayerRegionAmount((Player) sender, RegionGroup.CREATOR) + 1)) {
            this.messenger.sendMessage(sender, "command.create.too-many");
            return false;
        }

        if (!this.regionSettings.hasSizePermission(sender, session.calculateRegionSize())) {
            this.messenger.sendMessage(sender, "command.create.too-large");
            return false;
        }

        if (this.regionManager.checkOverlap(pos1, pos2, pos1.level.getName(), sender.getName(), true)) {
            this.messenger.sendMessage(sender, "command.create.regions-overlap");
            return false;
        }
        if (this.regionManager.createRegion(name, sender.getName(), pos1, pos2, pos1.level) == null) {
            this.messenger.sendMessage(sender, "command.create.region-exists");
            return false;
        }
        this.messenger.sendMessage(sender, "command.create.region-created", "@region", name);
        return true;
    }
}
