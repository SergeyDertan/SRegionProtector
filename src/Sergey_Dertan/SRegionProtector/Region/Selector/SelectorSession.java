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

    public int calculateRegionSize() {
        int minX = (int) Math.min(this.pos1.x, this.pos2.x);
        int minY = (int) Math.min(this.pos1.y, this.pos2.y);
        int minZ = (int) Math.min(this.pos1.z, this.pos2.z);

        int maxX = (int) Math.max(this.pos1.x, this.pos2.x);
        int maxY = (int) Math.max(this.pos1.y, this.pos2.y);
        int maxZ = (int) Math.max(this.pos1.z, this.pos2.z);

        return (maxX - minX) * (maxY - minY) * (maxZ - minZ);
    }
}