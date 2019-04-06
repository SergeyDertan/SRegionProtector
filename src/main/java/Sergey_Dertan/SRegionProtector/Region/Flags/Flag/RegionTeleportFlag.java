package Sergey_Dertan.SRegionProtector.Region.Flags.Flag;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

public final class RegionTeleportFlag extends RegionFlag {

    public Vector3 position;
    public String level; //if level isn`t loaded flag wont work while using cn.nukkit.level.Position

    public RegionTeleportFlag(boolean state, Vector3 position, String level) {
        super(state);
        this.position = position;
        this.level = level;
    }

    public RegionTeleportFlag(boolean state) {
        this(state, null, null);
    }

    public RegionTeleportFlag(boolean state, Position position) {
        this(state, position.clone(), position.level.getName());
    }

    public RegionTeleportFlag(Position position) {
        this(false, position.clone(), position.level.getName());
    }

    public RegionTeleportFlag() {
        this(false, null, null);
    }

    public Vector3 getVector() {
        return this.position;
    }

    /**
     * @return Position or null if level isn`t exists or loaded
     */
    public Position getPosition() {
        Level lvl = Server.getInstance().getLevelByName(this.level);
        if (lvl == null) return null;
        return Position.fromObject(this.position.clone(), lvl);
    }

    public void setPosition(Position position) {
        this.position = position.clone();
        this.level = position.level.getName();
    }

    @Override
    public RegionTeleportFlag clone() {
        if (this.position == null || this.level == null || this.level.isEmpty()) {
            return new RegionTeleportFlag(this.state);
        }
        return new RegionTeleportFlag(this.state, this.position.clone(), this.level);
    }
}
