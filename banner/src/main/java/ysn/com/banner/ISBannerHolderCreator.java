package ysn.com.banner;

/**
 * @Author yangsanning
 * @ClassName ISBannerHolderCreator
 * @Description 一句话概括作用
 * @Date 2019/1/18
 * @History 2019/1/18 author: description:
 */
public interface ISBannerHolderCreator<T extends ISBannerHolder> {
    /**
     * 创建ViewHolder
     * @return
     */
    public T createViewHolder();
}

