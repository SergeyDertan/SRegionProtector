package Sergey_Dertan.SRegionProtector.Region;

public enum RegionGroup {
    CREATOR(0),
    OWNER(1),
    MEMBER(2);

    private final int id;

    RegionGroup(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
