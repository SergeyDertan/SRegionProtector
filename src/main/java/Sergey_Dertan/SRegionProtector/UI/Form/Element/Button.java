package Sergey_Dertan.SRegionProtector.UI.Form.Element;

import Sergey_Dertan.SRegionProtector.UI.Form.Type.UIForm;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;

public final class Button extends ElementButton {

    private final transient Class<? extends UIForm> target; //target form
    private final transient Object[] args; //args for creating page instance

    private transient Runnable beforeNext; //do something before opening next page

    private transient boolean noNext = false; //for remove region form, next form won`t be opened

    public Button(String text, Class<? extends UIForm> target, Object... args) {
        super(text);
        this.target = target;
        this.args = args;
    }

    public Button setBeforeNext(Runnable beforeNext) {
        this.beforeNext = beforeNext;
        return this;
    }

    public Button noNext(boolean noNext) {
        this.noNext = noNext;
        return this;
    }

    public UIForm getNext() {
        if (this.beforeNext != null) this.beforeNext.run();
        if (this.noNext) return null;
        return UIForm.getInstance(this.target, this.args);
    }

    public Button setImage(ElementButtonImageData image) {
        this.addImage(image);
        return this;
    }
}
