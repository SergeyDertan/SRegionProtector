package Sergey_Dertan.SRegionProtector.Command;

import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import cn.nukkit.command.Command;

@SuppressWarnings("WeakerAccess")
public abstract class SRegionProtectorCommand extends Command {

    protected final Messenger messenger;

    public SRegionProtectorCommand(String name, String msg, String perm) {
        super(name);
        this.messenger = Messenger.getInstance();

        this.setDescription(this.messenger.getMessage("command." + msg + ".description"));
        this.setPermission("sregionprotector.command." + perm);
    }

    public SRegionProtectorCommand(String nmp) {
        this(nmp, nmp, nmp);
    }

    public SRegionProtectorCommand(String name, String mp) {
        this(name, mp, mp);
    }
}
