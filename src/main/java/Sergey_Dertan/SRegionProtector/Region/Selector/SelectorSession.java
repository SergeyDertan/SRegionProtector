package Sergey_Dertan.SRegionProtector.Region.Selector;

import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

@SuppressWarnings("WeakerAccess")
public final class SelectorSession {

    public static final long ACTION_TIMEOUT = 500L;

    private final long lifeTime;
    public long lastAction;
    public Position pos1, pos2;
    public boolean nextPos = true;
    private long expirationTime;

    public SelectorSession(long lifeTime) {
        this.expirationTime = System.currentTimeMillis() + lifeTime;
        this.lifeTime = lifeTime;
        this.lastAction = System.currentTimeMillis() - ACTION_TIMEOUT - 1L;
    }

    public Position getPos1() {
        return this.pos1;
    }

    public void setPos1(Position pos1) {
        this.pos1 = pos1;
    }

    public Position getPos2() {
        return this.pos2;
    }

    public void setPos2(Position pos2) {
        this.pos2 = pos2;
    }

    public long getExpirationTime() {
        return this.expirationTime;
    }

    public long calculateRegionSize() {
        long minX = (long) Math.min(this.pos1.x, this.pos2.x);
        long minY = (long) Math.min(this.pos1.y, this.pos2.y);
        long minZ = (long) Math.min(this.pos1.z, this.pos2.z);

        long maxX = (long) Math.max(this.pos1.x, this.pos2.x);
        long maxY = (long) Math.max(this.pos1.y, this.pos2.y);
        long maxZ = (long) Math.max(this.pos1.z, this.pos2.z);

        long size = (maxX - minX) * (maxY - minY) * (maxZ - minZ);

        if (size < 0L) return Long.MAX_VALUE;

        return size;
    }

    public boolean setNextPos(Position pos) {
        if (System.currentTimeMillis() - this.lastAction < ACTION_TIMEOUT) return false;
        if (this.nextPos) {
            this.pos1 = pos;
        } else {
            this.pos2 = pos;
        }
        this.nextPos = !this.nextPos;
        this.lastAction = System.currentTimeMillis();
        this.expirationTime = System.currentTimeMillis() + this.lifeTime;
        return true;
    }

    public void fixHeight() {
        if (this.pos1.level.getDimension() == Level.DIMENSION_NETHER) {
            if (this.pos1.y > 128) this.pos1.y = 128;
            if (this.pos2.y > 128) this.pos2.y = 128;
        } else {
            if (this.pos1.y > 255) this.pos1.y = 255;
            if (this.pos2.y > 255) this.pos2.y = 255;
        }
        if (this.pos1.y < 0) this.pos1.y = 0;
        if (this.pos2.y < 0) this.pos2.y = 0;
    }
}
