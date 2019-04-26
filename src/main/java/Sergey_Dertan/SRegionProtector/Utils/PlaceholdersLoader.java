package Sergey_Dertan.SRegionProtector.Utils;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionGroup;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;

public abstract class PlaceholdersLoader {

    static {
        PlaceholderAPI papi = PlaceholderAPI.getInstance();
        SRegionProtectorMain main = SRegionProtectorMain.getInstance();

        papi.visitorSensitivePlaceholder("srp_current_region", (pl, params) -> {
            Region region = main.getChunkManager().getRegion(pl, pl.level.getName());
            return region == null ? "" : region.name;
        });

        papi.visitorSensitivePlaceholder("srp_region_amount_creator", (pl, params) -> main.getRegionManager().getPlayerRegionAmount(pl, RegionGroup.CREATOR));

        papi.visitorSensitivePlaceholder("srp_region_amount_owner", (pl, params) -> main.getRegionManager().getPlayerRegionAmount(pl, RegionGroup.OWNER));

        papi.visitorSensitivePlaceholder("srp_region_amount_member", (pl, params) -> main.getRegionManager().getPlayerRegionAmount(pl, RegionGroup.MEMBER));

        papi.visitorSensitivePlaceholder("srp_region_is_selling", (pl, params) -> {
            Region region = main.getChunkManager().getRegion(pl, pl.level.getName());
            return region != null && region.isSelling();
        });

        papi.visitorSensitivePlaceholder("srp_region_price", (pl, params) -> {
            Region region = main.getChunkManager().getRegion(pl, pl.level.getName());
            return region == null ? -1L : region.getSellFlagPrice();
        });

        papi.visitorSensitivePlaceholder("srp_flag_state", (pl, params) -> {

            Region region = main.getChunkManager().getRegion(pl, pl.level.getName());
            int flag = RegionFlags.FLAG_INVALID;
            String name = params.single();
            if (name != null && !name.isEmpty()) {
                flag = RegionFlags.getFlagId(name);
            }
            if (flag == RegionFlags.FLAG_INVALID || region == null) return "";
            return region.getFlagState(flag) == RegionFlags.getStateFromString("allow", flag) ? "allow" : "deny";
        });
    }

    private PlaceholdersLoader() {
    }
}
