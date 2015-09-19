package com.loopeer.android.librarys.multitagview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class MultiTagView extends LinearLayout {

    public interface TagChangeListener {
        void onTagClick(String tag);
    }

    private final int DEFAULT_TAG_PADDING = 12;
    private final int DEFAULT_TAG_MARGIN = 12;
    private final int DEFAULT_TAG_PADDING_TOP = 3;
    private final int DEFAULT_LAYOUT_MARGIN_TOP = 12;
    private final int DEFAULT_TAG_HEIGHT = 28;

    private int tempWidth = 0;
    private LinearLayout mLayoutItem;
    private Context mContext;
    private int mTotalWidth;
    private ArrayList<String> tags;

    private boolean tagClickable;
    private boolean showAddButton;

    private TagChangeListener mTagChangeListener;

    public MultiTagView(Context context) {
        this(context, null);
    }

    public MultiTagView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiTagView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        mContext = context;

        if (attrs == null) return;
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MultiTagView, defStyleAttr, 0);
        if (a == null) return;

        showAddButton = a.getBoolean(R.styleable.MultiTagView_showEditButton, false);
        init();
    }

    private void init() {
        int parentPadding = getResources().getDimensionPixelSize(R.dimen.activity_vertical_margin);
        mTotalWidth = getDeviceWidth() - parentPadding * 2;
        tags = new ArrayList<>();
        mLayoutItem = new LinearLayout(mContext);
        mLayoutItem.setLayoutParams(
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(mLayoutItem);
        tagClickable = true;
        addEditText();
    }

    public void setTagChangeListener(TagChangeListener listener) {
        mTagChangeListener = listener;
    }

    private void addEditText() {
        if (!showAddButton) {
            return;
        }
    }

    private void insertTag(String tag) {
        tags.add(tag);
    }

    private void addTag(final String tag) {
        final Button button = new Button(mContext);
        button.setText(tag);

        button.setTextSize(14);
        button.setBackgroundResource(R.drawable.selector_tag_bg);
        button.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.selector_tag_text));
        button.setPadding(dip2px(DEFAULT_TAG_PADDING), dip2px(DEFAULT_TAG_PADDING_TOP),
                dip2px(DEFAULT_TAG_PADDING), dip2px(DEFAULT_TAG_PADDING_TOP));
        button.setEnabled(tagClickable);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doTagClick(tag);
            }
        });
        int btnWidth = (int) (2 * dip2px(DEFAULT_TAG_PADDING) + button.getPaint().measureText(button.getText().toString()));
        LayoutParams layoutParams = new LayoutParams(btnWidth, dip2px(DEFAULT_TAG_HEIGHT));
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.setLayoutParams(layoutParams);
        frameLayout.addView(button);
        layoutParams.rightMargin = dip2px(DEFAULT_TAG_MARGIN);
        tempWidth += dip2px(DEFAULT_TAG_MARGIN) + btnWidth;
        if (tempWidth - dip2px(DEFAULT_TAG_MARGIN) > mTotalWidth) {
            mLayoutItem = new LinearLayout(mContext);
            LayoutParams lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lParams.topMargin = dip2px(DEFAULT_LAYOUT_MARGIN_TOP);
            mLayoutItem.setLayoutParams(lParams);
            addView(mLayoutItem);
            tempWidth = dip2px(DEFAULT_TAG_MARGIN) + btnWidth;
        }
        mLayoutItem.addView(frameLayout, layoutParams);
    }

    private void doTagClick(String tag) {
        if (mTagChangeListener == null) return;
        mTagChangeListener.onTagClick(tag);
    }

    private void refresh() {
        removeAllViews();
        mLayoutItem = new LinearLayout(mContext);
        mLayoutItem.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        addView(mLayoutItem);
        tempWidth = 0;
        for (String tag : tags) {
            addTag(tag);
        }
        addEditText();
    }

    public void updateTags(ArrayList<String> arrayList) {
        tags.clear();
        tags.addAll(arrayList);
        refresh();
    }

    public void removeAllTagView() {
        tags.clear();
        refresh();
    }

    public void removeTagAt(int i) {
        tags.remove(i);
        refresh();
    }

    public void setShowAddButton(boolean show) {
        showAddButton = show;
        refresh();
    }

    public void setTagClickable(boolean able) {
        tagClickable = able;
        refresh();
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    private int getDeviceWidth() {
        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    private int dip2px(float dipValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
