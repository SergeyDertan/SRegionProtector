package Sergey_Dertan.SRegionProtector.Region;

import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Flags.FlagList;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.ConfigSection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Region extends SimpleAxisAlignedBB {

    private String name;
    private String creator;
    private String level;
    private List<String> owners, members;
    private FlagList flags;
    private Set<Chunk> chunks;

    public Region(String name, String creator, String level, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, List<String> owners, List<String> members, FlagList flags) {
        super(minX, minY, minZ, maxX, maxY, maxZ);
        this.name = name;
        this.creator = creator;
        this.level = level;
        this.owners = owners;
        this.members = members;
        this.flags = flags;
        this.chunks = new HashSet<>();
    }

    public Region(String name, String creator, String level, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this(name, creator, level, minX, minY, minZ, maxX, maxY, maxZ, new ArrayList<>(), new ArrayList<>(), RegionFlags.getDefaultFlagList());
    }

    public void clearUsers() {
        this.creator = "";
        this.owners.clear();
        this.members.clear();
    }

    public String getLevel() {
        return this.level;
    }

    public String getName() {
        return this.name;
    }

    public String getCreator() {
        return this.creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public FlagList getFlagList() {
        return this.flags;
    }

    public void setFlags(FlagList flags) {
        this.flags = flags;
    }

    public List<String> getMembers() {
        return this.members;
    }

    public List<String> getOwners() {
        return this.owners;
    }

    public boolean isOwner(String player, boolean ifCreator) {
        return this.owners.contains(player) || (ifCreator && this.creator.equals(player));
    }

    public boolean isOwner(String player) {
        return this.isOwner(player, true);
    }

    public boolean isCreator(String player) {
        return this.creator.equals(player);
    }

    public boolean isMember(String player) {
        return this.members.contains(player);
    }

    void removeOwner(String player) {
        this.owners.remove(player);
    }

    void removeMember(String player) {
        this.members.remove(player);
    }

    public Set<Chunk> getChunks() {
        return this.chunks;
    }

    public ConfigSection toMap() throws IOException {
        ConfigSection arr = new ConfigSection();
        arr.put("name", this.name);
        arr.put("creator", this.creator);

        arr.put("level", this.level);
        arr.put("min_x", this.getMinX());
        arr.put("min_y", this.getMinY());
        arr.put("min_z", this.getMinZ());

        arr.put("max_x", this.getMaxX());
        arr.put("max_y", this.getMaxY());
        arr.put("max_z", this.getMaxZ());

        String owners = Utils.serializeArray(this.owners.toArray(new String[]{}));
        String members = Utils.serializeArray(this.members.toArray(new String[]{}));

        arr.put("owners", owners);
        arr.put("members", members);

        return arr;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Region && this.level.equals(((Region) obj).level) &&
                this.getMinX() == ((Region) obj).getMinX() &&
                this.getMinY() == ((Region) obj).getMinY() &&
                this.getMinZ() == ((Region) obj).getMinZ() &&
                this.getMaxX() == ((Region) obj).getMaxX() &&
                this.getMaxY() == ((Region) obj).getMaxY() &&
                this.getMaxZ() == ((Region) obj).getMaxZ();
    }

    void addMember(String target) {
        this.members.add(target);
    }

    void addOwner(String target) {
        this.owners.add(target);
    }

    public boolean isLives(String target) {
        return this.creator.equals(target) || this.owners.contains(target) || this.members.contains(target);
    }
}