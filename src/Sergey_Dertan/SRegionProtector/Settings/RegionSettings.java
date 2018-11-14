package Sergey_Dertan.SRegionProtector.Settings;

import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import cn.nukkit.Server;
import cn.nukkit.permission.Permissible;
import cn.nukkit.permission.Permission;
import cn.nukkit.utils.ConfigSection;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class RegionSettings {

    public boolean[] flagsStatus;
    public boolean[] defaultFlags;
    private Map<Integer, Permission> regionSize;
    private Map<Integer, Permission> regionAmount;

    RegionSettings(Map<String, Object> cnf, ConfigSection rgCnf) {
        this.loadSizePermissions(cnf);
        this.loadAmountPermissions(cnf);
        this.loadFlagsStatuses(cnf);
        this.loadDefaultFlags(rgCnf);
        RegionFlags.init(this.defaultFlags);
    }

    private void loadDefaultFlags(ConfigSection rgCnf) {
        this.defaultFlags = new boolean[RegionFlags.FLAG_AMOUNT];
        Arrays.fill(this.defaultFlags, false);
        for (Map.Entry<String, Boolean> flag : ((Map<String, Boolean>) rgCnf.get("default-flags")).entrySet()) {
            if (RegionFlags.getFlagId(flag.getKey()) == RegionFlags.FLAG_INVALID) continue;
            this.defaultFlags[RegionFlags.getFlagId(flag.getKey())] = flag.getValue();
        }
    }

    public boolean hasSizePermission(Permissible target, int size) {
        if (target.hasPermission("sregionprotector.region.size.*")) return true;
        for (Map.Entry<Integer, Permission> perm : this.regionSize.entrySet()) {
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
        this.regionSize = new HashMap<>();
        Permission mainPerm = Server.getInstance().getPluginManager().getPermission("sregionprotector.region.size.*");
        for (Integer size : (List<Integer>) cnf.get("region-sizes")) {
            Permission permission = new Permission("sregionprotector.region.size." + size, "Allows to creating regions with size up to " + size + " blocks");
            Server.getInstance().getPluginManager().addPermission(permission);
            //mainPerm.addParent(mainPerm, true); //TODO test
            this.regionSize.put(size, permission);
        }
        mainPerm.recalculatePermissibles();
    }

    private void loadAmountPermissions(Map<String, Object> cnf) {
        this.regionAmount = new HashMap<>();
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
        this.flagsStatus = new boolean[RegionFlags.FLAG_AMOUNT];
        Arrays.fill(flagsStatus, true);
        for (Map.Entry<String, Boolean> flag : ((Map<String, Boolean>) cnf.get("active-flags")).entrySet()) {
            if (RegionFlags.getFlagId(flag.getKey()) == RegionFlags.FLAG_INVALID) continue;
            this.flagsStatus[RegionFlags.getFlagId(flag.getKey())] = flag.getValue();
        }
    }
}