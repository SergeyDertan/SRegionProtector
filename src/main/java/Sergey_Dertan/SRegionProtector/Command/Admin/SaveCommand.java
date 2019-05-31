package Sergey_Dertan.SRegionProtector.Command.Admin;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import cn.nukkit.command.CommandSender;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

public final class SaveCommand extends SRegionProtectorCommand {

    private final SRegionProtectorMain pl;

    public SaveCommand(SRegionProtectorMain pl) {
        super("rgsave", "save");
        this.pl = pl;

        this.setCommandParameters(new Object2ObjectArrayMap<>());
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "save.permission");
            return false;
        }
        this.pl.asyncSave(SRegionProtectorMain.SaveType.MANUAL, sender.getName());
        return false;
    }
}
