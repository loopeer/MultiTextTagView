package com.loopeer.android.librarys.multitexttagview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.loopeer.android.librarys.multitagview.MultiTagView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MultiTagView.TagChangeListener, View.OnClickListener {

    private MultiTagView tagView;
    private MultiTagView tagViewShow;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        intView();
        setTestData();
    }

    private void intView() {
        tagView = (MultiTagView) findViewById(R.id.tag_view);
        tagViewShow = (MultiTagView) findViewById(R.id.tag_view_show);
        button = (Button) findViewById(android.R.id.button1);

        tagView.setTagChangeListener(this);
        button.setOnClickListener(this);
    }

    private void setTestData() {
        tagView.updateTags(createTestTag());
    }

    private ArrayList<String> createTestTag() {
        ArrayList<String> results = new ArrayList<>();
        results.add("aslgjlsdjg");
        results.add("iewjg");
        results.add("fsljsdljg顺利度过");
        results.add("王府井哦四公斤");
        results.add("是道格拉斯结果");
        results.add("施工建设");
        results.add("伤筋动骨失落感");
        return results;
    }

    @Override
    public void onTagClick(String tag) {
        Toast.makeText(this, tag, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        ArrayList<String> results = tagView.getTags();

        tagViewShow.updateTags(results);
    }
}
