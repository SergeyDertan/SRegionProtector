package Sergey_Dertan.SRegionProtector.Utils;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionGroup;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;

//do not load this class if placeholder api isn`t installed
public abstract class PlaceholdersLoader {

    static {
        PlaceholderAPI papi = PlaceholderAPI.getInstance();
        SRegionProtectorMain main = SRegionProtectorMain.getInstance();

        papi.builder("srp_current_region", String.class).visitorLoader(entry -> {
            Region region = main.getChunkManager().getRegion(entry.getPlayer(), entry.getPlayer().level.getName());
            return region == null ? "" : region.name;
        }).autoUpdate(false).build();

        papi.builder("srp_region_amount_creator", String.class).visitorLoader(entry ->
                String.valueOf(main.getRegionManager().getPlayerRegionAmount(entry.getPlayer(), RegionGroup.CREATOR))
        ).autoUpdate(false).build();

        papi.builder("srp_region_amount_owner", String.class).visitorLoader(entry ->
                String.valueOf(main.getRegionManager().getPlayerRegionAmount(entry.getPlayer(), RegionGroup.OWNER))
        ).autoUpdate(false).build();

        papi.builder("srp_region_amount_member", String.class).visitorLoader(entry ->
                String.valueOf(main.getRegionManager().getPlayerRegionAmount(entry.getPlayer(), RegionGroup.MEMBER))
        ).autoUpdate(false).build();

        papi.builder("srp_region_is_selling", String.class).visitorLoader(entry -> {
            Region region = main.getChunkManager().getRegion(entry.getPlayer(), entry.getPlayer().level.getName());
            return String.valueOf(region != null && region.isSelling());
        }).autoUpdate(false).build();

        papi.builder("srp_region_price", String.class).visitorLoader(entry -> {
            Region region = main.getChunkManager().getRegion(entry.getPlayer(), entry.getPlayer().level.getName());
            return String.valueOf(region == null ? -1L : region.getSellFlagPrice());
        }).autoUpdate(false).build();

        papi.builder("srp_flag_state", String.class).visitorLoader(entry -> {
            Region region = main.getChunkManager().getRegion(entry.getPlayer(), entry.getPlayer().level.getName());
            int flag = RegionFlags.FLAG_INVALID;
            String name = entry.getParameters().single().getValue();
            if (name != null && !name.isEmpty()) {
                flag = RegionFlags.getFlagId(name);
            }
            if (flag == RegionFlags.FLAG_INVALID || region == null) return "";
            return region.getFlagState(flag) == RegionFlags.getStateFromString("allow", flag) ? "allow" : "deny";
        }).autoUpdate(false).build();
    }

    private PlaceholdersLoader() {
    }
}
