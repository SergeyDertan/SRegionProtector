package Sergey_Dertan.SRegionProtector.Region.Flags.Flag;

import Sergey_Dertan.SRegionProtector.Utils.Cloneable;

public class RegionFlag extends Cloneable {

    public boolean state;

    public RegionFlag(boolean state) {
        this.state = state;
    }

    public RegionFlag() {
        this(false);
    }

    public final boolean getState() {
        return this.state;
    }

    public final void setState(boolean state) {
        this.state = state;
    }

    @Override
    public RegionFlag clone() {
        return new RegionFlag(this.state);
    }
}
