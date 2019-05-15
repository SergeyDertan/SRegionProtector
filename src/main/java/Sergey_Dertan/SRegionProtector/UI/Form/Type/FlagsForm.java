package Sergey_Dertan.SRegionProtector.UI.Form.Type;

import Sergey_Dertan.SRegionProtector.Region.Flags.Flag.RegionFlag;
import Sergey_Dertan.SRegionProtector.Region.Flags.RegionFlags;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.UI.Form.Element.Button;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;

import java.util.Arrays;

final class FlagsForm extends FormWindowSimple implements UIForm {

    private static final transient String[] ICONS = new String[RegionFlags.FLAG_AMOUNT];

    static {
        Arrays.fill(ICONS, "textures/misc/missing_texture.png");

        ICONS[RegionFlags.FLAG_PLACE] = "textures/blocks/grass_side_carried.png";
        ICONS[RegionFlags.FLAG_BREAK] = "textures/blocks/grass_side_carried.png";
        ICONS[RegionFlags.FLAG_USE] = "textures/items/lever.png";
        ICONS[RegionFlags.FLAG_PVP] = "textures/items/diamond_sword.png";
        ICONS[RegionFlags.FLAG_EXPLODE] = "textures/blocks/tnt_side.png";
        ICONS[RegionFlags.FLAG_EXPLODE_BLOCK_BREAK] = "textures/blocks/tnt_side.png";
        ICONS[RegionFlags.FLAG_LIGHTER] = "textures/items/flint_and_steel.png";
        //ICONS[RegionFlags.FLAG_LEAVES_DECAY] = BlockID.LEAVE;
        ICONS[RegionFlags.FLAG_ITEM_DROP] = "textures/items/stick.png";
        ICONS[RegionFlags.FLAG_MOB_SPAWN] = "textures/items/egg_creeper.png";
        ICONS[RegionFlags.FLAG_CROPS_DESTROY] = "textures/blocks/farmland_wet.png";
        ICONS[RegionFlags.FLAG_REDSTONE] = "textures/items/redstone_dust.png";
        ICONS[RegionFlags.FLAG_ENDER_PEARL] = "textures/items/ender_pearl.png";
        ICONS[RegionFlags.FLAG_FIRE] = "textures/blocks/fire_1_placeholder.png";
        ICONS[RegionFlags.FLAG_LIQUID_FLOW] = "textures/blocks/water_placeholder.png";
        ICONS[RegionFlags.FLAG_CHEST_ACCESS] = "textures/blocks/chest_front.png";
        ICONS[RegionFlags.FLAG_SLEEP] = "textures/items/bed_red.png";
        ICONS[RegionFlags.FLAG_SMART_DOORS] = "textures/items/door_iron.png";
        ICONS[RegionFlags.FLAG_MINEFARM] = "textures/blocks/redstone_ore.png";
        ICONS[RegionFlags.FLAG_POTION_LAUNCH] = "textures/items/potion_bottle_splash_healthBoost.png";
        ICONS[RegionFlags.FLAG_HEAL] = "textures/ui/regeneration_effect.png";
        ICONS[RegionFlags.FLAG_NETHER_PORTAL] = "textures/ui/NetherPortal.png";
        ICONS[RegionFlags.FLAG_SELL] = "textures/items/emerald.png";
        ICONS[RegionFlags.FLAG_SEND_CHAT] = "textures/ui/betaIcon.png";
        ICONS[RegionFlags.FLAG_RECEIVE_CHAT] = "textures/ui/betaIcon.png";
        ICONS[RegionFlags.FLAG_FRAME_ITEM_DROP] = "textures/items/item_frame.png";
    }

    private final transient Region region;

    FlagsForm(Region region, Player player) {
        super(region.name + "`s flags", "");
        this.region = region;
        int i = 0;
        for (RegionFlag flag : region.getFlags()) {
            String str = RegionFlags.getFlagName(i).replace("-", " ") + ": ";
            str = str.concat(flag.state == RegionFlags.getStateFromString("allow", i) ? "allow" : "deny");
            Runnable beforeNext = null;
            if (i != RegionFlags.FLAG_SELL && i != RegionFlags.FLAG_TELEPORT) {
                int n = i;
                beforeNext = () -> {
                    if (RegionFlags.hasFlagPermission(player, n)) region.setFlagState(n, !region.getFlagState(n));
                };
            }
            ElementButtonImageData image = new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, ICONS[i]);
            this.addButton(new Button(str, FlagsForm.class, region, player).setBeforeNext(beforeNext).setImage(image));
            ++i;
        }
        this.addButton(new Button("Back", MainForm.class, region, player));
    }

    @Override
    public Region getRegion() {
        return this.region;
    }
}
