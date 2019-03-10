package Sergey_Dertan.SRegionProtector.Event;

import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityPotion;
import cn.nukkit.entity.mob.EntityMob;
import cn.nukkit.entity.passive.EntityAnimal;
import cn.nukkit.entity.passive.EntityWaterAnimal;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.Event;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.*;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.player.*;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.MainLogger;

import java.util.Iterator;

public final class RegionEventsHandler implements Listener {

    //TODO check events performance

    private final ChunkManager chunkManager;
    private final boolean[] flagsStatus; //check if flag enabled
    private final boolean[] needMessage; //check if flag requires a message
    private final boolean prioritySystem;

    public RegionEventsHandler(ChunkManager chunkManager, boolean[] flagsStatus, boolean[] needMessage, boolean prioritySystem) {
        this.chunkManager = chunkManager;
        this.flagsStatus = flagsStatus;
        this.needMessage = needMessage;
        this.prioritySystem = prioritySystem;
    }

    //build flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent e) {
        this.handleEvent(RegionFlags.FLAG_BUILD, e.getBlock(), e.getPlayer(), e);
    }

    //build flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockPlace(BlockPlaceEvent e) {
        this.handleEvent(RegionFlags.FLAG_BUILD, e.getBlock(), e.getPlayer(), e);
    }

    //interact, use, crops destroy & chest access flags
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerInteract(PlayerInteractEvent e) {
        this.handleEvent(RegionFlags.FLAG_INTERACT, e.getBlock(), e.getPlayer(), e);
        if (e.isCancelled()) return;
        if (e.getItem().getId() == ItemID.FLINT_AND_STEEL) {
            this.handleEvent(RegionFlags.FLAG_LIGHTER, e.getBlock(), e.getPlayer(), e);
            return;
        }
        Block block = e.getBlock();
        if (block instanceof BlockChest || block instanceof BlockEnderChest) {
            this.handleEvent(RegionFlags.FLAG_CHEST_ACCESS, block, e.getPlayer(), e);
            return;
        }
        if (block instanceof BlockFarmland) {
            this.handleEvent(RegionFlags.FLAG_CROPS_DESTROY, e.getBlock(), e.getPlayer(), e);
            return;
        }
        if (!(block instanceof BlockDoor) && !(block instanceof BlockTrapdoor) && !(block instanceof BlockButton) && !(block instanceof BlockFurnace) && !(block instanceof BlockBeacon) && !(block instanceof BlockHopper) && !(block instanceof BlockDispenser)) {
            return;
        }
        this.handleEvent(RegionFlags.FLAG_USE, e.getBlock(), e.getPlayer(), e);
    }

    //pvp, mob damage & invincible flags
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

    //mob spawn flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void entitySpawn(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof EntityMob) && !(e.getEntity() instanceof EntityAnimal) && !(e.getEntity() instanceof EntityWaterAnimal))
            return;
        this.handleEvent(RegionFlags.FLAG_MOB_SPAWN, e.getPosition(), null, e, false, false);
    }

    //lightning strike flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void lightningStrike(LightningStrikeEvent e) {
        if (e.getLightning() instanceof EntityLightning) this.handleEvent(RegionFlags.FLAG_LIGHTNING_STRIKE, ((Position) e.getLightning()), e);
    }

    //fire flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockIgnite(BlockIgniteEvent e) {
        this.handleEvent(RegionFlags.FLAG_FIRE, e.getBlock(), e.getEntity() instanceof Player ? ((Player) e.getEntity()) : null, e, false, false);
    }

    //fire flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockBurn(BlockBurnEvent e) {
        this.handleEvent(RegionFlags.FLAG_FIRE, e.getBlock(), e);
    }

    //leaves decay flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void leavesDecay(LeavesDecayEvent e) {
        this.handleEvent(RegionFlags.FLAG_LEAVES_DECAY, e.getBlock(), e);
    }

    //explode (creeper & tnt explode) & explode block break flags
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void entityExplode(EntityExplodeEvent e) {
        this.handleEvent(RegionFlags.FLAG_EXPLODE, e.getPosition(), null, e, false, false);
        if (e.isCancelled()) return;
        Iterator<Block> it = e.getBlockList().iterator();
        while (it.hasNext()) {
            this.handleEvent(RegionFlags.FLAG_EXPLODE_BLOCK_BREAK, it.next(), e);
            if (e.isCancelled()) {
                e.setCancelled(false);
                it.remove();
            }
        }
    }

    //potion launch flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void projectileLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof EntityPotion)) return;
        Player source = null;
        if (e.getEntity().shootingEntity instanceof Player) source = (Player) e.getEntity().shootingEntity;
        this.handleEvent(RegionFlags.FLAG_POTION_LAUNCH, e.getEntity(), source, e, false, false);
    }

    //send chat & receive chat flags
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerChat(PlayerChatEvent e) {
        this.handleEvent(RegionFlags.FLAG_SEND_CHAT, e.getPlayer(), e.getPlayer(), e, true, true);
        if (e.isCancelled()) return;
        Iterator<CommandSender> iterator = e.getRecipients().iterator();
        while (iterator.hasNext()) {
            CommandSender var1 = iterator.next();
            if (!(var1 instanceof Player)) return;
            this.handleEvent(RegionFlags.FLAG_RECEIVE_CHAT, (Position) var1, (Player) var1, e, true, true);
            if (e.isCancelled()) {
                iterator.remove();
                e.setCancelled(false);
            }
        }
    }

    //item drop flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerDropItem(PlayerDropItemEvent e) { //item drop
        this.handleEvent(RegionFlags.FLAG_ITEM_DROP, e.getPlayer(), e.getPlayer(), e, true, true);
    }

    //move flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerMove(PlayerMoveEvent e) { //player move
        this.handleEvent(RegionFlags.FLAG_MOVE, e.getTo(), e.getPlayer(), e, true, true);
    }

    //health regen flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void entityRegainHealth(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        this.handleEvent(RegionFlags.FLAG_HEALTH_REGEN, e.getEntity(), (Player) e.getEntity(), e, true, true);
    }

    //redstone flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void redstoneUpdate(RedstoneUpdateEvent e) {
        this.handleEvent(RegionFlags.FLAG_REDSTONE, e.getBlock(), e);
    }

    //ender pearl flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerTeleport(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        this.handleEvent(RegionFlags.FLAG_ENDER_PEARL, e.getTo(), e.getPlayer(), e, true, true);
    }

    //liquid flow flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void liquidFlow(LiquidFlowEvent e) {
        Block block = e.getSource();
        if (!(block instanceof BlockLava) && !(block instanceof BlockWater)) return;
        this.handleEvent(RegionFlags.FLAG_LIQUID_FLOW, e.getTo(), null, e, false, false, e.getSource());
    }

    //sleep flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerBedEnter(PlayerBedEnterEvent e) {
        this.handleEvent(RegionFlags.FLAG_SLEEP, e.getBed(), e.getPlayer(), e);
    }

    private void handleEvent(int flag, Position pos, Player player, Event ev, boolean mustBeMember, boolean checkPerm, Vector3 liquidSource) {
        if (!this.flagsStatus[flag]) return;
        if (checkPerm && (player != null && player.hasPermission("sregionprotector.admin"))) return;
        Chunk chunk = this.chunkManager.getChunk((long) pos.x >> 4, (long) pos.z >> 4, pos.level.getName(), false, false);
        if (chunk == null) return;
        for (Region region : chunk.getRegions()) {
            if (!region.isVectorInside(pos) || (liquidSource != null && region.isVectorInside(liquidSource)) || (mustBeMember && (player != null && region.isLivesIn(player.getName())))) {
                continue;
            }
            MainLogger.getLogger().info(region.name + " " + region.getPriority());
            if (!region.getFlagState(flag)) if (this.prioritySystem) break;

            ev.setCancelled();
            if (player != null && this.needMessage[flag]) Messenger.getInstance().sendMessage(player, "region.protected");
            break;
        }
    }

    private void handleEvent(int flag, Position pos, Player player, Event ev, boolean mustBeMember, boolean checkPerm) {
        this.handleEvent(flag, pos, player, ev, mustBeMember, checkPerm, null);
    }

    private void handleEvent(int flag, Position pos, Player player, Event ev) {
        this.handleEvent(flag, pos, player, ev, true, true, null);
    }

    private void handleEvent(int flag, Position pos, Event ev) {
        this.handleEvent(flag, pos, null, ev);
    }
}
