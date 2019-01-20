package ysn.com.banner;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @Author yangsanning
 * @ClassName SBannerTransformer
 * @Description 滑动大小
 * @Date 2019/1/18
 * @History 2019/1/18 author: description:
 */
public class SBannerTransformer implements ViewPager.PageTransformer {

    private static final float SCALE_MAX = 1.0f;
    private static final float SCALE_MIN = 0.8f;

    private float reduceX = 0.0f;
    private float itemWidth = 0;
    private int coverWidth;
    private float offsetPosition = 0f;
    private ViewPager viewPager;

    public SBannerTransformer(ViewPager pager) {
        viewPager = pager;
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        if (offsetPosition == 0f) {
            float paddingLeft = viewPager.getPaddingLeft();
            float paddingRight = viewPager.getPaddingRight();
            float width = viewPager.getMeasuredWidth();
            offsetPosition = paddingLeft / (width - paddingLeft - paddingRight);
        }
        float currentPos = position - offsetPosition;
        if (itemWidth == 0) {
            itemWidth = view.getWidth();
            //由于左右边的缩小而减小的x的大小的一半
            reduceX = (2.0f - SCALE_MAX - SCALE_MIN) * itemWidth / 2.0f;
        }
        if (currentPos <= -1.0f) {
            view.setTranslationX(reduceX + coverWidth);
            view.setScaleX(SCALE_MIN);
            view.setScaleY(SCALE_MIN);
        } else if (currentPos <= 1.0) {
            float scale = (SCALE_MAX - SCALE_MIN) * Math.abs(1.0f - Math.abs(currentPos));
            float translationX = currentPos * -reduceX;
            //两个view中间的临界，这时两个view在同一层，左侧View需要往X轴正方向移动覆盖的值()
            if (currentPos <= -0.5) {
                view.setTranslationX(translationX + coverWidth * Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f);
            } else if (currentPos <= 0.0f) {
                view.setTranslationX(translationX);
                //两个view中间的临界，这时两个view在同一层
            } else if (currentPos >= 0.5) {
                view.setTranslationX(translationX - coverWidth * Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f);
            } else {
                view.setTranslationX(translationX);
            }
            view.setScaleX(scale + SCALE_MIN);
            view.setScaleY(scale + SCALE_MIN);
        } else {
            view.setScaleX(SCALE_MIN);
            view.setScaleY(SCALE_MIN);
            view.setTranslationX(-reduceX - coverWidth);
        }
    }
}
