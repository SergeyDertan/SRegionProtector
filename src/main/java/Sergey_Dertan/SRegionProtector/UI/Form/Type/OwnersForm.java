package Sergey_Dertan.SRegionProtector.UI.Form.Type;

import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.UI.Form.Element.Button;
import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowSimple;

final class OwnersForm extends FormWindowSimple implements UIForm {

    private final transient Region region;

    OwnersForm(Region region, Player player) {
        super(region.name + "'s owners", "");
        this.region = region;

        if (player.hasPermission("sregionprotector.admin") || this.region.isCreator(player.getName())) {
            region.getOwners().forEach(owner -> this.addButton(new Button(owner, OwnerRemoveForm.class, owner, region, player)));
        } else {
            region.getOwners().forEach(owner -> this.addButton(new Button(owner, OwnersForm.class, region, player)));
        }
        this.addButton(new Button("Back", MainForm.class, region, player));
    }

    @Override
    public Region getRegion() {
        return this.region;
    }
}
