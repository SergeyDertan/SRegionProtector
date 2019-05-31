package Sergey_Dertan.SRegionProtector.UI.Form.Type;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.UI.Form.Element.Button;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;

final class RemoveRegionForm extends FormWindowSimple implements UIForm {

    private static final transient RegionManager REGION_MANAGER = SRegionProtectorMain.getInstance().getRegionManager();

    private static final transient ElementButtonImageData acceptImg = new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, "textures/ui/confirm.png");
    private static final transient ElementButtonImageData cancelImg = new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, "textures/ui/cancel.png");

    private final transient Region region;

    RemoveRegionForm(Region region, Player player) {
        super("Removing " + region.name, "Remove region " + region.name + " ?");
        this.region = region;

        this.addButton(new Button("Yes", null).setBeforeNext(() -> { //make sure that player still region`s owner
            if (REGION_MANAGER.regionExists(region.name) && (player.hasPermission("sregionprotector.admin") || this.region.isCreator(player.getName()))) {
                REGION_MANAGER.removeRegion(region);
                player.sendMessage("Region " + region.name + " removed");
            }
        }).noNext(true).setImage(acceptImg));
        this.addButton(new Button("No", MainForm.class, region, player).setImage(cancelImg));
    }

    @Override
    public Region getRegion() {
        return this.region;
    }
}
