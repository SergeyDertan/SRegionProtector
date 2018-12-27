package Sergey_Dertan.SRegionProtector.Event;

import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.Player;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPotion;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.entity.passive.EntityWaterAnimal;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.block.LeavesDecayEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerDropItemEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.level.Position;

import java.util.Iterator;

public final class RegionEventsHandler implements Listener {

    //TODO check events performance

    private ChunkManager chunkManager;
    private boolean[] flagsStatus;
    private boolean[] needMessage;

    public RegionEventsHandler(ChunkManager chunkManager, boolean[] flagsStatus, boolean[] needMessage) {
        this.chunkManager = chunkManager;
        this.flagsStatus = flagsStatus;
        this.needMessage = needMessage;
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
        if (blockId != BlockID.TRAPDOOR && blockId != BlockID.STONE_BUTTON && blockId != BlockID.WOODEN_BUTTON) return;
        this.handleEvent(RegionFlags.FLAG_USE, e.getBlock(), e.getPlayer(), e);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void entityDamage(EntityDamageEvent e) {
        Entity ent = e.getEntity();
        if (!(ent instanceof Player)) return;
        if (!(e instanceof EntityDamageByEntityEvent)) {
            this.handleEvent(RegionFlags.FLAG_INVINCIBLE, ent, (Player) ent, e, false, false);
            return;
        }
        if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
            this.handleEvent(RegionFlags.FLAG_PVP, ent, (Player) ((EntityDamageByEntityEvent) e).getDamager(), e, false, false);
        } else if (((EntityDamageByEntityEvent) e).getDamager() instanceof EntityMob) {
            this.handleEvent(RegionFlags.FLAG_MOB_DAMAGE, e.getEntity(), (Player) e.getEntity(), e, false, false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void entitySpawn(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof EntityMob) && !(e.getEntity() instanceof EntityAnimal) && !(e.getEntity() instanceof EntityWaterAnimal)) return;
        this.handleEvent(RegionFlags.FLAG_MOB_SPAWN, e.getPosition(), null, e, false, false);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void leaveDecay(LeavesDecayEvent e) {
        this.handleEvent(RegionFlags.FLAG_LEAVES_DECAY, e.getBlock(), null, e);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void entityExplode(EntityExplodeEvent e) {
        this.handleEvent(RegionFlags.FLAG_INTERACT, e.getPosition(), null, e, false, false);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void projectileLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof EntityPotion)) return;
        Player source = null;
        if (e.getEntity().shootingEntity instanceof Player) source = (Player) e.getEntity().shootingEntity;
        this.handleEvent(RegionFlags.FLAG_POTION_LAUNCH, e.getEntity(), source, e, false, false);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerChat(PlayerChatEvent e) {
        this.handleEvent(RegionFlags.FLAG_SEND_CHAT, e.getPlayer(), e.getPlayer(), e, true, true);
        if (e.isCancelled()) return;
        Iterator<CommandSender> iterator = e.getRecipients().iterator();
        while (iterator.hasNext()) { //TODO check
            CommandSender var1 = iterator.next();
            if (!(var1 instanceof Player)) return;
            this.handleEvent(RegionFlags.FLAG_RECEIVE_CHAT, e.getPlayer(), e.getPlayer(), e, true, true);
            if (e.isCancelled()) iterator.remove();
            e.setCancelled(false);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerDropItem(PlayerDropItemEvent e) {
        this.handleEvent(RegionFlags.FLAG_ITEM_DROP, e.getPlayer(), e.getPlayer(), e, true, true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerMove(PlayerMoveEvent e) {
        this.handleEvent(RegionFlags.FLAG_MOVE, e.getTo(), e.getPlayer(), e, true, true);
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void entityRegainHealth(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        this.handleEvent(RegionFlags.FLAG_HEALTH_REGEN, e.getEntity(), (Player) e.getEntity(), e, true, true);
    }

    private void handleEvent(int[] flags, Position pos, Player player, Event ev, boolean mustBeMember, boolean checkPerm) {
        if (checkPerm && (player != null && player.hasPermission("sregionprotector.admin"))) return;
        Chunk chunk = this.chunkManager.getChunk((long) pos.x >> 4, (long) pos.z >> 4, pos.level.getName(), false, false);
        if (chunk == null) return;
        for (Region region : chunk.getRegions()) {
            if ((mustBeMember && (player != null && region.isLivesIn(player.getName()))) || !region.isVectorInside(pos)) continue;
            for (int flag : flags) {
                if (!this.flagsStatus[flag] || !region.getFlagState(flag)) continue;
                ev.setCancelled(true);
                if (player != null && this.needMessage[flag]) Messenger.getInstance().sendMessage(player, "region.protected");
                return;
            }
        }
    }

    private void handleEvent(int flag, Position pos, Player player, Event ev, boolean mustBeMember, boolean checkPerm) {
        this.handleEvent(new int[]{flag}, pos, player, ev, mustBeMember, checkPerm);
    }

    private void handleEvent(int flag, Position pos, Player player, Event ev) {
        this.handleEvent(new int[]{flag}, pos, player, ev, true, true);
    }
}