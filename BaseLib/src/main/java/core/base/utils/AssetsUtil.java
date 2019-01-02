package core.base.utils;

import android.content.Context;

/**
 * 操作安装包中"assets"文件夹中的文件
 * 
 * @author ben
 *
 */
public class AssetsUtil {

	public static String readText(Context context, String assetPath) {
		try {
			return ConvertUtils.toString(context.getAssets().open(assetPath));
		} catch (Exception e) {
			return "";
		}
	}

}
