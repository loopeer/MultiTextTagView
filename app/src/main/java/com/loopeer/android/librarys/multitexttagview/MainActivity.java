package com.loopeer.android.librarys.multitexttagview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.loopeer.android.librarys.multitagview.MultiTagView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MultiTagView tagView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        intView();
        setTestData();
    }

    private void intView() {
        tagView = (MultiTagView) findViewById(R.id.tag_view);
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

}
