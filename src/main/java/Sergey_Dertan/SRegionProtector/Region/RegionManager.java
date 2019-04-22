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
import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Logger;
import cn.nukkit.utils.TextFormat;
import com.alibaba.fastjson.JSON;
import it.unimi.dsi.fastutil.objects.Object2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags.FLAG_AMOUNT;
import static Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags.fixMissingFlags;

@SuppressWarnings("unused")
public final class RegionManager {

    private final DataProvider provider;
    private final Map<String, Region> regions;
    private final Logger logger;
    private final ChunkManager chunkManager;
    private final Map<String, Set<Region>> owners;
    private final Map<String, Set<Region>> members;
    private final Messenger messenger;

    public RegionManager(DataProvider provider, Logger logger, ChunkManager chunkManager) {
        this.provider = provider;
        this.logger = logger;
        this.chunkManager = chunkManager;
        this.messenger = Messenger.getInstance();

        this.regions = new Object2ObjectAVLTreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.owners = new Object2ObjectAVLTreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.members = new Object2ObjectAVLTreeMap<>(String.CASE_INSENSITIVE_ORDER);
    }

    public Map<String, Region> getRegions() {
        Map<String, Region> regions = new Object2ObjectAVLTreeMap<>(String.CASE_INSENSITIVE_ORDER);
        regions.putAll(this.regions);
        return regions;
    }

    public synchronized boolean regionExists(String name) {
        if (name.replace(" ", "").isEmpty()) return false;
        return this.regions.containsKey(name);
    }

    public void init(boolean saveNewFlags) {
        List<RegionDataObject> regions = this.provider.loadRegionList();
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

            int priority = rdo.priority;

            String[] owners;
            String[] members;

            try {
                owners = JSON.parseArray(rdo.owners, String.class).toArray(new String[0]);
                members = JSON.parseArray(rdo.members, String.class).toArray(new String[0]);
            } catch (Exception e) {
                this.logger.warning(TextFormat.YELLOW + this.messenger.getMessage("loading.error.regions", "@region", name));
                this.logger.warning(cn.nukkit.utils.Utils.getExceptionMessage(e));
                continue;
            }

            FlagListDataObject flags = this.provider.loadFlags(name);

            List<RegionFlag> flagList = Converter.fromDataObject(flags);

            boolean needUpdate = false;
            if (flagList.size() < FLAG_AMOUNT) {
                if (saveNewFlags) needUpdate = true;
                fixMissingFlags(flagList);
            }

            Region region = new Region(name, creator, level, priority, minX, minY, minZ, maxX, maxY, maxZ, owners, members, flagList.toArray(new RegionFlag[0]));

            region.needUpdate = needUpdate;

            this.regions.put(name, region);

            for (String user : owners) this.owners.computeIfAbsent(user, (usr) -> new ObjectArraySet<>()).add(region);

            for (String user : members) this.members.computeIfAbsent(user, (usr) -> new ObjectArraySet<>()).add(region);

            this.owners.computeIfAbsent(region.getCreator(), (usr) -> new ObjectArraySet<>()).add(region);

            this.chunkManager.getRegionChunks(
                    new Vector3(minX, minY, minZ),
                    new Vector3(maxX, maxY, maxZ),
                    level, true
            ).forEach(chunk -> {
                chunk.addRegion(region);
                region.addChunk(chunk);
            });
        }

        this.logger.info(TextFormat.GREEN + this.messenger.getMessage("loading.regions.success", "@count", Integer.toString(this.regions.size())));
        this.logger.info(TextFormat.GREEN + this.messenger.getMessage("loading.chunks.success", "@count", Integer.toString(this.chunkManager.getChunkAmount())));
    }

    public synchronized Region createRegion(String name, String creator, Vector3 pos1, Vector3 pos2, Level level) {
        if (this.regions.containsKey(name)) return null;
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
        this.owners.computeIfAbsent(creator, (s) -> new ObjectArraySet<>()).add(region);
        this.regions.put(name, region);

        Vector3 pos = region.getHealerVector();

        new BlockEntityHealer(
                level.getChunk((int) pos.x >> 4, (int) pos.z >> 4, true),
                BlockEntityHealer.getDefaultNBT(pos, region.name)
        );
        region.needUpdate = true;
        return region;
    }

    private synchronized void clearUsers(Region region) {
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
        }
    }

    public synchronized void changeRegionOwner(Region region, String newOwner) {
        synchronized (region.lock) {
            this.clearUsers(region);

            region.clearUsers();

            this.owners.computeIfAbsent(newOwner, (s) -> new ObjectArraySet<>()).add(region);
            region.setCreator(newOwner);
            region.setSellFlagState(-1L, false);
        }
    }

    public synchronized void removeRegion(Region region) {
        synchronized (region.lock) {
            this.clearUsers(region);

            region.getChunks().forEach(chunk -> chunk.removeRegion(region));

            this.regions.remove(region.name);
            this.provider.remove(region);

            if (region.getHealerBlockEntity() != null) region.getHealerBlockEntity().close();
        }
    }

    public boolean checkOverlap(Vector3 pos1, Vector3 pos2, String level, String creator, boolean checkSellFlag, Region self) {
        AxisAlignedBB bb = new SimpleAxisAlignedBB(pos1, pos2);

        for (Chunk chunk : this.chunkManager.getRegionChunks(pos1, pos2, level, false)) {
            for (Region region : chunk.getRegions()) {
                if (region == self || !region.intersectsWith(bb)) continue;
                if (checkSellFlag && region.getFlagState(RegionFlags.FLAG_SELL)) return true;
                if (region.isCreator(creator)) continue;
                return true;
            }
        }
        return false;
    }

    public boolean checkOverlap(Vector3 pos1, Vector3 pos2, String level, String creator, boolean checkSellFlag) {
        return this.checkOverlap(pos1, pos2, level, creator, checkSellFlag, null);
    }

    public boolean checkOverlap(Vector3 pos1, Vector3 pos2, String level, String player) {
        return this.checkOverlap(pos1, pos2, level, player, false);
    }

    public synchronized void addMember(Region region, String target) {
        synchronized (region.lock) {
            this.members.computeIfAbsent(target, (usr) -> new ObjectArraySet<>()).add(region);
            region.addMember(target);
        }
    }

    public synchronized void addOwner(Region region, String target) {
        synchronized (region.lock) {
            this.owners.computeIfAbsent(target, (usr) -> new ObjectArraySet<>()).add(region);
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
        AtomicInteger amount = new AtomicInteger();
        this.regions.values().stream().filter(Region::needUpdate).forEach(region -> {
            synchronized (region.lock) {
                this.provider.save(region);
                region.needUpdate = false;
                amount.incrementAndGet();
            }
        });
        switch (saveType) {
            case AUTO:
                this.logger.info(TextFormat.GREEN + this.messenger.getMessage("regions-auto-save", "@amount", amount.toString()));
                break;
            case DISABLING:
                this.logger.info(TextFormat.GREEN + this.messenger.getMessage("disabling.regions-saved", "@amount", Integer.toString(this.regions.size())));
                break;
            case MANUAL:
                this.logger.info(TextFormat.GREEN + this.messenger.getMessage("regions-manual-save", new String[]{"@amount", "@initiator"}, new String[]{Integer.toString(this.regions.size()), initiator}));
                break;
        }
    }

    public synchronized void save(SRegionProtectorMain.SaveType saveType) {
        this.save(saveType, null);
    }

    public synchronized List<Region> getPlayersRegionList(Player player, RegionGroup group) {
        switch (group) {
            case CREATOR:
                return this.owners.getOrDefault(player.getName(), Collections.emptySet()).stream().filter(region -> region.isCreator(player.getName())).collect(Collectors.toList());
            case OWNER:
                return new ArrayList<>(this.owners.getOrDefault(player.getName(), Collections.emptySet()));
            case MEMBER:
                return new ArrayList<>(this.members.getOrDefault(player.getName(), Collections.emptySet()));
            default:
                return Collections.emptyList();
        }
    }

    public int getPlayerRegionAmount(Player player, RegionGroup group) {
        return this.getPlayersRegionList(player, group).size();
    }
}
