package Sergey_Dertan.SRegionProtector.Region;

import Sergey_Dertan.SRegionProtector.BlockEntity.BlockEntityHealer;
import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionSellFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionTeleportFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.ConfigSection;

import java.util.*;

import static Sergey_Dertan.SRegionProtector.Region.RegionManager.*;

public final class Region implements AxisAlignedBB {

    public final Object lock = new Object();

    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;

    public final String name;
    public final String level;

    boolean needUpdate = false;
    private String creator;
    private Set<String> owners, members;
    private RegionFlag[] flags;
    private Set<Chunk> chunks;

    public Region(String name, String creator, String level, double minX, double minY, double minZ, double maxX, double maxY, double maxZ, String[] owners, String[] members, RegionFlag[] flags) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        this.name = name;
        this.creator = creator;
        this.level = level;

        this.owners = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        this.owners.addAll(Arrays.asList(owners));

        this.members = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        this.members.addAll(Arrays.asList(members));

        this.flags = flags;
        this.chunks = new HashSet<>();
    }

    public Region(String name, String creator, String level, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this(name, creator, level, minX, minY, minZ, maxX, maxY, maxZ, new String[0], new String[0], RegionFlags.getDefaultFlagList());
    }

    public AxisAlignedBB clone() {
        return new SimpleAxisAlignedBB(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
    }

    void clearUsers() {
        synchronized (this.lock) {
            this.creator = "";
            this.owners.clear();
            this.members.clear();
            this.needUpdate = true;
        }
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
        synchronized (this.lock) {
            this.creator = creator;
            this.needUpdate = true;
        }
    }

    public boolean getFlagState(int id) {
        return this.flags[id].state;
    }

    public void setFlagState(int id, boolean state) {
        synchronized (this.lock) {
            this.flags[id].state = state;
            this.needUpdate = true;
        }
    }

    public RegionFlag getFlag(int flag) {
        return this.flags[flag].clone();
    }

    public void setTeleportFlag(Position pos, boolean state) {
        synchronized (this.lock) {
            ((RegionTeleportFlag) this.flags[RegionFlags.FLAG_SELL]).position = pos;
            ((RegionTeleportFlag) this.flags[RegionFlags.FLAG_SELL]).state = state;
            this.needUpdate = true;
        }
    }

    public void setSellFlagState(int price, boolean state) {
        synchronized (this.lock) {
            ((RegionSellFlag) this.flags[RegionFlags.FLAG_SELL]).state = state;
            ((RegionSellFlag) this.flags[RegionFlags.FLAG_SELL]).price = price;
            this.needUpdate = true;
        }
    }

    public Position getTeleportFlagPos() {
        return ((RegionTeleportFlag) this.flags[RegionFlags.FLAG_SELL]).position;
    }

    public int getSellFlagPrice() {
        return ((RegionSellFlag) this.flags[RegionFlags.FLAG_SELL]).price;
    }

    public Set<String> getMembers() {
        return new HashSet<>(this.members);
    }

    public Set<String> getOwners() {
        return new HashSet<>(this.owners);
    }

    public boolean isOwner(String player, boolean creator) {
        return this.owners.contains(player) || (creator && this.creator.equals(player));
    }

    public boolean isOwner(String player) {
        return this.isOwner(player, false);
    }

    public boolean isCreator(String player) {
        return this.creator.equalsIgnoreCase(player);
    }

    public boolean isMember(String player) {
        return this.members.contains(player);
    }

    void removeOwner(String player) {
        this.owners.remove(player);
        this.needUpdate = true;
    }

    void removeMember(String player) {
        this.members.remove(player);
        this.needUpdate = true;
    }

    Set<Chunk> getChunks() {
        return this.chunks;
    }

    void addChunk(Chunk chunk) {
        this.chunks.add(chunk);
    }

    public ConfigSection toMap() throws RuntimeException {
        ConfigSection arr = new ConfigSection();

        arr.put(NAME_TAG, this.name);
        arr.put(CREATOR_TAG, this.creator);

        arr.put(LEVEL_TAG, this.level);
        arr.put(MIN_X_TAG, this.getMinX());
        arr.put(MIN_Y_TAG, this.getMinY());
        arr.put(MIN_Z_TAG, this.getMinZ());

        arr.put(MAX_X_TAG, this.getMaxX());
        arr.put(MAX_Y_TAG, this.getMaxY());
        arr.put(MAX_Z_TAG, this.getMaxZ());

        String owners = Utils.serializeStringArray(this.owners.toArray(new String[]{}));
        String members = Utils.serializeStringArray(this.members.toArray(new String[]{}));

        arr.put(OWNERS_TAG, owners);
        arr.put(MEMBERS_TAG, members);

        return arr;
    }

    public Map<String, Map<String, Object>> flagsToMap() {

        Map<String, Map<String, Object>> flags = new HashMap<>();

        for (int i = 0; i < this.flags.length; ++i) {
            String name = RegionFlags.getFlagName(i);
            if (name.equals("")) continue;
            Map<String, Object> flagData = new HashMap<>();
            flagData.put("state", this.flags[i].state);
            switch (i) {
                case RegionFlags.FLAG_TELEPORT:
                    Position teleportPos = ((RegionTeleportFlag) this.flags[i]).position;
                    if (teleportPos == null) break;
                    Map<String, Object> pos = new HashMap<>();
                    pos.put("x", teleportPos.x);
                    pos.put("y", teleportPos.y);
                    pos.put("z", teleportPos.z);
                    pos.put("level", teleportPos.level.getName());
                    flagData.put("position", pos);
                    break;
                case RegionFlags.FLAG_SELL:
                    flagData.put("price", ((RegionSellFlag) this.flags[i]).price);
                    break;
            }
            flags.put(name, flagData);
        }

        return flags;
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
        this.needUpdate = true;
    }

    void addOwner(String target) {
        this.owners.add(target);
        this.needUpdate = true;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.clone();
    }

    public boolean isLivesIn(String target) {
        return this.creator.equalsIgnoreCase(target) || this.owners.contains(target) || this.members.contains(target);
    }

    public Position getHealerPosition() {
        return Position.fromObject(this.getHealerVector(), Server.getInstance().getLevelByName(this.level));
    }

    public Vector3 getHealerVector() {
        double x = getMinX() + (getMaxX() - getMinX()) / 2;
        double y = getMinY() + (getMaxY() - getMinY()) / 2;
        double z = getMinZ() + (getMaxZ() - getMinZ()) / 2;
        return new Vector3(x, y, z);
    }

    public BlockEntityHealer getHealerBlockEntity() {
        return (BlockEntityHealer) this.getHealerPosition().level.getBlockEntity(this.getHealerVector());
    }

    @Override
    public double getMaxX() {
        return this.maxX;
    }

    @Override
    public double getMaxY() {
        return this.maxY;
    }

    @Override
    public double getMaxZ() {
        return this.maxZ;
    }

    @Override
    public double getMinX() {
        return this.minX;
    }

    @Override
    public double getMinY() {
        return this.minY;
    }

    @Override
    public double getMinZ() {
        return this.minZ;
    }
}