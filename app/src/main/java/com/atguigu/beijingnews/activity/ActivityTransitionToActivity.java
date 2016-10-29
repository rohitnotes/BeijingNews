package com.atguigu.beijingnews.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.atguigu.beijingnews.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Activity that gets transitioned to
 */
public class ActivityTransitionToActivity extends Activity {

    private PhotoView iv_photoview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transition_to);

        iv_photoview = (PhotoView) findViewById(R.id.iv_photoview);

        final PhotoViewAttacher attacher = new PhotoViewAttacher(iv_photoview);
        Uri uri = getIntent().getData();

        Picasso.with(this)
                .load(uri)
                .into(iv_photoview, new Callback() {
                    @Override
                    public void onSuccess() {
//                        attacher.cleanup();//保持原样
                        attacher.update();//更新成原生状态
                    }

                    @Override
                    public void onError() {

                    }
                });
    }
}
