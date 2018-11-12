package Sergey_Dertan.SRegionProtector.Event;

import Sergey_Dertan.SRegionProtector.Region.Selector.RegionSelector;
import Sergey_Dertan.SRegionProtector.Region.Selector.SelectorSession;
import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemAxeWood;
import cn.nukkit.level.Position;

public final class SelectorEventsHandler implements Listener {

    private RegionSelector regionSelector;

    public SelectorEventsHandler(RegionSelector selector) {
        this.regionSelector = selector;
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent e) {
        this.regionSelector.removeSession(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void playerInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Item item = e.getItem();
        Block block = e.getBlock();
        if (block instanceof BlockAir || !(item instanceof ItemAxeWood)) return;
        SelectorSession session = this.regionSelector.getSession(player);
        if (session.pos1 == null) {
            session.pos1 = Position.fromObject(block, block.level);
            player.sendMessage("pos1 set"); //TODO messages
            return;
        }

        if (session.pos2 == null) {
            session.pos2 = Position.fromObject(block, block.level);
            player.sendMessage("pos2 set"); //TODO messages
            return;
        }

        session.pos1 = Position.fromObject(block, block.level);
        session.pos2 = null;
        player.sendMessage("pos1 set"); //TODO messages
    }
}
