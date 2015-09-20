package com.loopeer.android.librarys.multitagview;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
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

public class MultiTagView extends LinearLayout {

    private final static String TAG = "MultiTagView";
    private final static String CURSOR_DRAWABLE_NAME = "mCursorDrawableRes";

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
    private ColorStateList textColorHint;
    private ColorStateList tagTextColor;
    private ColorStateList editTextColor;
    private int cursorDrawableId;
    private int tagBackgroundId;
    private float textSize;

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
        textColorHint = a.getColorStateList(R.styleable.MultiTagView_textColorHint);
        tagTextColor = a.getColorStateList(R.styleable.MultiTagView_tagTextColor);
        tagTextColor = a.getColorStateList(R.styleable.MultiTagView_tagTextColor);
        editTextColor = a.getColorStateList(R.styleable.MultiTagView_tagEditTextColor);
        cursorDrawableId = a.getResourceId(R.styleable.MultiTagView_cursorDrawable, R.drawable.cusor_edit);
        tagBackgroundId = a.getResourceId(R.styleable.MultiTagView_tagBackground, R.drawable.selector_tag_bg);
        textSize = a.getDimensionPixelSize(R.styleable.MultiTagView_textSize, 0);

        tagTextColor = tagTextColor == null ? ContextCompat.getColorStateList(getContext(), R.color.selector_tag_text) : tagTextColor;
        editTextColor = editTextColor == null ? ContextCompat.getColorStateList(getContext(), R.color.tag_edit_text_color) : editTextColor;
        textColorHint = textColorHint == null ? ContextCompat.getColorStateList(getContext(), R.color.tag_text_color_hint) : textColorHint;
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
        try {
            Field f = TextView.class.getDeclaredField(CURSOR_DRAWABLE_NAME);
            f.setAccessible(true);
            f.set(editText, cursorDrawableId);
        } catch (Exception ignored) {
        }

        editText.setBackgroundResource(tagBackgroundId);
        editText.setPadding(0, dip2px(DEFAULT_TAG_PADDING_TOP), 0, dip2px(DEFAULT_TAG_PADDING_TOP));
        editText.setHint(getResources().getString(R.string.tag_add));
        editText.setHintTextColor(textColorHint);
        editText.setTextColor(editTextColor);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (TextUtils.isEmpty(editText.getText().toString().trim())) {
                        return false;
                    }
                    insertTag(editText.getText().toString().trim());
                    refresh();
                    return true;
                }
                return false;
            }
        });
        if (textSize != 0) {
            editText.setTextSize(textSize);
        }
        int textEditTextWidth = (int) editText.getPaint().measureText(getResources().getString(R.string.tag_add));
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, dip2px(DEFAULT_TAG_HEIGHT));
        tempWidth += textEditTextWidth;
        if (tempWidth > getWidth()) {
            addNewLayoutItemWithTopMargin();
            tempWidth = tagMargin + textEditTextWidth;
        } else {
            tempWidth += tagMargin;
        }
        layoutParams.rightMargin = tagMargin;
        mLayoutItem.addView(editText, layoutParams);
        editText.requestFocus();
        tempWidth -= tagMargin + textEditTextWidth;
        editText.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }
        }, 300);

    }


    private void insertTag(String s) {
        tags.add(s);
    }

    private void addTag(final String tag) {
        final Button button = new Button(mContext);
        button.setText(tag);

        if (textSize != 0) {
            button.setTextSize(textSize);
        }
        button.setBackgroundResource(tagBackgroundId);
        button.setTextColor(tagTextColor);
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
