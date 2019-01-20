package ysn.com.sbanner;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


/**
 * @Author yangsanning
 * @ClassName MainActivity
 * @Description 一句话概括作用
 * @Date 2019/1/18
 * @History 2019/1/18 author: description:
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity, BannerFragment.newInstance()).commit();
    }
}
