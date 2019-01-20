package ysn.com.banner;

import android.content.Context;
import android.util.AttributeSet;

/**
 * @Author yangsanning
 * @ClassName IView
 * @Description 一句话概括作用
 * @Date 2019/1/20
 * @History 2019/1/20 author: description:
 */
public interface IView {

    void initAttrs(Context context, AttributeSet attrs);

    void initView(Context context);

    void initData(Context context);
}
