package Sergey_Dertan.SRegionProtector.UI.Form.Type;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.UI.Form.Element.Button;
import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowSimple;

final class MemberRemoveForm extends FormWindowSimple implements UIForm {

    private static final transient RegionManager regionManager = SRegionProtectorMain.getInstance().getRegionManager();
    private final transient Region region;

    MemberRemoveForm(String member, Region region, Player player) {
        super(region.name, "Do u want to remove member " + member + " from " + region.name + "?");
        this.region = region;
        this.addButton(new Button("Yes", MembersForm.class, region, player).setBeforeNext(() -> {
            if (region.isMember(member)) regionManager.removeMember(region, member);
        }));
        this.addButton(new Button("No", MembersForm.class, region, player));
    }

    @Override
    public Region getRegion() {
        return this.region;
    }
}
