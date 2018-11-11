package Sergey_Dertan.SRegionProtector.Event;

import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPotion;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplodeEvent;
import cn.nukkit.event.entity.ProjectileLaunchEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Position;

public final class RegionEventsHandler implements Listener {

    //TODO check events performance

    private ChunkManager chunkManager;
    private boolean[] flagsStatus;

    public RegionEventsHandler(ChunkManager chunkManager, boolean[] flagsStatus) {
        this.chunkManager = chunkManager;
        this.flagsStatus = flagsStatus;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent e) {
        this.handleEvent(RegionFlags.FLAG_BUILD, e.getBlock(), e.getPlayer(), e);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockPlace(BlockPlaceEvent e) {
        this.handleEvent(RegionFlags.FLAG_BUILD, e.getBlock(), e.getPlayer(), e);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerInteract(PlayerInteractEvent e) {
        this.handleEvent(RegionFlags.FLAG_INTERACT, e.getBlock(), e.getPlayer(), e);
        if (e.isCancelled()) return;
        int blockId = e.getBlock().getId();
        if (blockId == BlockID.TRAPDOOR || blockId == BlockID.STONE_BUTTON || blockId == BlockID.WOODEN_BUTTON) this.handleEvent(RegionFlags.FLAG_USE, e.getBlock(), e.getPlayer(), e);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void entityDamage(EntityDamageEvent e) {
        Entity ent = e.getEntity();
        if (!(ent instanceof Player)) return;
        this.handleEvent(RegionFlags.FLAG_INVINCIBLE, ent, (Player) ent, e, false, false);
        if (!(e instanceof EntityDamageByEntityEvent)) return;
        if (!(((EntityDamageByEntityEvent) e).getDamager() instanceof Player)) return;
        this.handleEvent(RegionFlags.FLAG_PVP, ent, (Player) ((EntityDamageByEntityEvent) e).getDamager(), e, false, false);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void entityExplode(EntityExplodeEvent e) {
        this.handleEvent(RegionFlags.FLAG_INTERACT, e.getPosition(), null, e, true, false);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void projectileLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof EntityPotion)) return;
        Player source = null;
        if (e.getEntity().shootingEntity instanceof Player) source = (Player) e.getEntity().shootingEntity;
        this.handleEvent(RegionFlags.FLAG_POTION_LAUNCH, e.getEntity(), source, e, false, false);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerMove(PlayerMoveEvent e) {
        this.handleEvent(RegionFlags.FLAG_MOVE, e.getTo(), e.getPlayer(), e, true, true);
    }

    private void handleEvent(int[] flags, Position pos, Player player, Event ev, boolean mustBeMember, boolean checkPerm) {
        if (checkPerm && (player != null && player.hasPermission("sregionprotector.admin"))) return;
        Chunk chunk = this.chunkManager.getChunk((long) pos.x >> 4, (long) pos.z >> 4, pos.level.getName(), false, false);
        if (chunk == null) return;
        for (Region region : chunk.getRegions()) {
            if ((mustBeMember && (player != null && region.isLives(player.getName().toLowerCase()))) || !region.isVectorInside(pos)) continue;
            for (int flag : flags) {
                if (!this.flagsStatus[flag] || !region.getFlagList().getFlagState(flag)) continue;
                ev.setCancelled(true);
                if (player != null) player.sendMessage("Some region here"); //TODO messages
                return;
            }
        }
    }

    private void handleEvent(int flag, Position pos, Player player, Event ev, boolean mustBeMember, boolean checkPerm) {
        this.handleEvent(new int[]{flag}, pos, player, ev, mustBeMember, checkPerm);
    }

    private void handleEvent(int flag, Position pos, Player player, Event ev, boolean mustBeMember) {
        this.handleEvent(new int[]{flag}, pos, player, ev, mustBeMember, true);
    }

    private void handleEvent(int flag, Position pos, Player player, Event ev) {
        this.handleEvent(new int[]{flag}, pos, player, ev, true, true);
    }
}