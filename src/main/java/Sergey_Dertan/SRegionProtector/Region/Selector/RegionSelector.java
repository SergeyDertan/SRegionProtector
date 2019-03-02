package Sergey_Dertan.SRegionProtector.Region.Selector;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.SourceInterface;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Set;

public final class RegionSelector {

    private final long sessionLifetime;
    private final Int2ObjectMap<SelectorSession> sessions;
    private final int borderBlock;
    private final Int2ObjectMap<Set<Vector3>> borders;
    private final Field interfaz;
    private final boolean async;

    public RegionSelector(long sessionLifetime, Block borderBlock, boolean async) {
        this.sessions = new Int2ObjectArrayMap<>();
        this.borders = new Int2ObjectArrayMap<>();
        this.sessionLifetime = sessionLifetime;
        this.borderBlock = GlobalBlockPalette.getOrCreateRuntimeId(borderBlock.getId(), borderBlock.getDamage());
        this.borders.defaultReturnValue(Collections.emptySet());
        this.async = async;

        Field interfaz = null;
        if (async) {
            try {
                interfaz = Player.class.getDeclaredField("interfaz");
                interfaz.setAccessible(true);
            } catch (NoSuchFieldException | SecurityException ignore) {
            }
        }
        this.interfaz = interfaz;
    }

    public synchronized void removeSession(Player player) {
        this.sessions.remove(player.getLoaderId());
    }

    public synchronized SelectorSession getSession(Player player) {
        return this.sessions.computeIfAbsent(player.getLoaderId(), s -> new SelectorSession(this.sessionLifetime));
    }

    public synchronized void clear() {
        long currentTime = System.currentTimeMillis();
        this.sessions.int2ObjectEntrySet().removeIf(s -> s.getValue().getExpirationTime() < currentTime);
    }

    public synchronized boolean sessionExists(Player player) {
        return this.sessions.containsKey(player.getLoaderId());
    }

    @SuppressWarnings({"ConstantConditions", "Duplicates"})
    public synchronized void showBorders(Player target, Vector3 pos1, Vector3 pos2) {
        int minX = (int) Math.min(pos1.x, pos2.x);
        int minY = (int) Math.min(pos1.y, pos2.y);
        int minZ = (int) Math.min(pos1.z, pos2.z);

        int maxX = (int) Math.max(pos1.x, pos2.x);
        int maxY = (int) Math.max(pos1.y, pos2.y);
        int maxZ = (int) Math.max(pos1.z, pos2.z);

        Set<Vector3> blocks = new ObjectArraySet<>(10);
        SourceInterface interfaz = null;
        if (this.async) {
            try {
                interfaz = this.async ? (SourceInterface) this.interfaz.get(target) : null;
            } catch (IllegalAccessException ignore) {
            }
        }

        for (int yt = minY; yt <= maxY; ++yt) {
            for (int xt = minX; ; xt = maxX) {
                for (int zt = minZ; ; zt = maxZ) {
                    UpdateBlockPacket pk = new UpdateBlockPacket();
                    pk.x = xt;
                    pk.y = yt;
                    pk.z = zt;
                    pk.flags = UpdateBlockPacket.FLAG_NONE;
                    pk.blockRuntimeId = this.borderBlock;
                    if (this.async) {
                        interfaz.putPacket(target, pk);
                    } else {
                        target.dataPacket(pk);
                    }
                    blocks.add(new Vector3(xt, yt, zt));
                    if (zt == maxZ) break;
                }
                if (xt == maxX) break;
            }
        }

        for (int yd = minY; ; yd = maxY) {
            for (int zd = minZ; ; zd = maxZ) {
                for (int zx = minX; zx <= maxX; ++zx) {
                    UpdateBlockPacket pk = new UpdateBlockPacket();
                    pk.x = zx;
                    pk.y = yd;
                    pk.z = zd;
                    pk.flags = UpdateBlockPacket.FLAG_NONE;
                    pk.blockRuntimeId = this.borderBlock;
                    if (this.async) {
                        interfaz.putPacket(target, pk);
                    } else {
                        target.dataPacket(pk);
                    }
                    blocks.add(new Vector3(zx, yd, zd));
                }
                if (zd == maxZ) break;
            }

            for (int xd = minX; ; xd = maxX) {
                for (int zx = minZ; zx <= maxZ; ++zx) {
                    UpdateBlockPacket pk = new UpdateBlockPacket();
                    pk.x = xd;
                    pk.y = yd;
                    pk.z = zx;
                    pk.flags = UpdateBlockPacket.FLAG_NONE;
                    pk.blockRuntimeId = this.borderBlock;
                    if (this.async) {
                        interfaz.putPacket(target, pk);
                    } else {
                        target.dataPacket(pk);
                    }
                    blocks.add(new Vector3(xd, yd, zx));
                }
                if (xd == maxX) break;
            }
            if (yd == maxY) break;
        }

        this.borders.put(target.getLoaderId(), blocks);
    }

    public synchronized boolean hasBorders(Player player) {
        return this.borders.containsKey(player.getLoaderId());
    }

    @SuppressWarnings("ConstantConditions")
    public synchronized void removeBorders(Player target, boolean send) {
        if (send) {
            if (this.async) {
                SourceInterface interfaz = null;
                try {
                    interfaz = (SourceInterface) this.interfaz.get(target);
                } catch (IllegalAccessException ignore) {
                }
                SourceInterface inter = interfaz;
                this.borders.get(target.getLoaderId()).forEach(s -> {
                    UpdateBlockPacket pk = new UpdateBlockPacket();
                    pk.x = (int) s.x;
                    pk.y = (int) s.y;
                    pk.z = (int) s.z;
                    pk.flags = UpdateBlockPacket.FLAG_NONE;
                    pk.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(target.level.getBlockIdAt((int) s.x, (int) s.y, (int) s.z), target.level.getBlockDataAt((int) s.x, (int) s.y, (int) s.z));
                    inter.putPacket(target, pk);
                });
            } else {
                target.level.sendBlocks(new Player[]{target}, this.borders.get(target.getLoaderId()).toArray(new Vector3[0]));
            }
        }
        this.borders.remove(target.getLoaderId());
    }

    public void removeBorders(Player target) {
        this.removeBorders(target, true);
    }
}
