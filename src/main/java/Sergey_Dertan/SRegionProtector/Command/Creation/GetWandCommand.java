package Sergey_Dertan.SRegionProtector.Command.Creation;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;

import java.util.Map;

public final class GetWandCommand extends SRegionProtectorCommand {

    public GetWandCommand(String name, Map<String, String> messages) {
        super(name, messages);
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if (!this.testPermissionSilent(sender)) {
            Messenger.getInstance().sendMessage(sender, "command.wand.permission");
            return false;
        }
        if (!(sender instanceof Player)) {
            Messenger.getInstance().sendMessage(sender, "command.wand.in-game");
            return false;
        }
        if (!this.testPermission(sender)) return false;
        ((Player) sender).getInventory().addItem(Item.get(ItemID.WOODEN_AXE, 0, 1));
        Messenger.getInstance().sendMessage(sender, "command.wand.wand-given");
        return true;
    }
}