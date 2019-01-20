package ysn.com.banner;

import android.content.Context;
import android.view.View;

/**
 * @Author yangsanning
 * @ClassName ISBannerHolder
 * @Description 一句话概括作用
 * @Date 2019/1/18
 * @History 2019/1/18 author: description:
 */
public interface ISBannerHolder<T> {

    /**
     *  创建View
     */
    View createView(Context context);

    /**
     * 绑定数据
     */
    void onBind(Context context, int position, T data);
}