package Sergey_Dertan.SRegionProtector.Region;

import Sergey_Dertan.SRegionProtector.BlockEntity.BlockEntityHealer;
import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Messenger.Messenger;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.Converter;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.FlagListDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataObject.RegionDataObject;
import Sergey_Dertan.SRegionProtector.Provider.DataProvider;
import Sergey_Dertan.SRegionProtector.Region.Chunk.Chunk;
import Sergey_Dertan.SRegionProtector.Region.Chunk.ChunkManager;
import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Utils.Utils;
import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.TextFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import static Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags.FLAG_AMOUNT;
import static Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags.fixMissingFlags;

public final class RegionManager {

    private DataProvider provider;
    private Object2ObjectMap<String, Region> regions;
    private Logger logger;
    private ChunkManager chunkManager;
    private Object2ObjectMap<String, ObjectList<Region>> owners;
    private Object2ObjectMap<String, ObjectList<Region>> members;
    private Messenger messenger;

    public RegionManager(DataProvider provider, Logger logger) {
        this.provider = provider;
        this.logger = logger;
        this.messenger = Messenger.getInstance();
    }

    public void setChunkManager(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
        this.addChunksToRegions();
    }

    private void addChunksToRegions() {
        for (Region region : this.regions.values()) {
            Vector3 min = new Vector3(region.getMaxX(), region.getMaxY(), region.getMaxZ());
            Vector3 max = new Vector3(region.getMinX(), region.getMinY(), region.getMinZ());
            this.chunkManager.getRegionChunks(min, max, region.level).forEach(region::addChunk);
        }
    }

    public Map<String, Region> getRegions() {
        Map<String, Region> regions = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        regions.putAll(this.regions);
        return regions;
    }

    public synchronized boolean regionExists(String name) {
        if (name.replace(" ", "").isEmpty()) return false;
        return this.regions.containsKey(name);
    }

    public void init() {
        this.regions = new Object2ObjectAVLTreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.owners = new Object2ObjectAVLTreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.members = new Object2ObjectAVLTreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Set<RegionDataObject> regions = this.provider.loadRegionList();
        for (RegionDataObject rdo : regions) {
            String name = rdo.name;
            String creator = rdo.creator;
            String level = rdo.level;

            double minX = rdo.minX;
            double minY = rdo.minY;
            double minZ = rdo.minZ;

            double maxX = rdo.maxX;
            double maxY = rdo.maxY;
            double maxZ = rdo.maxZ;

            String[] owners;
            String[] members;

            try {
                owners = Utils.deserializeStringArray(rdo.owners);
                members = Utils.deserializeStringArray(rdo.members);
            } catch (RuntimeException e) {
                this.logger.warning(TextFormat.YELLOW + this.messenger.getMessage("loading.error.regions", new String[]{"@region", "@err"}, new String[]{name, e.getMessage()}));
                continue;
            }

            FlagListDataObject flags = this.provider.loadFlags(name);

            RegionFlag[] flagList = Converter.fromDataObject(flags);

            boolean needUpdate = false;
            if (flagList.length < FLAG_AMOUNT) {
                needUpdate = true;
                fixMissingFlags(flagList);
            }

            Region region = new Region(name, creator, level, minX, minY, minZ, maxX, maxY, maxZ, owners, members, flagList);

            region.needUpdate = needUpdate;

            this.regions.put(name, region);

            for (String user : owners) this.owners.computeIfAbsent(user, (usr) -> new ObjectArrayList<>()).add(region);

            for (String user : members) this.members.computeIfAbsent(user, (usr) -> new ObjectArrayList<>()).add(region);

            this.owners.computeIfAbsent(region.getCreator(), (usr) -> new ObjectArrayList<>()).add(region);
        }

        this.logger.info(TextFormat.GREEN + this.messenger.getMessage("loading.regions.success", "@count", String.valueOf(this.regions.size())));
    }

    public synchronized Region createRegion(String name, String creator, Vector3 pos1, Vector3 pos2, Level level) {
        double minX = Math.min(pos1.x, pos2.x);
        double minY = Math.min(pos1.y, pos2.y);
        double minZ = Math.min(pos1.z, pos2.z);

        double maxX = Math.max(pos1.x, pos2.x);
        double maxY = Math.max(pos1.y, pos2.y);
        double maxZ = Math.max(pos1.z, pos2.z);

        Region region = new Region(name, creator, level.getName(), minX, minY, minZ, maxX, maxY, maxZ);

        this.chunkManager.getRegionChunks(pos1, pos2, level.getName(), true).forEach(chunk -> {
            chunk.addRegion(region);
            region.addChunk(chunk);
        });
        this.owners.computeIfAbsent(creator, (s) -> new ObjectArrayList<>()).add(region);
        this.regions.put(name, region);

        Vector3 pos = region.getHealerVector();

        new BlockEntityHealer(
                level.getChunk((int) pos.x >> 4, (int) pos.z >> 4, true),
                BlockEntityHealer.getDefaultNBT(region.getHealerVector(), region.name)
        );
        region.needUpdate = true;
        return region;
    }

    public synchronized void changeRegionOwner(Region region, String newOwner) {
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

            this.owners.computeIfAbsent(newOwner, (s) -> new ObjectArrayList<>()).add(region);
            region.setCreator(newOwner);
            region.setSellFlagState(-1L, false);
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

    public boolean checkOverlap(Vector3 pos1, Vector3 pos2, String level, String player, boolean checkSellFlag) {
        SimpleAxisAlignedBB bb = new SimpleAxisAlignedBB(pos1, pos2);

        for (Chunk chunk : this.chunkManager.getRegionChunks(pos1, pos2, level, false)) {
            for (Region region : chunk.getRegions()) {
                if (!region.intersectsWith(bb)) continue;
                if (checkSellFlag && region.getFlagState(RegionFlags.FLAG_SELL)) return true;
                if (region.isCreator(player)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean checkOverlap(Vector3 pos1, Vector3 pos2, String level, String player) {
        return this.checkOverlap(pos1, pos2, level, player, false);
    }

    public synchronized void addMember(Region region, String target) {
        synchronized (region.lock) {
            this.members.computeIfAbsent(target, (usr) -> new ObjectArrayList<>()).add(region);
            region.addMember(target);
        }
    }

    public synchronized void addOwner(Region region, String target) {
        synchronized (region.lock) {
            this.owners.computeIfAbsent(target, (usr) -> new ObjectArrayList<>()).add(region);
            region.addOwner(target);
        }
    }

    public synchronized void removeOwner(Region region, String target) {
        synchronized (region.lock) {
            this.owners.get(target).remove(region);
            if (this.owners.get(target).size() == 0) this.owners.remove(target);
            region.removeOwner(target);
        }
    }

    public synchronized void removeMember(Region region, String target) {
        synchronized (region.lock) {
            this.members.get(target).remove(region);
            if (this.members.get(target).size() == 0) this.members.remove(target);
            region.removeMember(target);
        }
    }

    public synchronized Region getRegion(String name) {
        return this.regions.get(name);
    }

    public synchronized void save(SRegionProtectorMain.SaveType saveType, String initiator) {
        int amount = 0;
        for (Region region : this.regions.values()) {
            synchronized (region.lock) {
                if (!region.needUpdate) continue;
                this.provider.saveRegion(region);
                region.needUpdate = false;
                ++amount;
            }
        }
        switch (saveType) {
            case AUTO:
                this.logger.info(TextFormat.GREEN + this.messenger.getMessage("regions-auto-save", "@amount", String.valueOf(amount)));
                break;
            case DISABLING:
                this.logger.info(TextFormat.GREEN + this.messenger.getMessage("disabling.regions-saved", "@amount", String.valueOf(this.regions.size())));
                break;
            case MANUAL:
                this.logger.info(TextFormat.GREEN + this.messenger.getMessage("regions-manual-save", new String[]{"@amount", "@initiator"}, new String[]{String.valueOf(this.regions.size()), initiator}));
                break;
        }
    }

    public synchronized void save(SRegionProtectorMain.SaveType saveType) {
        this.save(saveType, null);
    }

    public Set<Region> getPlayersRegionList(Player player, RegionGroup group) {
        switch (group) {
            case CREATOR:
                Set<Region> list = new HashSet<>();
                for (Region region : this.owners.getOrDefault(player.getName(), new ObjectArrayList<>())) {
                    if (region.isCreator(player.getName())) list.add(region);
                }
                return list;
            case OWNER:
                return new HashSet<>(this.owners.getOrDefault(player.getName(), new ObjectArrayList<>()));
            case MEMBER:
                return new HashSet<>(this.members.getOrDefault(player.getName(), new ObjectArrayList<>()));
            default:
                return new HashSet<>();
        }
    }

    public int getPlayerRegionAmount(Player player, RegionGroup group) {
        return this.getPlayersRegionList(player, group).size();
    }
}
