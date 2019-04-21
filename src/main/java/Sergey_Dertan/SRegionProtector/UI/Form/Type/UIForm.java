package Sergey_Dertan.SRegionProtector.UI.Form.Type;

import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;

import java.util.ArrayList;
import java.util.List;

public interface UIForm {

    Class<? extends UIForm> MAIN = MainForm.class;

    static UIForm getInstance(Class<? extends UIForm> clazz, Object... args) {
        try {
            List<Class<?>> parametersTypes = new ArrayList<>();
            for (Object obj : args) {
                parametersTypes.add(obj.getClass());
            }
            return clazz.getDeclaredConstructor(parametersTypes.toArray(new Class<?>[0])).newInstance(args);
        } catch (Exception e) {
            return null;
        }
    }

    Region getRegion();

    default UIForm handle(FormResponse response, Player player) {
        return this;
    }
}
