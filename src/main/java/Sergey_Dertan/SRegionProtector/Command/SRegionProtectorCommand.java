package Sergey_Dertan.SRegionProtector.Command;

import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import cn.nukkit.command.Command;

public abstract class SRegionProtectorCommand extends Command {

    protected final Messenger messenger;

    public SRegionProtectorCommand(String name) {
        super(name);
        this.messenger = Messenger.getInstance();
    }
}
