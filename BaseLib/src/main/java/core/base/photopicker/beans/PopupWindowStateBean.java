package core.base.photopicker.beans;

/**
 * @Author 陈俊彬
 * @Date 2017/10/19
 */

public class PopupWindowStateBean {

    private boolean isShow;

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public PopupWindowStateBean(boolean isShow) {
        this.isShow = isShow;
    }
}
