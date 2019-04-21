package Sergey_Dertan.SRegionProtector.UI.Form.Type;

import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.UI.Form.Element.Button;
import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowSimple;

final class MembersForm extends FormWindowSimple implements UIForm {

    private final transient Region region;

    MembersForm(Region region, Player player) {
        super(region.name + "'s members", "");
        this.region = region;

        if (player.hasPermission("sregionprotector.admin") || this.region.isOwner(player.getName(), true)) {
            region.getMembers().forEach(member -> this.addButton(new Button(member, MemberRemoveForm.class, member, region)));
        } else {
            region.getMembers().forEach(member -> this.addButton(new Button(member, MembersForm.class, region, player)));
        }
        this.addButton(new Button("Back", MainForm.class, region, player));
    }

    @Override
    public Region getRegion() {
        return this.region;
    }
}
