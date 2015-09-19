package com.loopeer.android.librarys.multitagview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class MultiTagView extends LinearLayout {

    private final static String TAG = "MultiTagView";

    public interface TagChangeListener {
        void onTagClick(String tag);
    }

    private final int DEFAULT_TAG_PADDING = 12;
    private final int DEFAULT_TAG_MARGIN = 12;
    private final int DEFAULT_TAG_PADDING_TOP = 3;
    private final int DEFAULT_TAG_HEIGHT = 28;

    private int tempWidth = 0;
    private LinearLayout mLayoutItem;
    private Context mContext;
    private ArrayList<String> tags;

    private boolean tagClickable;
    private boolean showAddButton;
    private int tagMargin;
    private int tagPadding;

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
        tagMargin = a.getDimensionPixelSize(R.styleable.MultiTagView_tagMargin, DEFAULT_TAG_MARGIN);
        tagPadding = a.getDimensionPixelSize(R.styleable.MultiTagView_tagPadding, DEFAULT_TAG_PADDING);
        setUpTreeObserver();
        init();
    }

    private void init() {
        tags = new ArrayList<>();
        addNewLayoutItemWithoutTopMargin();
        tagClickable = true;
        addEditText();
    }

    private void addNewLayoutItemWithTopMargin() {
        addNewLayoutItem(true);
    }

    private void addNewLayoutItemWithoutTopMargin() {
        addNewLayoutItem(false);
    }

    private void addNewLayoutItem(boolean withTopMargin) {
        mLayoutItem = new LinearLayout(getContext());
        LayoutParams lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        if (withTopMargin) {
            lParams.topMargin = tagMargin;
        }
        mLayoutItem.setLayoutParams(lParams);
        addView(mLayoutItem);
    }

    private void setUpTreeObserver() {
        ViewTreeObserver viewTreeObserver = getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onGlobalLayout() {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    } else {
                        getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    refresh();
                }
            });
        }
    }

    public void setTagChangeListener(TagChangeListener listener) {
        mTagChangeListener = listener;
    }

    private void addEditText() {
        if (!showAddButton) {
            return;
        }
        final EditText editText = new EditText(mContext);
        editText.setMinimumWidth(2);
        editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
        editText.setSingleLine();
        /*try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(editText, R.drawable.cusor_edit);
        } catch (Exception ignored) {
        }*/
        /*
        editText.setPadding(0, dip2px(DEFAULT_TAG_PADDING_TOP), 0, dip2px(DEFAULT_TAG_PADDING_TOP));
        editText.setHint(getResources().getString(R.string.tag_add));
        editText.setHintTextColor(getResources().getColor(R.color.text_color_hint));
        editText.setTextColor(getResources().getColor(R.color.text_color_primary));
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                        return false;
                    }
                    insertTag(editText.getText().toString().trim());
                    editString = null;
                    mTagChangeListener.onTagEdited();
                    refresh();
                    return true;
                }
                return false;
            }
        });
        editText.setBackgroundResource(android.R.color.white);
        editText.setTextSize(14);
        int textEditTextWidth = (int) editText.getPaint().measureText(getResources().getString(R.string.tag_add));
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, dip2px(DEFAULT_TAG_HEIGHT));

        tempWidth += dip2px(DEFAULT_TAG_MARGIN) + textEditTextWidth; //add tag width

        if (tempWidth - dip2px(DEFAULT_TAG_MARGIN) > getWidth()) {  //if out of screen, add a new layout
            mLayoutItem = new LinearLayout(mContext);
            LayoutParams lParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            lParams.topMargin = dip2px(DEFAULT_LAYOUT_MARGIN_TOP);
            mLayoutItem.setLayoutParams(lParams);
            addView(mLayoutItem);
            tempWidth = dip2px(DEFAULT_TAG_MARGIN) + mEditTextWidth;
        }
        mLayoutItem.addView(editText, layoutParams);
        editText.requestFocus();
        tempWidth -= dip2px(DEFAULT_TAG_MARGIN) + mEditTextWidth;
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mOnCheckDeleteViewListener.checkDeleteView();
                }
            }
        });
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        if (isFirstIn && TextUtils.isEmpty(editString) && showAddButton) {
            isFirstIn = false;
            editText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
                }
            }, 300);
        }*/
    }

    private void addTag(final String tag) {
        final Button button = new Button(mContext);
        button.setText(tag);

        button.setTextSize(14);
        button.setBackgroundResource(R.drawable.selector_tag_bg);
        button.setTextColor(ContextCompat.getColorStateList(getContext(), R.color.selector_tag_text));
        button.setPadding(tagPadding, dip2px(DEFAULT_TAG_PADDING_TOP),
                tagPadding, dip2px(DEFAULT_TAG_PADDING_TOP));
        button.setEnabled(tagClickable);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doTagClick(tag);
            }
        });
        int btnWidth = (int) (2 * tagPadding + button.getPaint().measureText(button.getText().toString()));
        LayoutParams layoutParams = new LayoutParams(btnWidth, dip2px(DEFAULT_TAG_HEIGHT));
        FrameLayout frameLayout = new FrameLayout(mContext);
        frameLayout.setLayoutParams(layoutParams);
        frameLayout.addView(button);
        tempWidth += btnWidth;
        if (tempWidth > getWidth()) {
            addNewLayoutItemWithTopMargin();
            tempWidth = tagMargin + btnWidth;
        } else {
            tempWidth += tagMargin;
        }
        layoutParams.rightMargin = tagMargin;
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
        Log.e(TAG, "start update");
        tags.clear();
        tags.addAll(arrayList);
        doRefresh();
    }

    private void doRefresh() {
        if (getWidth() == 0) return;
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

    private int dip2px(float dipValue) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
