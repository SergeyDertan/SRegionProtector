package Sergey_Dertan.SRegionProtector.Region;

import Sergey_Dertan.SRegionProtector.BlockEntity.BlockEntityHealer;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Provider.Provider;
import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.Flags.FlagList;
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

import java.util.*;

public final class RegionManager {

    private Provider provider;
    private Map<String, Region> regions;
    private PluginLogger logger;
    private ChunkManager chunkManager;
    private Map<String, List<Region>> owners, members;
    //TODO need update

    public RegionManager(Provider provider, PluginLogger logger) {
        this.provider = provider;
        this.logger = logger;
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
        this.regions = new HashMap<>();
        this.owners = new HashMap<>();
        this.members = new HashMap<>();
        List<Map<String, Object>> regions = this.provider.loadRegionList();
        for (Map<String, Object> regionData : regions) {
            String name = (String) regionData.get("name");
            String creator = (String) regionData.get("creator");
            String level = (String) regionData.get("level");

            double minX = (double) regionData.get("min_x");
            double minY = (double) regionData.get("min_y");
            double minZ = (double) regionData.get("min_z");

            double maxX = (double) regionData.get("max_x");
            double maxY = (double) regionData.get("max_y");
            double maxZ = (double) regionData.get("max_z");

            String[] owners;
            String[] members;

            try {
                owners = Utils.deserializeStringArray((String) regionData.get("owners"));
                members = Utils.deserializeStringArray((String) regionData.get("members"));
            } catch (RuntimeException e) {
                this.logger.warning(TextFormat.YELLOW + Messenger.getInstance().getMessage("loading.error.regions", new String[]{"@region", "@err"}, new String[]{name, e.getMessage()}));
                continue;
            }

            FlagList flagList = RegionFlags.loadFlagList(this.provider.loadFlags(name));

            Level lvl = Server.getInstance().getLevelByName(level);

            if (lvl == null) {
                this.logger.warning(TextFormat.YELLOW + Messenger.getInstance().getMessage("loading.error.regions", new String[]{"@region", "@err"}, new String[]{name, "level not found"})); //TODO msg
                continue;
            }

            Region region = new Region(name, creator, lvl, minX, minY, minZ, maxX, maxY, maxZ, new ArrayList<>(Arrays.asList(owners)), new ArrayList<>(Arrays.asList(members)), flagList);

            this.regions.put(name, region);

            for (String user : owners) this.owners.computeIfAbsent(user, (usr) -> new ArrayList<>()).add(region);

            for (String user : members) this.members.computeIfAbsent(user, (usr) -> new ArrayList<>()).add(region);

            this.owners.computeIfAbsent(region.getCreator(), (usr) -> new ArrayList<>()).add(region);
        }

        this.logger.info(TextFormat.GREEN + Messenger.getInstance().getMessage("loading.regions.success", "@count", String.valueOf(this.regions.size())));
    }

    public Region createRegion(String name, String creator, Vector3f pos1, Vector3f pos2, Level level) {
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
        this.owners.computeIfAbsent(creator, (s) -> new ArrayList<>()).add(region);
        this.regions.put(name, region);

        Vector3 pos = region.getHealerVector();

        new BlockEntityHealer(
                level.getChunk((int) pos.x >> 4, (int) pos.z >> 4, true), //TODO true
                BlockEntityHealer.getDefaultNBT(region.getHealerVector(), name)
        );
        return region;
    }

    public void changeRegionOwner(Region region, String newOwner) {
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

        this.owners.computeIfAbsent(newOwner, (s) -> new ArrayList<>()).add(region);
        region.setCreator(newOwner);
        region.getFlagList().getSellFlag().state = false;
        region.getFlagList().getSellFlag().price = -1;
    }

    public void removeRegion(Region region) {
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

    public boolean checkOverlap(Vector3f pos1, Vector3f pos2, Level level, Player player) {
        double minX = Math.min(pos1.x, pos2.x);
        double minY = Math.min(pos1.y, pos2.y);
        double minZ = Math.min(pos1.z, pos2.z);

        double maxX = Math.max(pos1.x, pos2.x);
        double maxY = Math.max(pos1.y, pos2.y);
        double maxZ = Math.max(pos1.z, pos2.z);

        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);

        for (Chunk chunk : this.chunkManager.getRegionChunks(pos1, pos2, level.getId(), false)) {
            for (Region region : chunk.getRegions()) {
                if (!region.intersectsWith(bb) || region.isCreator(player.getName())) continue;
                return true;
            }
        }
        return false;
    }

    public void addMember(Region region, String target) {
        this.members.computeIfAbsent(target, (usr) -> new ArrayList<>()).add(region);
        region.addMember(target);
    }

    public void addOwner(Region region, String target) {
        this.owners.computeIfAbsent(target, (usr) -> new ArrayList<>()).add(region);
        region.addOwner(target);
    }

    public void removeOwner(Region region, String target) {
        this.owners.get(target).remove(region);
        if (this.owners.get(target).size() == 0) this.owners.remove(target);
        region.removeOwner(target);
    }

    public void removeMember(Region region, String target) {
        this.members.get(target).remove(region);
        if (this.members.get(target).size() == 0) this.members.remove(target);
        region.removeMember(target);
    }

    public Region getRegion(String name) {
        return this.regions.get(name);
    }

    public void save() {
        this.provider.saveRegionList(new ArrayList<>(this.regions.values()));
    }

    public List<Region> getPlayersRegionList(Player player, RegionGroup group) {
        switch (group) {
            case CREATOR:
                List<Region> list = new ArrayList<>();
                for (Region region : this.owners.getOrDefault(player.getName().toLowerCase(), new ArrayList<>())) {
                    if (region.isCreator(player.getName().toLowerCase())) list.add(region);
                }
                return list;
            case OWNER:
                return this.owners.getOrDefault(player.getName().toLowerCase(), new ArrayList<>());
            case MEMBER:
                return this.members.getOrDefault(player.getName().toLowerCase(), new ArrayList<>());
            default:
                return new ArrayList<>();
        }
    }

    public int getPlayerRegionAmount(Player player, RegionGroup group) {
        return this.getPlayersRegionList(player, group).size();
    }
}
