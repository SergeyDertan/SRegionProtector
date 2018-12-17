package Sergey_Dertan.SRegionProtector.Region;

public enum RegionGroup {

    CREATOR(0, "creator"),
    OWNER(1, "owner"),
    MEMBER(2, "member");

    private final int id;
    private final String name;

    RegionGroup(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static RegionGroup get(String name) {
        name = name.toLowerCase();
        switch (name) {
            case "creator":
                return CREATOR;
            case "owner":
                return OWNER;
            case "member":
                return MEMBER;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }
}
