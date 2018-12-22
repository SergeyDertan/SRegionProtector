package Sergey_Dertan.SRegionProtector.Main;

public enum SaveType {

    AUTO(0),
    MANUAL(1),
    DISABLING(2);

    private final int id;

    private SaveType(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }
}
