package Sergey_Dertan.SRegionProtector.Command.Admin;

import Sergey_Dertan.SRegionProtector.Command.SRegionProtectorCommand;
import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;

public class SaveCommand extends SRegionProtectorCommand {

    private SRegionProtectorMain pl;

    public SaveCommand(String name, SRegionProtectorMain pl) {
        super(name);
        this.pl = pl;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] strings) {
        if (!this.testPermissionSilent(sender)) {
            this.messenger.sendMessage(sender, "save.permission");
            return false;
        }
        Server.getInstance().getScheduler().scheduleTask(this.pl, () -> pl.save(SRegionProtectorMain.SaveType.MANUAL, sender.getName()), true);
        return false;
    }
}
