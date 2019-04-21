package Sergey_Dertan.SRegionProtector.UI.Form.Type;

import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.UI.Form.Element.Button;
import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowSimple;

final class MainForm extends FormWindowSimple implements UIForm {

    private final transient Region region;

    MainForm(Region region, Player player) {
        super("Region '" + region.name + "'",
                "Level: " + region.level + "\n" +
                        "Creator: " + region.getCreator() + "\n" +
                        "Priority: " + region.getPriority() + "\n" +
                        "Size: " + Math.round((region.maxX - region.minX) * (region.maxY - region.minY) * (region.maxZ - region.minZ))
        );
        this.region = region;

        this.addButton(new Button("Owners", OwnersForm.class, region, player));
        this.addButton(new Button("Members", MembersForm.class, region, player));
        this.addButton(new Button("Flags", FlagsForm.class, region, player));
        if (player.hasPermission("sregionprotector.admin") || this.region.isCreator(player.getName())) {
            this.addButton(new Button("Sell region", SellRegionForm.class, region));
            this.addButton(new Button("Set priority", SetPriorityForm.class, region));
            this.addButton(new Button("Remove region", RemoveRegionForm.class, region, player));
        }
    }

    @Override
    public Region getRegion() {
        return this.region;
    }
}
