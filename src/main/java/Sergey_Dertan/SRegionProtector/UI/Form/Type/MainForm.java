package Sergey_Dertan.SRegionProtector.UI.Form.Type;

import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.UI.Form.Element.Button;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;

final class MainForm extends FormWindowSimple implements UIForm {

    private static final transient ElementButtonImageData priorityImage = new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, "textures/ui/move.png");
    private static final transient ElementButtonImageData unknownImage = new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, "textures/ui/permissions_member_star.png");
    private static final transient ElementButtonImageData removeImage = new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, "textures/gui/newgui/storage/trash.png");
    private static final transient ElementButtonImageData ownersImage = new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, "textures/ui/permissions_op_crown.png");
    private static final transient ElementButtonImageData flagsImage = new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, "textures/gui/newgui/settings/toggle_on_hover.png");
    private static final transient ElementButtonImageData sellImage = new ElementButtonImageData(ElementButtonImageData.IMAGE_DATA_TYPE_PATH, "textures/gui/newgui/MCoin.png");

    private final transient Region region;

    MainForm(Region region, Player player) {
        super("Region '" + region.name + "'",
                "Level: " + region.level + "\n" +
                        "Creator: " + region.getCreator() + "\n" +
                        "Priority: " + region.getPriority() + "\n" +
                        "Size: " + region.size
        );
        this.region = region;

        this.addButton(new Button("Owners", OwnersForm.class, region, player).setImage(ownersImage));
        this.addButton(new Button("Members", MembersForm.class, region, player).setImage(unknownImage));
        this.addButton(new Button("Flags", FlagsForm.class, region, player).setImage(flagsImage));
        if (player.hasPermission("sregionprotector.admin") || this.region.isCreator(player.getName())) {
            this.addButton(new Button("Sell region", SellRegionForm.class, region).setImage(sellImage));
            this.addButton(new Button("Set priority", SetPriorityForm.class, region).setImage(priorityImage));
            this.addButton(new Button("Remove region", RemoveRegionForm.class, region, player).setImage(removeImage));
        }
    }

    @Override
    public Region getRegion() {
        return this.region;
    }
}
