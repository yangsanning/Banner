package ysn.com.sbanner;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import ysn.com.banner.ISBannerHolder;
import ysn.com.banner.ISBannerHolderCreator;
import ysn.com.banner.SBannerView;
import ysn.com.sbanner.bean.Banner;

/**
 * @Author yangsanning
 * @ClassName BannerFragment
 * @Description 一句话概括作用
 * @Date 2019/1/18
 * @History 2019/1/18 author: description:
 */

public class BannerFragment extends Fragment {

    //    public static final int[] IMAGES = new int[]{R.mipmap.image1};
    public static final int[] IMAGES = new int[]{R.mipmap.image1, R.mipmap.image2, R.mipmap.image3, R.mipmap.image4};

    private SBannerView sBannerView;

    public static BannerFragment newInstance() {
        return new BannerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frgament_banner, null);
        initView(view);
        iniData();
        return view;
    }

    private void initView(View view) {
        sBannerView = view.findViewById(R.id.banner_fragment_sbanner_view);
    }

    private void iniData() {
        List<Banner> list = new ArrayList<>();
        Banner banner;
        for (int image : IMAGES) {
            banner = new Banner();
            banner.resId = image;
            list.add(banner);
        }

        sBannerView.setPages(list, new ISBannerHolderCreator<BannerHolder>() {
            @Override
            public BannerHolder createViewHolder() {
                return new BannerHolder();
            }
        }).start();
    }

    public class BannerHolder implements ISBannerHolder<Banner> {
        private ImageView imageView;

        @Override
        public View createView(Context context) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_banner, null);
            imageView = view.findViewById(R.id.banner_item_image);
            return view;
        }

        @Override
        public void onBind(Context context, int position, Banner data) {
            imageView.setImageResource(data.resId);
        }
    }
}