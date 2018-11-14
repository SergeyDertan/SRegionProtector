package Sergey_Dertan.SRegionProtector.Region.Flags.Flag;

import cn.nukkit.level.Position;

public final class RegionTeleportFlag extends RegionFlag {

    public Position position;

    public RegionTeleportFlag(boolean state, Position position) {
        super(state);
        this.position = position;
    }

    public RegionTeleportFlag(boolean state) {
        this(state, null);
    }

    public RegionTeleportFlag(Position position) {
        this(false, position);
    }

    public RegionTeleportFlag() {
        this(false, null);
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public RegionTeleportFlag clone() {
        return new RegionTeleportFlag(this.state, this.position == null ? null :
                new Position(this.position.x, this.position.y, this.position.z, this.position.level) //TODO check
        );
    }
}