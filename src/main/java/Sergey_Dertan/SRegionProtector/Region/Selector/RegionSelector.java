package Sergey_Dertan.SRegionProtector.Region.Selector;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.Set;

public final class RegionSelector {

    public final int sessionLifetime;
    private Int2ObjectMap<SelectorSession> sessions;
    private Block borderBlock;
    private Int2ObjectMap<Set<Vector3>> borders;

    public RegionSelector(int sessionLifetime, Block borderBlock) {
        this.sessions = new Int2ObjectArrayMap<>();
        this.borders = new Int2ObjectArrayMap<>();
        this.sessionLifetime = sessionLifetime;
        this.borderBlock = borderBlock;
        this.borders.defaultReturnValue(new ObjectArraySet<>());
    }

    public void removeSession(Player player) {
        this.sessions.remove(player.getLoaderId());
    }

    public SelectorSession getSession(Player player) {
        return this.sessions.computeIfAbsent(player.getLoaderId(), s -> new SelectorSession(this.sessionLifetime));
    }

    public void clear() {
        int currentTime = (int) System.currentTimeMillis() / 1000;
        this.sessions.int2ObjectEntrySet().removeIf(s -> s.getValue().getExpirationTime() < currentTime);
    }

    public boolean sessionExists(Player player) {
        return this.sessions.containsKey(player.getLoaderId());
    }

    public void showBorders(Player target, Vector3 pos1, Vector3 pos2) { //TODO async?
        int minX = (int) Math.min(pos1.x, pos2.x);
        int minY = (int) Math.min(pos1.y, pos2.y);
        int minZ = (int) Math.min(pos1.z, pos2.z);

        int maxX = (int) Math.max(pos1.x, pos2.x);
        int maxY = (int) Math.max(pos1.y, pos2.y);
        int maxZ = (int) Math.max(pos1.z, pos2.z);

        Set<Vector3> blocks = new ObjectArraySet<>(10); //TODO

        for (int yt = minY; yt <= maxY; ++yt) {
            for (int xt = minX; ; xt = maxX) {
                for (int zt = minZ; ; zt = maxZ) {
                    UpdateBlockPacket pk = new UpdateBlockPacket();
                    pk.x = xt;
                    pk.y = yt;
                    pk.z = zt;
                    pk.flags = UpdateBlockPacket.FLAG_NONE; //TODO
                    pk.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(this.borderBlock.getId(), this.borderBlock.getDamage());
                    target.dataPacket(pk);
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
                    pk.flags = UpdateBlockPacket.FLAG_NONE; //TODO
                    pk.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(this.borderBlock.getId(), this.borderBlock.getDamage());
                    target.dataPacket(pk);
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
                    pk.flags = UpdateBlockPacket.FLAG_NONE; //TODO
                    pk.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(this.borderBlock.getId(), this.borderBlock.getDamage());
                    target.dataPacket(pk);
                    blocks.add(new Vector3(xd, yd, zx));
                }
                if (xd == maxX) break;
            }
            if (yd == maxY) break;
        }

        this.borders.put(target.getLoaderId(), blocks);
    }

    public boolean hasBorders(Player player) {
        return this.borders.containsKey(player.getLoaderId());
    }

    public void removeBorders(Player target, boolean send) {
        if (send) {
            target.level.sendBlocks(new Player[]{target}, this.borders.get(target.getLoaderId()).toArray(new Vector3[0]));
        }
        this.borders.remove(target.getLoaderId());
    }

    public void removeBorders(Player target) {
        this.removeBorders(target, true);
    }
}