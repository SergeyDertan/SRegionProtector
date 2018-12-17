package Sergey_Dertan.SRegionProtector.Region;

import Sergey_Dertan.SRegionProtector.BlockEntity.BlockEntityHealer;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Provider.Provider;
import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.math.Vector3f;
import cn.nukkit.plugin.PluginLogger;
import cn.nukkit.utils.TextFormat;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public final class RegionManager {

    public final static String MIN_X_TAG = "min_x";
    public final static String MIN_Y_TAG = "min_y";
    public final static String MIN_Z_TAG = "min_z";

    public final static String MAX_X_TAG = "max_x";
    public final static String MAX_Y_TAG = "max_y";
    public final static String MAX_Z_TAG = "max_z";

    public final static String OWNERS_TAG = "owners";
    public final static String MEMBERS_TAG = "members";
    public final static String NAME_TAG = "name";
    public final static String LEVEL_TAG = "level";
    public final static String CREATOR_TAG = "creator";

    private Provider provider;
    private Map<String, Region> regions;
    private PluginLogger logger;
    private ChunkManager chunkManager;
    private Map<String, Set<Region>> owners, members;
    private Messenger messenger;

    public RegionManager(Provider provider, PluginLogger logger) {
        this.provider = provider;
        this.logger = logger;
        this.messenger = Messenger.getInstance();
    }

    public void setChunkManager(ChunkManager chunkManager) { //TODO fix?
        this.chunkManager = chunkManager;
        this.addChunksToRegions();
    }

    private void addChunksToRegions() {
        for (Region region : this.regions.values()) {
            Vector3 min = new Vector3(region.getMaxX(), region.getMaxY(), region.getMaxZ());
            Vector3 max = new Vector3(region.getMinX(), region.getMinY(), region.getMinZ());
            this.chunkManager.getRegionChunks(min.asVector3f(), max.asVector3f(), region.getLevel().getId()).forEach(region::addChunk);
        }
    }

    public Map<String, Region> getRegions() {
        return this.regions;
    }

    public boolean regionExists(String name) {
        return this.regions.containsKey(name);
    }

    public void init() {
        this.regions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.owners = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.members = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Set<Map<String, Object>> regions = this.provider.loadRegionList();
        for (Map<String, Object> regionData : regions) {
            String name = (String) regionData.get(NAME_TAG);
            String creator = (String) regionData.get(CREATOR_TAG);
            String level = (String) regionData.get(LEVEL_TAG);

            double minX = (double) regionData.get(MIN_X_TAG);
            double minY = (double) regionData.get(MIN_Y_TAG);
            double minZ = (double) regionData.get(MIN_Z_TAG);

            double maxX = (double) regionData.get(MAX_X_TAG);
            double maxY = (double) regionData.get(MAX_Y_TAG);
            double maxZ = (double) regionData.get(MAX_Z_TAG);

            String[] owners;
            String[] members;

            try {
                owners = Utils.deserializeStringArray((String) regionData.get(OWNERS_TAG));
                members = Utils.deserializeStringArray((String) regionData.get(MEMBERS_TAG));
            } catch (RuntimeException e) {
                this.logger.warning(TextFormat.YELLOW + this.messenger.getMessage("loading.error.regions", new String[]{"@region", "@err"}, new String[]{name, e.getMessage()}));
                continue;
            }

            RegionFlag[] flagList = RegionFlags.loadFlagList(this.provider.loadFlags(name));

            Level lvl = Server.getInstance().getLevelByName(level);

            if (lvl == null) {
                this.logger.warning(TextFormat.YELLOW + this.messenger.getMessage("loading.error.regions", new String[]{"@region", "@err"}, new String[]{name, "level not found"})); //TODO msg
                continue;
            }

            Region region = new Region(name, creator, lvl, minX, minY, minZ, maxX, maxY, maxZ, owners, members, flagList);

            this.regions.put(name, region);

            for (String user : owners) this.owners.computeIfAbsent(user, (usr) -> new HashSet<>()).add(region);

            for (String user : members) this.members.computeIfAbsent(user, (usr) -> new HashSet<>()).add(region);

            this.owners.computeIfAbsent(region.getCreator(), (usr) -> new HashSet<>()).add(region);
        }

        this.logger.info(TextFormat.GREEN + this.messenger.getMessage("loading.regions.success", "@count", String.valueOf(this.regions.size())));
    }

    public synchronized Region createRegion(String name, String creator, Vector3f pos1, Vector3f pos2, Level level) {
        double minX = Math.min(pos1.x, pos2.x);
        double minY = Math.min(pos1.y, pos2.y);
        double minZ = Math.min(pos1.z, pos2.z);

        double maxX = Math.max(pos1.x, pos2.x);
        double maxY = Math.max(pos1.y, pos2.y);
        double maxZ = Math.max(pos1.z, pos2.z);

        Region region = new Region(name, creator, level, minX, minY, minZ, maxX, maxY, maxZ);

        this.chunkManager.getRegionChunks(pos1, pos2, level.getId(), true).forEach(chunk -> {
            chunk.addRegion(region);
            region.addChunk(chunk);
        });
        this.owners.computeIfAbsent(creator, (s) -> new HashSet<>()).add(region);
        this.regions.put(name, region);

        Vector3 pos = region.getHealerVector();

        new BlockEntityHealer(
                level.getChunk((int) pos.x >> 4, (int) pos.z >> 4, true), //TODO true
                BlockEntityHealer.getDefaultNBT(region.getHealerVector(), name)
        );
        region.needUpdate = true;
        return region;
    }

    public void changeRegionOwner(Region region, String newOwner) {
        synchronized (region.lock) {
            region.getMembers().forEach(member ->
                    {
                        this.members.get(member).remove(region);
                        if (this.members.get(member).size() == 0) this.members.remove(member);
                    }
            );

            region.getOwners().forEach(owner ->
                    {
                        this.owners.get(owner).remove(region);
                        if (this.owners.get(owner).size() == 0) this.owners.remove(owner);
                    }
            );

            this.owners.get(region.getCreator()).remove(region);
            if (this.owners.get(region.getCreator()).size() == 0) this.owners.remove(region.getCreator());

            region.clearUsers();

            this.owners.computeIfAbsent(newOwner, (s) -> new HashSet<>()).add(region);
            region.setCreator(newOwner);
            region.setSellFlagState(-1, false);
        }
    }

    public synchronized void removeRegion(Region region) {
        synchronized (region.lock) {
            region.getMembers().forEach(member ->
                    {
                        this.members.get(member).remove(region);
                        if (this.members.get(member).size() == 0) this.members.remove(member);
                    }
            );

            region.getOwners().forEach(owner ->
                    {
                        this.owners.get(owner).remove(region);
                        if (this.owners.get(owner).size() == 0) this.owners.remove(owner);
                    }
            );

            this.owners.get(region.getCreator()).remove(region);
            if (this.owners.get(region.getCreator()).size() == 0) this.owners.remove(region.getCreator());

            region.getChunks().forEach(chunk -> chunk.removeRegion(region));

            this.regions.remove(region.getName());
            this.provider.removeRegion(region);

            if (region.getHealerBlockEntity() != null) region.getHealerBlockEntity().close();
        }
    }

    public boolean checkOverlap(Vector3f pos1, Vector3f pos2, Level level, Player player) {
        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(pos1.asVector3(), pos2.asVector3());

        for (Chunk chunk : this.chunkManager.getRegionChunks(pos1, pos2, level.getId(), false)) {
            for (Region region : chunk.getRegions()) {
                if (!region.intersectsWith(bb) || region.isCreator(player.getName())) continue;
                return true;
            }
        }
        return false;
    }

    public void addMember(Region region, String target) {
        synchronized (region.lock) {
            this.members.computeIfAbsent(target, (usr) -> new HashSet<>()).add(region);
            region.addMember(target);
        }
    }

    public void addOwner(Region region, String target) {
        synchronized (region.lock) {
            this.owners.computeIfAbsent(target, (usr) -> new HashSet<>()).add(region);
            region.addOwner(target);
        }
    }

    public void removeOwner(Region region, String target) {
        synchronized (region.lock) {
            this.owners.get(target).remove(region);
            if (this.owners.get(target).size() == 0) this.owners.remove(target);
            region.removeOwner(target);
        }
    }

    public void removeMember(Region region, String target) {
        synchronized (region.lock) {
            this.members.get(target).remove(region);
            if (this.members.get(target).size() == 0) this.members.remove(target);
            region.removeMember(target);
        }
    }

    public Region getRegion(String name) {
        return this.regions.get(name);
    }

    public synchronized void save(boolean auto) {
        int amount = 0;
        for (Region region : this.regions.values()) {
            synchronized (region.lock) {
                if (!region.needUpdate) continue;
                this.provider.saveRegion(region);
                region.needUpdate = false;
                ++amount;
            }
        }
        if (auto) {
            this.logger.info(TextFormat.GREEN + this.messenger.getMessage("regions-auto-save", "@amount", String.valueOf(amount)));
        } else {
            this.logger.info(TextFormat.GREEN + this.messenger.getMessage("disabling.regions-saved", "@amount", String.valueOf(this.regions.size())));
        }
    }

    public void save() {
        this.save(false);
    }

    public Set<Region> getPlayersRegionList(Player player, RegionGroup group) {
        switch (group) {
            case CREATOR:
                Set<Region> list = new HashSet<>();
                for (Region region : this.owners.getOrDefault(player.getName().toLowerCase(), new HashSet<>())) {
                    if (region.isCreator(player.getName().toLowerCase())) list.add(region);
                }
                return list;
            case OWNER:
                return this.owners.getOrDefault(player.getName().toLowerCase(), new HashSet<>());
            case MEMBER:
                return this.members.getOrDefault(player.getName().toLowerCase(), new HashSet<>());
            default:
                return new HashSet<>();
        }
    }

    public int getPlayerRegionAmount(Player player, RegionGroup group) {
        return this.getPlayersRegionList(player, group).size();
    }
}
