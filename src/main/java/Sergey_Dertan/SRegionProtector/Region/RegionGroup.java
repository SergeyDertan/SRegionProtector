package Sergey_Dertan.SRegionProtector.Region;

public enum RegionGroup {

    CREATOR("creator"),
    OWNER("owner"),
    MEMBER("member");

    public final String name;

    RegionGroup(String name) {
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
}
