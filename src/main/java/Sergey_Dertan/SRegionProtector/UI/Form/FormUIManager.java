package Sergey_Dertan.SRegionProtector.UI.Form;

import Sergey_Dertan.SRegionProtector.Main.SRegionProtectorMain;
import Sergey_Dertan.SRegionProtector.Region.Region;
import Sergey_Dertan.SRegionProtector.Region.RegionManager;
import Sergey_Dertan.SRegionProtector.UI.Form.Element.Button;
import Sergey_Dertan.SRegionProtector.UI.Form.Type.UIForm;
import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponse;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindow;

public abstract class FormUIManager {

    private static final RegionManager REGION_MANAGER = SRegionProtectorMain.getInstance().getRegionManager();

    public static void open(Player target, Region region) {
        target.showFormWindow((FormWindow) UIForm.getInstance(UIForm.MAIN, region, target));
    }

    public static void handle(Player player, UIForm form) {
        if (!REGION_MANAGER.regionExists(form.getRegion().name)) return;
        FormResponse response = ((FormWindow) form).getResponse();
        if (response instanceof FormResponseSimple) {
            ElementButton btn = ((FormResponseSimple) response).getClickedButton();
            if (btn instanceof Button) {
                UIForm next = ((Button) btn).getNext();
                if (next != null) player.showFormWindow((FormWindow) next);
                return;
            }
        }

        UIForm next = form.handle(((FormWindow) form).getResponse(), player);
        if (next != null) player.showFormWindow((FormWindow) next);
    }
}
