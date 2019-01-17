package Sergey_Dertan.SRegionProtector.Region.Flags.Flag;

public final class RegionSellFlag extends RegionFlag {

    public long price;

    public RegionSellFlag(boolean state, long price) {
        super(state);
        this.price = price;
    }

    public RegionSellFlag(long price) {
        this(false, price);
    }

    public RegionSellFlag(boolean state) {
        this(state, 0L);
    }

    public RegionSellFlag() {
        this(false, 0L);
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public RegionSellFlag clone() {
        return new RegionSellFlag(this.state, this.price);
    }
}
