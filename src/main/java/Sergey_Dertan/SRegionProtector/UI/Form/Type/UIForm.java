package Sergey_Dertan.SRegionProtector.UI.Form.Type;

import Sergey_Dertan.SRegionProtector.Region.Region;
import cn.nukkit.Player;
import cn.nukkit.form.response.FormResponse;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public interface UIForm {

    Class<? extends UIForm> MAIN = MainForm.class;

    static UIForm getInstance(Class<? extends UIForm> clazz, Object... args) {
        for (Constructor constructor : clazz.getDeclaredConstructors()) {
            List<Class<?>> parameters = new ArrayList<>();
            if (constructor.getParameterCount() != args.length) continue;
            int i = -1;
            for (Class<?> param : constructor.getParameterTypes()) {
                if (param.isAssignableFrom(args[++i].getClass())) {
                    parameters.add(param);
                }
            }
            try {
                return clazz.getDeclaredConstructor(parameters.toArray(new Class<?>[0])).newInstance(args);
            } catch (Exception ignore) {
            }
        }
        return null;
    }

    Region getRegion();

    default UIForm handle(FormResponse response, Player player) {
        return this;
    }
}
