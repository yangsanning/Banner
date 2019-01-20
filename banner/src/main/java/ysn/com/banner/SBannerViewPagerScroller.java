package ysn.com.banner;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import java.lang.reflect.Field;

/**
 * @Author yangsanning
 * @ClassName SBannerViewPagerScroller
 * @Description 由于ViewPager 默认的切换速度有点快，所以自定义一个Scroller 来控制切换的速度。
 * @Date 2019/1/18
 * @History 2019/1/18
 * 实际上ViewPager 切换本来就是用的Scroller 来做的，所以通过反射来获取取到ViewPager 的mScroller 属性
 */
public class SBannerViewPagerScroller extends Scroller {

    /**
     * 越大越慢(ViewPager默认的最大Duration 为600)
     */
    private int duration = 800;
    private boolean isUseDefaultDuration = false;

    public SBannerViewPagerScroller(Context context) {
        super(context);
    }

    public SBannerViewPagerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public SBannerViewPagerScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        super.startScroll(startX, startY, dx, dy, duration);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        super.startScroll(startX, startY, dx, dy, isUseDefaultDuration ? duration : this.duration);
    }

    /**
     * 设置ViewPager的滑动速度
     */
    public void correlation(SBannerViewPager sBannerViewPager) {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(sBannerViewPager, this);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
