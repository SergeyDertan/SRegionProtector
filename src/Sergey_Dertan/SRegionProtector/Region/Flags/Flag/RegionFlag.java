package Sergey_Dertan.SRegionProtector.Region.Flags.Flag;

public class RegionFlag {

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
}
