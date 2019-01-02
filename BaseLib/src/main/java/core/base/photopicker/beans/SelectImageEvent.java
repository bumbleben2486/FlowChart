package core.base.photopicker.beans;

import java.util.List;

/**
 */
public class SelectImageEvent {
    public final static int STATUS_OK=1;
    public final static int STATUS_FAIL=0;
    public final static int STATUS_CANCEL=-1;
    public String flag;
    public int status;
    public int tag;//页面来向标签 - 标识哪个页面过来的(只有生活秀编辑与预览发布页面)
    public List<MediaBean> selectMediaBeans;

    public SelectImageEvent(String flag,int tag, int status, List<MediaBean> selectMediaBeans) {
        this.tag = tag;
        this.flag = flag;
        this.status = status;
        this.selectMediaBeans = selectMediaBeans;
    }

    public SelectImageEvent() {

    }
}
