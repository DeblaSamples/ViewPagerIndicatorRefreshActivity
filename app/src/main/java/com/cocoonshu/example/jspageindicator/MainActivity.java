package com.cocoonshu.example.jspageindicator;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.viewpagerindicator.TitlePageIndicator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * com.viewpagerindicator.TitlePageIndicator 试用
 * @Author Cocoonshu
 * @Date   2017-03-23 13:52:36
 */
public class MainActivity extends AppCompatActivity {
    public static final int FUNC_1 = 1;
    public static final int FUNC_2 = 2;

    private int                mRefreshMethod    = FUNC_2;
    private ImageButton        mRefresher        = null;
    private TitlePageIndicator mIndicator        = null;
    private ViewPager          mViewPager        = null;
    private ViewPagerAdapter   mViewPagerAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        setupListeners();
    }

    private void findViews() {
        mRefresher        = (ImageButton) findViewById(R.id.refresher);
        mIndicator        = (TitlePageIndicator) findViewById(R.id.indicator);
        mViewPager        = (ViewPager) findViewById(R.id.pager);
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mViewPagerAdapter);
        mIndicator.setViewPager(mViewPager);
        mViewPagerAdapter.setTitles(new String[] {"2017", "3", "23"});
    }

    private void setupListeners() {
        mRefresher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int      hour     = calendar.get(Calendar.HOUR_OF_DAY);
                int      minute   = calendar.get(Calendar.MINUTE);
                int      second   = calendar.get(Calendar.SECOND);
                String[] titles   = new String[] {
                        String.valueOf(hour),
                        String.valueOf(minute),
                        String.valueOf(second)};

                if (mRefreshMethod == FUNC_1) {
                    // 用这个方法，ViewPager在第一个Page和最后一个Page时，才能刷新Indicator
                    mViewPagerAdapter.setTitles(titles);
                } else if (mRefreshMethod == FUNC_2) {
                    // 所以只能考虑用新的Adapter来规避缓存问题，但需要注意的是记得把旧Adapter中已经缓存好的数据
                    // 交换到新的Adapter中，防止Fragment被重建
                    int currentPage = mViewPager.getCurrentItem();
                    ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
                    adapter.swapCache(mViewPagerAdapter);
                    adapter.setTitles(titles);
                    mViewPager.setAdapter(adapter);
                    mViewPager.setCurrentItem(currentPage, false);
                    mViewPagerAdapter = adapter;
                }
            }
        });
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> mFragments = new ArrayList<>();
        private String[]       mTitles    = null;

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setTitles(String[] titles) {
            mTitles = titles;
            notifyDataSetChanged();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles == null ? null : mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            int cachedFragmentCount = mFragments.size();
            if (position < cachedFragmentCount) {
                return mFragments.get(position);
            } else {
                Fragment fragment = new DummyFragment();
                mFragments.add(position, fragment);
                return fragment;
            }
        }

        @Override
        public int getCount() {
            return mTitles == null ? 0 : mTitles.length;
        }

        public void swapCache(ViewPagerAdapter adapter) {
            if (adapter != null) {
                mFragments.clear();
                mFragments.addAll(adapter.mFragments);
            }
        }
    }

    public static class DummyFragment extends Fragment {

        public DummyFragment() {
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_content, container, false);
        }
    }
}
