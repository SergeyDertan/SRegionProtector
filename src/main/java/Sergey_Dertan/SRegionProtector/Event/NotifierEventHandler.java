package Sergey_Dertan.SRegionProtector.Event;

import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Utils.Pair;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.network.protocol.SetLocalPlayerAsInitializedPacket;
import cn.nukkit.utils.TextFormat;

@SuppressWarnings("unused")
public final class NotifierEventHandler implements Listener {

    private final Pair<String, String> updateInfo;

    public NotifierEventHandler(String version, String description) {
        version = TextFormat.GREEN + Messenger.getInstance().getMessage("loading.init.update-available", "@ver", version);
        description = TextFormat.GREEN + Messenger.getInstance().getMessage("loading.init.update-description", "@description", description);
        this.updateInfo = new Pair<>(version, description);
    }

    @EventHandler
    public void playerJoin(DataPacketReceiveEvent e) { //message wont be displayed while using PlayerJoinEvent
        if (e.getPacket() instanceof SetLocalPlayerAsInitializedPacket && e.getPlayer().hasPermission("sregionprotector.update-notify")) {
            e.getPlayer().sendMessage(this.updateInfo.key);
            e.getPlayer().sendMessage(this.updateInfo.value);
        }
    }
}
