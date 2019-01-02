package core.base.photopicker.beans;

/**
 * Created by 刘红亮 on 2016/4/21.
 * 接收相册或者照相机裁剪来的图片
 */
public class SelectPhotoEvent {

    public final static int STATUS_OK = 1;
    public final static int STATUS_FAIL = 0;
    public final static int STATUS_CANCEL = -1;
    public int actTag;//0 - 编辑页面，1 - 发布页面,2 - 我的上传头像

    public String flag;
    public int status;
    public String imagePath;

    public SelectPhotoEvent(String flag, int status, String imagePath) {
        this.flag = flag;
        this.status = status;
        this.imagePath = imagePath;
    }

    public SelectPhotoEvent() {

    }
}
