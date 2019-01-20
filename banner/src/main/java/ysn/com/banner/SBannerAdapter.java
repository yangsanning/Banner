package ysn.com.banner;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author yangsanning
 * @ClassName SBannerAdapter
 * @Description 一句话概括作用
 * @Date 2019/1/18
 * @History 2019/1/18 author: description:
 */
public class SBannerAdapter<T> extends PagerAdapter {

    private List<T> dataList;
    private ISBannerHolderCreator bannerHolderCreator;
    private ViewPager mViewPager;
    private boolean isOpenLoop;
    private OnBannerClickListener onBannerClickListener;
    private final int loopCount = 520 * 1314 * 2020;

    public SBannerAdapter(List<T> datas, ISBannerHolderCreator bannerHolderCreator, boolean isOpenLoop) {
        if (dataList == null) {
            dataList = new ArrayList<>();
        }
        //dataList.add(datas.get(datas.size()-1));// 加入最后一个
        for (T t : datas) {
            dataList.add(t);
        }
        // dataList.add(datas.get(0));//在最后加入最前面一个
        this.bannerHolderCreator = bannerHolderCreator;
        this.isOpenLoop = isOpenLoop;
    }

    public void setPageClickListener(OnBannerClickListener pageClickListener) {
        onBannerClickListener = pageClickListener;
    }

    /**
     * 初始化Adapter和设置当前选中的Item
     *
     * @param viewPager
     */
    public void setUpViewViewPager(ViewPager viewPager) {
        mViewPager = viewPager;
        mViewPager.setAdapter(this);
        mViewPager.getAdapter().notifyDataSetChanged();
        int currentItem = isOpenLoop ? getStartSelectItem() : 0;
        //设置当前选中的Item
        mViewPager.setCurrentItem(currentItem);
    }

    private int getStartSelectItem() {
        if (getRealCount() == 0) {
            return 0;
        }

        // 我们设置当前选中的位置为Integer.MAX_VALUE / 2,这样开始就能往左滑动
        // 但是要保证这个值与getRealPosition 的 余数为0，因为要从第一页开始显示
        int currentItem = getRealCount() * loopCount / 2;
        if (currentItem % getRealCount() == 0) {
            return currentItem;
        }

        // 直到找到从0开始的位置
        while (currentItem % getRealCount() != 0) {
            currentItem++;
        }
        return currentItem;
    }

    public void setDatas(List<T> datas) {
        dataList = datas;
    }

    /**
     * getCount 的返回值为Integer.MAX_VALUE 的话，那么在setCurrentItem的时候会ANR(除了在onCreate 调用之外)
     */
    @Override
    public int getCount() {
        return isOpenLoop ? getRealCount() * loopCount : getRealCount();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        View view = getView(position, container);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        // 轮播模式才执行
        if (isOpenLoop) {
            int position = mViewPager.getCurrentItem();
            if (position == getCount() - 1) {
                position = 0;
                setCurrentItem(position);
            }
        }

    }

    private void setCurrentItem(int position) {
        try {
            mViewPager.setCurrentItem(position, false);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return 真实的Count
     */
    private int getRealCount() {
        return dataList == null ? 0 : dataList.size();
    }

    private View getView(int position, ViewGroup container) {

        final int realPosition = position % getRealCount();
        ISBannerHolder holder = null;

        holder = bannerHolderCreator.createViewHolder();

        if (holder == null) {
            throw new RuntimeException("can not return a null holder");
        }
        // create View
        View view = holder.createView(container.getContext());

        if (dataList != null && dataList.size() > 0) {
            holder.onBind(container.getContext(), realPosition, dataList.get(realPosition));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onBannerClickListener != null) {
                    onBannerClickListener.onBannerClick(v, realPosition);
                }
            }
        });
        return view;
    }

    public interface OnBannerClickListener {
        void onBannerClick(View view, int position);
    }
}
