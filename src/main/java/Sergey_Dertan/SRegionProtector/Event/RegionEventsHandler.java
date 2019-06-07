package Sergey_Dertan.SRegionProtector.Event;

import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Utils.Pair;
import Sergey_Dertan.SRegionProtector.Utils.Tags;
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
import cn.nukkit.event.level.ChunkUnloadEvent;
import cn.nukkit.event.level.LevelLoadEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.redstone.RedstoneUpdateEvent;
import cn.nukkit.event.weather.LightningStrikeEvent;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.EnumLevel;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.AngryVillagerParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.DataPacket;
import it.unimi.dsi.fastutil.objects.Object2BooleanArrayMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;

import java.util.*;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class RegionEventsHandler implements Listener {

    //TODO check events performance

    private final ChunkManager chunkManager;
    private final boolean[] flagsStatus; //check if flag enabled
    private final boolean[] needMessage; //check if flag requires a message
    private final boolean prioritySystem;
    private final boolean showParticle;

    private final Object2BooleanMap<Class> isMonster;
    private final Class monster; //mobplugin

    private final Pair<Vector3, Integer>[] portalBlocks;

    private final Messenger.MessageType protectedMessageType;

    @SuppressWarnings("unchecked")
    public RegionEventsHandler(ChunkManager chunkManager, boolean[] flagsStatus, boolean[] needMessage, boolean prioritySystem, Messenger.MessageType protectedMessageType, boolean particle) {
        this.chunkManager = chunkManager;
        this.flagsStatus = flagsStatus;
        this.needMessage = needMessage;
        this.prioritySystem = prioritySystem;
        this.showParticle = particle;

        this.protectedMessageType = protectedMessageType;

        this.isMonster = new Object2BooleanArrayMap<>();

        Class monster = null;
        try {
            monster = Class.forName("nukkitcoders.mobplugin.entities.monster.Monster");
        } catch (ClassNotFoundException ignore) {
        }
        this.monster = monster;

        this.portalBlocks = this.netherPortalBlocks();
    }

    /**
     * @see BlockNetherPortal#spawnPortal(Position)
     */
    private Pair[] netherPortalBlocks() {
        Map<Vector3, Integer> blocks = new HashMap<>();

        blocks.put(new Vector3(1), BlockID.OBSIDIAN);
        blocks.put(new Vector3(2), BlockID.OBSIDIAN);

        //z=1
        blocks.put(new Vector3(0, 0, 1), BlockID.OBSIDIAN);
        blocks.put(new Vector3(1, 0, 1), BlockID.OBSIDIAN);
        blocks.put(new Vector3(2, 0, 1), BlockID.OBSIDIAN);
        blocks.put(new Vector3(3, 0, 1), BlockID.OBSIDIAN);
        //z=2
        blocks.put(new Vector3(1, 0, 2), BlockID.OBSIDIAN);
        blocks.put(new Vector3(2, 0, 2), BlockID.OBSIDIAN);
        //z=1
        //y=1
        blocks.put(new Vector3(0, 1, 1), BlockID.OBSIDIAN);
        blocks.put(new Vector3(1, 1, 1), BlockID.NETHER_PORTAL);
        blocks.put(new Vector3(2, 1, 1), BlockID.NETHER_PORTAL);
        blocks.put(new Vector3(3, 1, 1), BlockID.OBSIDIAN);
        //y=2
        //z=1
        blocks.put(new Vector3(0, 2, 1), BlockID.OBSIDIAN);
        blocks.put(new Vector3(1, 2, 1), BlockID.NETHER_PORTAL);
        blocks.put(new Vector3(2, 2, 1), BlockID.NETHER_PORTAL);
        blocks.put(new Vector3(3, 2, 1), BlockID.OBSIDIAN);
        //y=3
        blocks.put(new Vector3(0, 3, 1), BlockID.OBSIDIAN);
        blocks.put(new Vector3(1, 3, 1), BlockID.NETHER_PORTAL);
        blocks.put(new Vector3(2, 3, 1), BlockID.NETHER_PORTAL);
        blocks.put(new Vector3(3, 3, 1), BlockID.OBSIDIAN);
        //y=4
        blocks.put(new Vector3(0, 4, 1), BlockID.OBSIDIAN);
        blocks.put(new Vector3(1, 4, 1), BlockID.OBSIDIAN);
        blocks.put(new Vector3(2, 4, 1), BlockID.OBSIDIAN);
        blocks.put(new Vector3(3, 4, 1), BlockID.OBSIDIAN);

        for (int x = -1; x < 4; x++) {
            for (int y = 1; y < 4; y++) {
                for (int z = -1; z < 3; z++) {
                    blocks.putIfAbsent(new Vector3(x, y, z), BlockID.AIR);
                }
            }
        }
        List<Pair<Vector3, Integer>> blockss = new ArrayList<>();
        blocks.forEach((k, v) -> blockss.add(new Pair<>(k, v)));
        return blockss.toArray(new Pair[0]);
    }

    //break & minefarm flags
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockBreak(BlockBreakEvent e) {
        if (e.getBlock().getClass().getSimpleName().startsWith(Tags.BLOCK_ORE)) { //too much instanceof
            this.handleEvent(RegionFlags.FLAG_MINEFARM, e.getBlock(), e.getPlayer(), e, false, false);
            if (e.isCancelled()) {
                e.setCancelled(false);
                return;
            }
        }
        this.handleEvent(RegionFlags.FLAG_BREAK, e.getBlock(), e.getPlayer(), e);
    }

    //place flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void blockPlace(BlockPlaceEvent e) {
        this.handleEvent(RegionFlags.FLAG_PLACE, e.getBlock(), e.getPlayer(), e);
    }

    //interact, lighter, use, crops destroy, chest access & smart doors flags
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerInteract(PlayerInteractEvent e) {
        Block block = e.getBlock();
        this.handleEvent(RegionFlags.FLAG_INTERACT, block, e.getPlayer(), e);
        if (e.isCancelled()) return;
        if (block instanceof BlockDoor || block instanceof BlockTrapdoorIron) {
            if (this.canInteractWith(RegionFlags.FLAG_SMART_DOORS, block, e.getPlayer())) {
                if (block instanceof BlockTrapdoorIron) {
                    block.setDamage(block.getDamage() ^ 0x08);
                    block.level.setBlock(block, block, true);
                    block.level.addSound(block, ((BlockTrapdoorIron) block).isOpen() ? Sound.RANDOM_DOOR_OPEN : Sound.RANDOM_DOOR_CLOSE);
                    return;
                }
                BlockDoor door = (BlockDoor) block;

                int damage = door.getDamage();
                boolean isUp = (damage & 8) > 0;
                int up;
                if (isUp) {
                    up = damage;
                } else {
                    up = door.up().getDamage();
                }
                boolean isRight = (up & 1) > 0;

                BlockFace second;
                if (isUp) {
                    second = ((BlockDoor) door.down()).getBlockFace();
                } else {
                    second = door.getBlockFace();
                }

                if (isRight) {
                    switch (second) {
                        case EAST:
                            second = BlockFace.WEST;
                            break;
                        case WEST:
                            second = BlockFace.EAST;
                            break;
                        case NORTH:
                            second = BlockFace.SOUTH;
                            break;
                        case SOUTH:
                            second = BlockFace.NORTH;
                            break;
                    }
                }

                BlockDoor pair = door.getSide(second) instanceof BlockDoor ? ((BlockDoor) door.getSide(second)) : null;

                if (door.toggle(e.getPlayer())) {
                    door.level.addSound(door, door.isOpen() ? Sound.RANDOM_DOOR_OPEN : Sound.RANDOM_DOOR_CLOSE);
                }

                if (pair != null && !pair.isTop(pair.getDamage())) {
                    pair = ((BlockDoor) pair.up());
                }
                if (pair != null && ((pair.getDamage() & 1) > 0 == !isRight)) {
                    if (pair.toggle(e.getPlayer())) {
                        pair.level.addSound(pair, pair.isOpen() ? Sound.RANDOM_DOOR_OPEN : Sound.RANDOM_DOOR_CLOSE);
                    }
                }
                e.setCancelled();
                return;
            }
        }

        if (e.getItem() != null && e.getItem().getId() == ItemID.FLINT_AND_STEEL) {
            this.handleEvent(RegionFlags.FLAG_LIGHTER, block, e.getPlayer(), e, false, false);
            return;
        }
        if (block instanceof BlockChest || block instanceof BlockEnderChest) {
            this.handleEvent(RegionFlags.FLAG_CHEST_ACCESS, block, e.getPlayer(), e);
            return;
        }
        if (block instanceof BlockFarmland) {
            this.handleEvent(RegionFlags.FLAG_CROPS_DESTROY, block, e.getPlayer(), e);
            return;
        }
        if (block instanceof BlockDoor || block instanceof BlockTrapdoor || block instanceof BlockButton || block instanceof BlockFurnace || block instanceof BlockBeacon || block instanceof BlockHopper || block instanceof BlockDispenser) {
            this.handleEvent(RegionFlags.FLAG_USE, block, e.getPlayer(), e);
        }
    }

    //pvp, mob damage, lightning strike & invincible flags
    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void entityDamage(EntityDamageEvent e) {
        Entity ent = e.getEntity();
        if (!(ent instanceof Player)) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            this.handleEvent(RegionFlags.FLAG_FALL_DAMAGE, ent, ((Player) ent), e, false, false);
            if (e.isCancelled()) return;
        }
        if (!(e instanceof EntityDamageByEntityEvent)) {
            this.handleEvent(RegionFlags.FLAG_INVINCIBLE, ent, (Player) ent, e, false, false);
            return;
        }

        if (((EntityDamageByEntityEvent) e).getDamager() instanceof Player) {
            this.handleEvent(RegionFlags.FLAG_PVP, ent, (Player) ((EntityDamageByEntityEvent) e).getDamager(), e, false, false);
        } else if (
                ((EntityDamageByEntityEvent) e).getDamager() instanceof EntityMob ||
                        (this.monster != null && this.isMonster.computeIfAbsent(((EntityDamageByEntityEvent) e).getDamager().getClass(), (s) -> this.monster.isAssignableFrom(((EntityDamageByEntityEvent) e).getDamager().getClass())))
        ) {
            this.handleEvent(RegionFlags.FLAG_MOB_DAMAGE, e.getEntity(), (Player) e.getEntity(), e, false, false);
        } else if (((EntityDamageByEntityEvent) e).getDamager() instanceof EntityLightning) {
            this.handleEvent(RegionFlags.FLAG_LIGHTNING_STRIKE, e.getEntity(), e);
        }
    }

    //mob spawn & lightning strike flags
    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.HIGH)
    public void entitySpawn(EntitySpawnEvent e) {
        EmptyEvent ev = new EmptyEvent();
        if (e.getEntity() instanceof EntityLightning) {
            this.handleEvent(RegionFlags.FLAG_LIGHTNING_STRIKE, e.getPosition(), ev);
            if (ev.isCancelled()) e.getEntity().close();
            return;
        }
        if (e.getEntity() instanceof EntityMob ||
                e.getEntity() instanceof EntityAnimal ||
                e.getEntity() instanceof EntityWaterAnimal ||
                (this.monster != null && this.isMonster.computeIfAbsent(e.getEntity().getClass(), (s) -> this.monster.isAssignableFrom(e.getEntity().getClass())))
        ) {
            this.handleEvent(RegionFlags.FLAG_MOB_SPAWN, e.getPosition(), ev);
            if (ev.isCancelled()) e.getEntity().close();
        }
    }

    //lightning strike flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void lightningStrike(LightningStrikeEvent e) {
        if (e.getLightning() instanceof EntityLightning)
            this.handleEvent(RegionFlags.FLAG_LIGHTNING_STRIKE, ((Position) e.getLightning()), e);
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
        this.handleEvent(RegionFlags.FLAG_EXPLODE, e.getPosition(), e);
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
        this.handleEvent(RegionFlags.FLAG_SEND_CHAT, e.getPlayer(), e.getPlayer(), e);
        if (e.isCancelled()) return;
        Iterator<CommandSender> iterator = e.getRecipients().iterator();
        while (iterator.hasNext()) {
            CommandSender var1 = iterator.next();
            if (!(var1 instanceof Player)) return;
            this.handleEvent(RegionFlags.FLAG_RECEIVE_CHAT, (Position) var1, (Player) var1, e);
            if (e.isCancelled()) {
                iterator.remove();
                e.setCancelled(false);
            }
        }
    }

    //item drop flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerDropItem(PlayerDropItemEvent e) {
        this.handleEvent(RegionFlags.FLAG_ITEM_DROP, e.getPlayer(), e.getPlayer(), e);
    }

    //move flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerMove(PlayerMoveEvent e) {
        this.handleEvent(RegionFlags.FLAG_MOVE, e.getTo(), e.getPlayer(), e);
    }

    //health regen flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void entityRegainHealth(EntityRegainHealthEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        this.handleEvent(RegionFlags.FLAG_HEALTH_REGEN, e.getEntity(), (Player) e.getEntity(), e);
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
        this.handleEvent(RegionFlags.FLAG_ENDER_PEARL, e.getTo(), e.getPlayer(), e);
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

    //chunk loader flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void chunkUnload(ChunkUnloadEvent e) {
        if (!this.flagsStatus[RegionFlags.FLAG_CHUNK_LOADER]) return;
        Chunk chunk = this.chunkManager.getChunk(e.getChunk().getX(), e.getChunk().getZ(), e.getLevel().getName(), false, false);
        if (chunk == null) return;
        for (Region region : chunk.getRegions()) {
            if (!region.getFlagState(RegionFlags.FLAG_CHUNK_LOADER)) continue;
            e.setCancelled();
            break;
        }
    }

    //chunk loader flag
    @EventHandler(priority = EventPriority.HIGH)
    public void levelLoad(LevelLoadEvent e) {
        if (!this.flagsStatus[RegionFlags.FLAG_CHUNK_LOADER]) return;
        Collection<Chunk> chunks = this.chunkManager.getLevelChunks(e.getLevel().getName());
        for (Chunk chunk : chunks) {
            for (Region region : chunk.getRegions()) {
                if (!region.getFlagState(RegionFlags.FLAG_CHUNK_LOADER)) continue;
                e.getLevel().loadChunk((int) chunk.x, (int) chunk.z);
            }
        }
    }

    //frame item drop flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void itemFrameDropItem(ItemFrameDropItemEvent e) {
        this.handleEvent(RegionFlags.FLAG_FRAME_ITEM_DROP, e.getBlock(), e.getPlayer(), e);
    }

    //prevent nether portal from spawning in region
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void entityPortalEnter(EntityPortalEnterEvent e) {
        if (!this.flagsStatus[RegionFlags.FLAG_NETHER_PORTAL]) return;
        if (e.getPortalType() != EntityPortalEnterEvent.PortalType.NETHER) return;
        Position portal = EnumLevel.moveToNether(e.getEntity()).floor();
        if (portal == null) return;

        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                int chunkX = (portal.getFloorX() >> 4) + x;
                int chunkZ = (portal.getFloorZ() >> 4) + z;
                FullChunk chunk = portal.level.getChunk(chunkX, chunkZ, true);
                if (chunk == null || !(chunk.isGenerated() || chunk.isPopulated())) {
                    portal.level.generateChunk(chunkX, chunkZ, true);
                }
            }
        }

        for (Pair<Vector3, Integer> block : this.portalBlocks) {
            Vector3 pos = portal.add(block.key).floor();
            if (portal.level.getBlockIdAt((int) pos.x, (int) pos.y, (int) pos.z) != block.value) {
                Region region = this.chunkManager.getRegion(pos, portal.level.getName());
                if (region != null && region.getFlagState(RegionFlags.FLAG_NETHER_PORTAL)) {
                    e.setCancelled();
                    if (e.getEntity() instanceof Player) {
                        Messenger.getInstance().sendMessage(((Player) e.getEntity()), "region.protected." + RegionFlags.getFlagName(RegionFlags.FLAG_NETHER_PORTAL));
                    }
                    break;
                }
            }
        }
    }

    //bucket empty flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerBucketEmpty(PlayerBucketEmptyEvent e) {
        this.handleEvent(RegionFlags.FLAG_BUCKET_EMPTY, e.getBlockClicked(), e.getPlayer(), e);
    }

    //bucket fill flag
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void playerBucketFill(PlayerBucketFillEvent e) {
        this.handleEvent(RegionFlags.FLAG_BUCKET_FILL, e.getBlockClicked(), e.getPlayer(), e);
    }

    private boolean canInteractWith(int flag, Position pos, Player player) {
        if (!this.flagsStatus[flag]) return false;
        Chunk chunk = this.chunkManager.getChunk((long) pos.x, (long) pos.z, pos.level.getName(), true, false);
        if (chunk == null) return false;
        for (Region region : chunk.getRegions()) {
            if (!region.isVectorInside(pos)) continue;
            if (!region.getFlagState(flag)) {
                if (this.prioritySystem) {
                    return false;
                } else {
                    continue;
                }
            }
            return region.isLivesIn(player.getName()) || player.hasPermission("sregionprotector.admin");
        }
        return false;
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
            if (!region.getFlagState(flag)) {
                if (this.prioritySystem) {
                    break;
                } else {
                    continue;
                }
            }

            if (this.showParticle && player != null) {
                Vector3 pPos = pos;
                if (pos.x % 1 + pos.y % 1 + pos.z % 1 == 0) {
                    pPos = new Vector3(pos.x + 0.5, pos.y + 1.3, pos.z + 0.5);
                }
                Particle particle = new AngryVillagerParticle(pPos);
                for (DataPacket pk : particle.encode()) {
                    player.dataPacket(pk);
                }
            }
            ev.setCancelled();
            if (player != null && this.needMessage[flag]) {
                Messenger.getInstance().sendMessage(player, "region.protected." + RegionFlags.getFlagName(flag), this.protectedMessageType);
            }
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

    /**
     * special for
     *
     * @see RegionEventsHandler#entitySpawn(EntitySpawnEvent)
     * because EntitySpawnEvent can`t be cancelled
     */
    private static final class EmptyEvent extends Event {

        private boolean isCancelled;

        @Override
        public void setCancelled() {
            this.isCancelled = true;
        }

        @Override
        public boolean isCancelled() {
            return this.isCancelled;
        }
    }
}
