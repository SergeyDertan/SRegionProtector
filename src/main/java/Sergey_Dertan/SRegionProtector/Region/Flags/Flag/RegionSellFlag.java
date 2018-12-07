package Sergey_Dertan.SRegionProtector.Region.Flags.Flag;

public final class RegionSellFlag extends RegionFlag {

    public int price;

    public RegionSellFlag(boolean state, int price) {
        super(state);
        this.price = price;
    }

    public RegionSellFlag(int price) {
        this(false, price);
    }

    public RegionSellFlag(boolean state) {
        this(state, 0);
    }

    public RegionSellFlag() {
        this(false, 0);
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public RegionSellFlag clone() {
        return new RegionSellFlag(this.state, this.price);
    }
}
