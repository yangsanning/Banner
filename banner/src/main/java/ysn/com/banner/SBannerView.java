package ysn.com.banner;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.AttrRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author yangsanning
 * @ClassName SBannerView
 * @Description 3d轮播控件
 * @Date 2019/1/18
 * @History 2019/1/18 author: description:
 */
public class SBannerView<T> extends RelativeLayout implements IView {

    private static final int INDICATOR_SELECT_ALPHA = 255;

    /**
     * indicatorRes[0] 为为选中，indicatorRes[1]为选中
     */
    private int indicatorNormalAlpha;
    private int indicatorMarginBottom;
    private int[] indicatorRes = new int[]{R.drawable.indicator_normal, R.drawable.indicator_select};
    private ArrayList<ImageView> indicators = new ArrayList<>();

    private List<T> dataList;
    private int currentItem = 0;

    private int marginLeft, marginRight;
    private boolean isOpenLoop = true;
    private boolean isAutoPlay = true;

    private Handler handler = new Handler();
    private int delayedTime = 3000;

    private SBannerViewPager sBannerViewPager;
    private SBannerAdapter sBannerAdapter;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private SBannerAdapter.OnBannerClickListener onBannerClickListener;

    private LinearLayout indicatorLayout;

    private final Runnable loopRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAutoPlay) {
                currentItem = sBannerViewPager.getCurrentItem();
                currentItem++;
                if (currentItem == sBannerAdapter.getCount() - 1) {
                    currentItem = 0;
                    sBannerViewPager.setCurrentItem(currentItem, false);
                } else {
                    sBannerViewPager.setCurrentItem(currentItem);
                }
            }
            handler.postDelayed(this, delayedTime);
        }
    };

    public SBannerView(@NonNull Context context) {
        this(context, null);
    }

    public SBannerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SBannerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SBannerView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr, @StyleRes int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        initAttrs(context, attrs);
        initView(context);
        initData(context);
    }

    @Override
    public void initAttrs(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SBannerView);
        marginLeft = typedArray.getDimensionPixelSize(R.styleable.SBannerView_sbv_margin_left, 80);
        marginRight = typedArray.getDimensionPixelSize(R.styleable.SBannerView_sbv_margin_right, 80);
        indicatorNormalAlpha = typedArray.getInt(R.styleable.SBannerView_sbv_indicator_normal_alpha, 255);
        indicatorMarginBottom = typedArray.getDimensionPixelSize(R.styleable.SBannerView_sbv_indicator_margin_bottom, 50);
        typedArray.recycle();
    }

    @Override
    public void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_sbanner_view, this, true);
        indicatorLayout = view.findViewById(R.id.sbanner_view_indicator_layout);
        sBannerViewPager = view.findViewById(R.id.sbanner_view_sbanner_view_pager);
        sBannerViewPager.setOffscreenPageLimit(4);
    }

    @Override
    public void initData(Context context) {
        MarginLayoutParams params = (MarginLayoutParams) indicatorLayout.getLayoutParams();
        params.bottomMargin = indicatorMarginBottom;
        indicatorLayout.setLayoutParams(params);

        // 控制ViewPager滑动速度的Scroller
        SBannerViewPagerScroller sBannerViewPagerScroller = new SBannerViewPagerScroller(context);
        sBannerViewPagerScroller.correlation(sBannerViewPager);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isOpenLoop) {
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            // 按住Banner的时候，停止自动轮播
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_DOWN:
                int paddingLeft = sBannerViewPager.getLeft();
                float touchX = ev.getRawX();
                if (touchX >= paddingLeft && touchX < getScreenWidth(getContext()) - paddingLeft) {
                    pause();
                }
                break;
            case MotionEvent.ACTION_UP:
                start();
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 初始化Banner类型
     * size为1,不轮播且铺满,且不显示指示器
     */
    private void initType() {
        MarginLayoutParams params = (MarginLayoutParams) sBannerViewPager.getLayoutParams();
        if (dataList.size() == 1) {
            isOpenLoop = isAutoPlay = false;
            indicatorLayout.setVisibility(GONE);
            params.setMargins(0, 0, 0, 0);
        } else {
            isOpenLoop = isAutoPlay = true;
            indicatorLayout.setVisibility(VISIBLE);
            params.setMargins(marginLeft, 0, marginRight, 0);
        }
        sBannerViewPager.setLayoutParams(params);
    }

    /**
     * 初始化指示器Indicator
     */
    private void initIndicator() {
        indicatorLayout.removeAllViews();
        indicators.clear();
        for (int i = 0; i < dataList.size(); i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setPadding(6, 0, 6, 0);
            if (i == (currentItem % dataList.size())) {
                updateIndicatorBg(imageView, INDICATOR_SELECT_ALPHA, 1);
            } else {
                updateIndicatorBg(imageView, indicatorNormalAlpha, 0);
            }

            indicators.add(imageView);
            indicatorLayout.addView(imageView);
        }
    }

    private void updateIndicatorBg(ImageView imageView, @IntRange(from = 0, to = 255) int alpha,
                                   @IntRange(from = 0, to = 1) int position) {
        imageView.setAlpha(alpha);
        imageView.setImageResource(indicatorRes[position]);
    }

    /**
     * 设置数据，这是最重要的一个方法。
     * <p>其他的配置应该在这个方法之前调用</p>
     *  @param datas Banner 展示的数据集合
     * @param bannerHolderCreator ViewHolder生成器 {@link ISBannerHolderCreator} And {@link ISBannerHolder}
     */
    public SBannerView<T> setPages(List<T> datas, ISBannerHolderCreator bannerHolderCreator) {
        if (datas != null && bannerHolderCreator != null) {
            dataList = datas;

            pause();

            // 初始化Banner类型
            initType();

            sBannerViewPager.setPageTransformer(true, new SBannerTransformer(sBannerViewPager));

            // 将Indicator初始化放在Adapter的初始化之前，解决更新数据变化更新时crush.
            initIndicator();

            sBannerAdapter = new SBannerAdapter(datas, bannerHolderCreator, isOpenLoop);
            sBannerAdapter.setUpViewViewPager(sBannerViewPager);
            sBannerAdapter.setPageClickListener(onBannerClickListener);

            sBannerViewPager.clearOnPageChangeListeners();
            sBannerViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    int realPosition = position % indicators.size();
                    if (onPageChangeListener != null) {
                        onPageChangeListener.onPageScrolled(realPosition, positionOffset, positionOffsetPixels);
                    }
                }

                @Override
                public void onPageSelected(int position) {
                    currentItem = position;

                    // 切换indicator
                    int realPosition = currentItem % indicators.size();
                    ImageView imageView;
                    for (int i = 0; i < dataList.size(); i++) {
                        imageView = indicators.get(i);
                        if (i == realPosition) {
                            updateIndicatorBg(imageView, INDICATOR_SELECT_ALPHA, 1);
                        } else {
                            updateIndicatorBg(imageView, indicatorNormalAlpha, 0);
                        }
                    }
                    // 不能直接将onPageChangeListener 设置给ViewPager ,否则拿到的position 是原始的position
                    if (onPageChangeListener != null) {
                        onPageChangeListener.onPageSelected(realPosition);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                    switch (state) {
                        case ViewPager.SCROLL_STATE_DRAGGING:
                            isAutoPlay = false;
                            break;
                        case ViewPager.SCROLL_STATE_SETTLING:
                            isAutoPlay = true;
                            break;
                        default:
                            break;

                    }
                    if (onPageChangeListener != null) {
                        onPageChangeListener.onPageScrollStateChanged(state);
                    }
                }
            });
        }
        return this;
    }

    /**
     * 设置是否可以轮播
     */
    public SBannerView<T> setOpenLoop(boolean openLoop) {
        isOpenLoop = openLoop;
        if (!openLoop) {
            pause();
        }
        return this;
    }

    /**
     * 设置BannerView 的切换时间间隔
     */
    public SBannerView<T> setDelayedTime(int delayedTime) {
        this.delayedTime = delayedTime;
        return this;
    }

    /**
     * 是否显示Indicator
     *
     * @param visible true 显示Indicator，否则不显示
     */
    public SBannerView<T> setIndicatorVisible(boolean visible) {
        if (visible) {
            indicatorLayout.setVisibility(VISIBLE);
        } else {
            indicatorLayout.setVisibility(GONE);
        }
        return this;
    }

    /**
     * 设置indicator 图片资源
     *
     * @param unSelectRes 未选中状态资源图片
     * @param selectRes   选中状态资源图片
     */
    public SBannerView<T> setIndicatorRes(@DrawableRes int unSelectRes, @DrawableRes int selectRes) {
        indicatorRes[0] = unSelectRes;
        indicatorRes[1] = selectRes;
        return this;
    }

    public SBannerView<T> addPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        return this;
    }

    /**
     * 添加Page点击事件
     *
     * @param onBannerClickListener {@link SBannerAdapter.OnBannerClickListener}
     */
    public SBannerView<T> setBannerClickListener(SBannerAdapter.OnBannerClickListener onBannerClickListener) {
        this.onBannerClickListener = onBannerClickListener;
        return this;
    }

    /**
     * 开始轮播
     * 确保在调用用了{@link SBannerView {@link #setPages(List, ISBannerHolderCreator)}} 之后再调用这个方法开始轮播
     */
    public void start() {
        // 如果Adapter为null, 说明还没有设置数据，这个时候不应该轮播Banner
        if (sBannerAdapter == null) {
            return;
        }
        if (isOpenLoop) {
            pause();
            isAutoPlay = true;
            handler.postDelayed(loopRunnable, delayedTime);
        }
    }

    /**
     * 停止轮播
     */
    public void pause() {
        isAutoPlay = false;
        handler.removeCallbacks(loopRunnable);
    }
}
