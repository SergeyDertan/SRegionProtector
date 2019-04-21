package Sergey_Dertan.SRegionProtector.UI.Form.Type;

import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementInput;
import cn.nukkit.form.element.ElementLabel;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindowCustom;

final class SetPriorityForm extends FormWindowCustom implements UIForm {

    private final transient Region region;

    @SuppressWarnings("WeakerAccess")
    SetPriorityForm(Region region, String err) {
        super("Changing priority for " + region.name);
        this.region = region;

        this.addElement(new ElementLabel("Current priority: " + region.getPriority()));
        if (err != null) this.addElement(new ElementLabel(err));
        this.addElement(new ElementInput("Priority", "PRIORITY"));
    }

    @SuppressWarnings("unused")
    SetPriorityForm(Region region) {
        this(region, "");
    }

    @Override
    public UIForm handle(FormResponse response, Player player) {
        if (!player.hasPermission("sregionprotector.admin") && !this.region.isCreator(player.getName())) return null;
        int priority;
        try {
            String priorityStr = null;
            for (int i = 0; i < 3; ++i) {
                priorityStr = ((FormResponseCustom) response).getInputResponse(i);
                if (priorityStr != null) break;
            }
            if (priorityStr == null) throw new RuntimeException();
            priority = Integer.parseInt(priorityStr);
        } catch (RuntimeException e) {
            return UIForm.getInstance(SetPriorityForm.class, this.region, "Wrong priority!");
        }
        this.region.setPriority(priority);
        return UIForm.getInstance(SetPriorityForm.class, this.region);
    }

    @Override
    public Region getRegion() {
        return this.region;
    }
}
