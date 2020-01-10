package com.android.test;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.test.adapter.UrlListAdapter;
import com.android.test.callback.HtmlCallBack;
import com.android.test.network.DownLoadHtmlRunnable;
import com.android.test.network.ThreadPoolManger;
import com.android.test.utils.HtmlCacheUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 只做了内存缓存
 */
public class UrlListActivity extends AppCompatActivity {
    private static final String TAG = "UrlListActivity";
    public static final String URL_ = "url";
    private RecyclerView mRecyclerView;
    private UrlListAdapter mAdapter;
    private List<String> mList;
    private CountDownTimer countDownTimer;

    private LinearLayoutManager mLayoutManager;
    private int firstPosition, lastPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        initView();
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recycler_view);

        String[] urls = getResources().getStringArray(R.array.url);
        if (urls.length > 0) {
            mList = Arrays.asList(urls);
        } else {
            mList = new ArrayList<>();
        }
        mAdapter = new UrlListAdapter(this,mList);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        DefaultItemAnimator mAnimator = new DefaultItemAnimator();
        mAnimator.setSupportsChangeAnimations(false);
        mRecyclerView.setItemAnimator(mAnimator);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new UrlListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(UrlListActivity.this, WebViewActivity.class);
                intent.putExtra(URL_, mList.get(position));
                startActivity(intent);
            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    //开始滑动,停止加载
                    if (countDownTimer != null) {
                        countDownTimer.cancel();
                    }
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //停止滑动,3秒后开始预加载
                    firstPosition = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                    lastPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
                    countDownTimer.start();
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

            }
        });

        initTimer();
    }

    /**
     * 计时器
     */
    private void initTimer(){
        countDownTimer = new CountDownTimer(3000, 3000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                loadData();
            }
        };

        //先默认缓存4条数据
        firstPosition = 0;
        lastPosition = 4;
        countDownTimer.start();
    }


    /**
     * 缓存数据
     */
    private void loadData(){
        for(int i= firstPosition;i<= lastPosition;i++){
            if(HtmlCacheUtil.getInstance().getCache(mList.get(i)) == null){
                final int finalI = i;
                ThreadPoolManger.getInstance().addRunnable(new DownLoadHtmlRunnable(mList.get(i), new HtmlCallBack() {
                    @Override
                    public void success(String html, String url) {
                        Log.e(TAG, html);
                        //缓存Html到内存中
                        HtmlCacheUtil.getInstance().putCache(url, html);
                        mAdapter.notifyItemChanged(finalI);
                    }

                    @Override
                    public void onFail(Exception e) {
                        Log.e(TAG, "onFail: " + e.toString());
                    }
                }));
            }
        }
        ThreadPoolManger.getInstance().run();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(null != countDownTimer){
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }
}
