package Sergey_Dertan.SRegionProtector.Region.Selector;

import cn.nukkit.level.Position;

public final class SelectorSession {

    public Position pos1, pos2;

    private int expirationTime;

    public SelectorSession() {
        this.expirationTime = (int) System.currentTimeMillis() / 1000;
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

    public int getExpirationTime() {
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
}