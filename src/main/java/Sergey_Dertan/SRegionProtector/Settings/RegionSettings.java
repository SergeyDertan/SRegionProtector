package Sergey_Dertan.SRegionProtector.Settings;

import Sergey_Dertan.SRegionProtector.BlockEntity.BlockEntityHealer;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import cn.nukkit.permission.Permissible;
import cn.nukkit.permission.PermissionAttachmentInfo;

import java.util.Arrays;
import java.util.Map;

import static Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags.FLAG_AMOUNT;

@SuppressWarnings({"WeakerAccess", "unused"})
public final class RegionSettings {

    public final boolean[] flagsStatus = new boolean[FLAG_AMOUNT];
    public final boolean[] defaultFlags = new boolean[FLAG_AMOUNT]; //default flag value while creating region
    public final boolean[] needMessage = new boolean[FLAG_AMOUNT]; //check if player will see the message

    public final boolean[] display = new boolean[FLAG_AMOUNT]; //check if flag should be shown in the info command

    public final boolean saveNewFlags; //if true after adding new flag to the region it will be saved

    public final int maxRegionNameLength;
    public final int minRegionNameLength;

    public final int defaultAmount; //max region amount which doesnt required any permission
    public final long defaultSize; //max region size which doesnt required any permission

    public int healFlagHealDelay; //in ticks, 1 second = 20 ticks
    public int healFlagHealAmount;

    RegionSettings(Map<String, Object> cnf, Map<String, Object> rgCnf) {
        this.loadFlagsStatuses(cnf);
        this.loadDefaultFlags(rgCnf);
        this.loadHealFlagSettings(rgCnf);
        this.loadMessages(rgCnf);
        this.loadDisplaySettings(cnf);
        RegionFlags.init(this.defaultFlags);

        this.saveNewFlags = (boolean) cnf.get("save-new-flags");

        this.maxRegionNameLength = ((Number) rgCnf.get("max-region-name-length")).intValue();
        this.minRegionNameLength = ((Number) rgCnf.get("min-region-name-length")).intValue();

        this.defaultAmount = ((Number) cnf.get("default-max-region-amount")).intValue();
        this.defaultSize = ((Number) cnf.get("default-max-region-size")).longValue();
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
        if (size <= defaultSize || target.hasPermission("sregionprotector.region.size.*")) return true;

        for (PermissionAttachmentInfo perm : target.getEffectivePermissions().values()) {
            if (!perm.getPermission().startsWith("sregionprotector.region.size.")) continue;
            try {
                long max = Long.parseLong(perm.getPermission().replace("sregionprotector.region.size.", ""));
                if (max >= size) return true;
            } catch (NumberFormatException ignore) {
            }
        }
        return false;
    }

    public boolean hasAmountPermission(Permissible target, int amount) {
        if (amount <= defaultAmount || target.hasPermission("sregionprotector.region.amount.*")) return true;

        for (PermissionAttachmentInfo perm : target.getEffectivePermissions().values()) {
            if (!perm.getPermission().startsWith("sregionprotector.region.amount.")) continue;
            try {
                int max = Integer.parseInt(perm.getPermission().replace("sregionprotector.region.amount.", ""));
                if (max >= amount) return true;
            } catch (NumberFormatException ignore) {
            }
        }
        return false;
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
