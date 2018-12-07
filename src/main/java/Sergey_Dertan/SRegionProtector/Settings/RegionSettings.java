package Sergey_Dertan.SRegionProtector.Settings;

import Sergey_Dertan.SRegionProtector.BlockEntity.BlockEntityHealer;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import cn.nukkit.Server;
import cn.nukkit.permission.Permissible;
import cn.nukkit.permission.Permission;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class RegionSettings {

    public final boolean[] flagsStatus = new boolean[RegionFlags.FLAG_AMOUNT];
    public final boolean[] defaultFlags = new boolean[RegionFlags.FLAG_AMOUNT];
    public int maxRegionNameLength;
    public int minRegionNameLength;
    public int healFlagHealDelay;
    public int healFlagHealAmount;
    private Map<Long, Permission> regionSize;
    private Map<Integer, Permission> regionAmount;

    RegionSettings(Map<String, Object> cnf, Map<String, Object> rgCnf) {
        this.loadSizePermissions(cnf);
        this.loadAmountPermissions(cnf);
        this.loadFlagsStatuses(cnf);
        this.loadDefaultFlags(rgCnf);
        this.loadHealFlagSettings(rgCnf);
        RegionFlags.init(this.defaultFlags);

        this.maxRegionNameLength = (int) rgCnf.get("max-region-name-length");
        this.minRegionNameLength = (int) rgCnf.get("min-region-name-length");
    }

    private void loadHealFlagSettings(Map<String, Object> cnf) {
        this.healFlagHealDelay = (int) cnf.get("heal-flag-heal-delay");
        this.healFlagHealAmount = (int) cnf.get("heal-flag-heal-amount");

        BlockEntityHealer.HEAL_DELAY = this.healFlagHealDelay;
        BlockEntityHealer.HEAL_AMOUNT = this.healFlagHealAmount;
        BlockEntityHealer.FLAG_ENABLED = this.flagsStatus[RegionFlags.FLAG_HEAL];
    }

    private void loadDefaultFlags(Map<String, Object> rgCnf) {
        for (Map.Entry<String, Boolean> flag : ((Map<String, Boolean>) rgCnf.get("default-flags")).entrySet()) {
            if (RegionFlags.getFlagId(flag.getKey()) == RegionFlags.FLAG_INVALID) continue;
            this.defaultFlags[RegionFlags.getFlagId(flag.getKey())] = flag.getValue();
        }
    }

    public boolean hasSizePermission(Permissible target, long size) {
        if (target.hasPermission("sregionprotector.region.size.*")) return true;
        for (Map.Entry<Long, Permission> perm : this.regionSize.entrySet()) {
            if (perm.getKey() < size) continue;
            if (target.hasPermission(perm.getValue())) return true;
        }
        return false;
    }

    public boolean hasAmountPermission(Permissible target, int amount) {
        if (target.hasPermission("sregionprotector.region.amount.*")) return true;
        for (Map.Entry<Integer, Permission> perm : this.regionAmount.entrySet()) {
            if (perm.getKey() < amount) continue;
            if (target.hasPermission(perm.getValue())) return true;
        }
        return false;
    }

    private void loadSizePermissions(Map<String, Object> cnf) {
        this.regionSize = new Long2ObjectOpenHashMap<>();
        Permission mainPerm = Server.getInstance().getPluginManager().getPermission("sregionprotector.region.size.*");
        for (Integer size : (List<Integer>) cnf.get("region-sizes")) {
            Permission permission = new Permission("sregionprotector.region.size." + size, "Allows to creating regions with size up to " + size + " blocks");
            Server.getInstance().getPluginManager().addPermission(permission);
            //mainPerm.addParent(mainPerm, true); //TODO test
            this.regionSize.put(size.longValue(), permission);
        }
        mainPerm.recalculatePermissibles();
    }

    private void loadAmountPermissions(Map<String, Object> cnf) {
        this.regionAmount = new Int2ObjectOpenHashMap<>();
        Permission mainPerm = Server.getInstance().getPluginManager().getPermission("sregionprotector.region.amount.*");
        for (Integer amount : (List<Integer>) cnf.get("region-amounts")) {
            Permission permission = new Permission("sregionprotector.region.amount." + amount, "Allows to creating up to " + amount + " regions");
            Server.getInstance().getPluginManager().addPermission(permission);
            //mainPerm.addParent(mainPerm, true); //TODO test
            this.regionAmount.put(amount, permission);
        }
        mainPerm.recalculatePermissibles();
    }

    public boolean isFlagEnabled(int id) {
        return this.flagsStatus[id];
    }

    public boolean isFlagEnabled(String name) {
        return this.isFlagEnabled(RegionFlags.getFlagId(name));
    }

    private void loadFlagsStatuses(Map<String, Object> cnf) {
        Arrays.fill(flagsStatus, false);
        for (Map.Entry<String, Boolean> flag : ((Map<String, Boolean>) cnf.get("active-flags")).entrySet()) {
            if (RegionFlags.getFlagId(flag.getKey()) == RegionFlags.FLAG_INVALID) continue;
            this.flagsStatus[RegionFlags.getFlagId(flag.getKey())] = flag.getValue();
        }
    }
}