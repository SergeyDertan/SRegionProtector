package Sergey_Dertan.SRegionProtector.Settings;

import Sergey_Dertan.SRegionProtector.BlockEntity.BlockEntityHealer;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import cn.nukkit.Server;
import cn.nukkit.permission.Permissible;
import cn.nukkit.permission.Permission;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.lang.reflect.Field;
import java.util.*;

import static Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags.FLAG_AMOUNT;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class RegionSettings {

    public final boolean[] flagsStatus = new boolean[FLAG_AMOUNT];
    public final boolean[] defaultFlags = new boolean[FLAG_AMOUNT];
    public final boolean[] needMessage = new boolean[FLAG_AMOUNT]; //check if player will see the message

    public final boolean[] display = new boolean[FLAG_AMOUNT]; //check if flag should be shown in the info command

    public final int maxRegionNameLength;
    public final int minRegionNameLength;

    public int healFlagHealDelay;
    public int healFlagHealAmount;

    private Long2ObjectMap<Permission> regionSize;
    private Int2ObjectMap<Permission> regionAmount;

    RegionSettings(Map<String, Object> cnf, Map<String, Object> rgCnf) {
        this.loadSizePermissions(cnf);
        this.loadAmountPermissions(cnf);
        this.loadFlagsStatuses(cnf);
        this.loadDefaultFlags(rgCnf);
        this.loadHealFlagSettings(rgCnf);
        this.loadMessages(rgCnf);
        this.loadDisplaySettings(cnf);
        RegionFlags.init(this.defaultFlags);

        this.maxRegionNameLength = ((Number) rgCnf.get("max-region-name-length")).intValue();
        this.minRegionNameLength = ((Number) rgCnf.get("min-region-name-length")).intValue();
    }

    @SuppressWarnings("unchecked")
    private void loadDisplaySettings(Map<String, Object> cnf) {
        Arrays.fill(this.display, true);
        for (Map.Entry<String, Boolean> flag : ((Map<String, Boolean>) cnf.get("display")).entrySet()) {
            if (RegionFlags.getFlagId(flag.getKey()) == RegionFlags.FLAG_INVALID) continue;
            this.display[RegionFlags.getFlagId(flag.getKey())] = flag.getValue();
        }
    }

    @SuppressWarnings("unchecked")
    private void loadMessages(Map<String, Object> rgCnf) {
        Arrays.fill(this.needMessage, false);
        for (Map.Entry<String, Boolean> flag : ((Map<String, Boolean>) rgCnf.get("need-message")).entrySet()) {
            if (RegionFlags.getFlagId(flag.getKey()) == RegionFlags.FLAG_INVALID) continue;
            this.needMessage[RegionFlags.getFlagId(flag.getKey())] = flag.getValue();
        }
    }

    private void loadHealFlagSettings(Map<String, Object> cnf) {
        this.healFlagHealDelay = ((Number) cnf.get("heal-flag-heal-delay")).intValue();
        this.healFlagHealAmount = ((Number) cnf.get("heal-flag-heal-amount")).intValue();

        BlockEntityHealer.HEAL_DELAY = this.healFlagHealDelay;
        BlockEntityHealer.HEAL_AMOUNT = this.healFlagHealAmount;
        BlockEntityHealer.FLAG_ENABLED = this.flagsStatus[RegionFlags.FLAG_HEAL];
    }

    @SuppressWarnings("unchecked")
    private void loadDefaultFlags(Map<String, Object> rgCnf) {
        for (Map.Entry<String, Boolean> flag : ((Map<String, Boolean>) rgCnf.get("default-flags")).entrySet()) {
            if (RegionFlags.getFlagId(flag.getKey()) == RegionFlags.FLAG_INVALID) continue;
            this.defaultFlags[RegionFlags.getFlagId(flag.getKey())] = flag.getValue();
        }
    }

    public boolean hasSizePermission(Permissible target, long size) {
        if (target.hasPermission("sregionprotector.region.size.*")) return true;
        for (Long2ObjectMap.Entry<Permission> perm : this.regionSize.long2ObjectEntrySet()) {
            if (perm.getLongKey() < size) continue;
            if (target.hasPermission(perm.getValue())) return true;
        }
        return false;
    }

    public boolean hasAmountPermission(Permissible target, int amount) {
        if (target.hasPermission("sregionprotector.region.amount.*")) return true;
        for (Int2ObjectMap.Entry<Permission> perm : this.regionAmount.int2ObjectEntrySet()) {
            if (perm.getIntKey() < amount) continue;
            if (target.hasPermission(perm.getValue())) return true;
        }
        return false;
    }

    @SuppressWarnings("Duplicates")
    private void loadSizePermissions(Map<String, Object> cnf) {
        this.regionSize = new Long2ObjectOpenHashMap<>();
        Permission mainPerm = Server.getInstance().getPluginManager().getPermission("sregionprotector.region.size.*");
        @SuppressWarnings("unchecked")
        List<Integer> list = (List<Integer>) cnf.get("region-sizes");
        list.sort(Comparator.comparingInt(Integer::intValue));
        Map<String, Boolean> children = new HashMap<>();
        List<Permission> permissions = new ArrayList<>();
        for (Integer size : list) {
            Permission permission = new Permission("sregionprotector.region.size." + size, "Allows to creating regions with size up to " + size + " blocks", mainPerm.getDefault(), new HashMap<>(children));
            Server.getInstance().getPluginManager().addPermission(permission);
            this.regionSize.put(size.longValue(), permission);
            children.put(permission.getName(), true);
            permissions.add(permission);
        }
        try {
            Field field = mainPerm.getClass().getDeclaredField("children");
            boolean accessible = field.isAccessible();
            if (!accessible) {
                field.setAccessible(true);
            }
            field.set(mainPerm, children);
            field.setAccessible(accessible);
        } catch (NoSuchFieldException | IllegalAccessException ignore) {
        }
        permissions.forEach(Permission::recalculatePermissibles);
        mainPerm.recalculatePermissibles();
    }

    @SuppressWarnings("Duplicates")
    private void loadAmountPermissions(Map<String, Object> cnf) {
        this.regionAmount = new Int2ObjectOpenHashMap<>();
        Permission mainPerm = Server.getInstance().getPluginManager().getPermission("sregionprotector.region.amount.*");
        @SuppressWarnings("unchecked")
        List<Integer> list = (List<Integer>) cnf.get("region-amounts");
        list.sort(Comparator.comparingInt(Integer::intValue));
        Map<String, Boolean> children = new HashMap<>();
        List<Permission> permissions = new ArrayList<>();
        for (Integer amount : list) {
            Permission permission = new Permission("sregionprotector.region.amount." + amount, "Allows to creating up to " + amount + " regions", mainPerm.getDefault(), new HashMap<>(children));
            Server.getInstance().getPluginManager().addPermission(permission);
            this.regionAmount.put((int) amount, permission);
            children.put(permission.getName(), true);
            permissions.add(permission);
        }
        try {
            Field field = mainPerm.getClass().getDeclaredField("children");
            boolean accessible = field.isAccessible();
            if (!accessible) {
                field.setAccessible(true);
            }
            field.set(mainPerm, children);
            field.setAccessible(accessible);
        } catch (NoSuchFieldException | IllegalAccessException ignore) {
        }
        permissions.forEach(Permission::recalculatePermissibles);
        mainPerm.recalculatePermissibles();
    }

    public boolean isFlagEnabled(int id) {
        return this.flagsStatus[id];
    }

    public boolean isFlagEnabled(String name) {
        return this.isFlagEnabled(RegionFlags.getFlagId(name));
    }

    @SuppressWarnings("unchecked")
    private void loadFlagsStatuses(Map<String, Object> cnf) {
        Arrays.fill(this.flagsStatus, false);
        for (Map.Entry<String, Boolean> flag : ((Map<String, Boolean>) cnf.get("active-flags")).entrySet()) {
            if (RegionFlags.getFlagId(flag.getKey()) == RegionFlags.FLAG_INVALID) continue;
            this.flagsStatus[RegionFlags.getFlagId(flag.getKey())] = flag.getValue();
        }
    }
}
