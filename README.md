# MultiTextTagView

Screeshot
====
![](/screenshot/screenshot.gif)

Usage
====
First, add the layout
```xml
    <com.loopeer.android.librarys.multitagview.MultiTagView
        android:id="@+id/tag_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="12dp"
        app:tagMargin="6dp"
        app:tagPaddingHorizontal="6dp"
        app:tagPaddingVertical="4dp"
        app:showEditButton="true"
        app:tagTextSize="16sp"
        />
```
* **showEditButton** When you set true, you can add tag by input
* **tagMargin** One tag to other has the margin
* **tagPaddingHorizontal**
* **tagEditPaddingHorizontal** 
* **tagPaddingVertical** 
* **textColorHint** 
* **tagTextColor** 
* **tagEditTextColor** 
* **tagBackground** 
* **tagEditBackground** 
* **cursorDrawable** 
* **tagTextSize** 

Then, you can add data.
```java
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
```
You can add ClickListener
```java
        tagView.setTagChangeListener(this);
```
Get click data by the method 
```java
    @Override
    public void onTagClick(String tag) {
        Toast.makeText(this, tag, Toast.LENGTH_SHORT).show();
    }
```
Get tags data by the method 
```java
        ArrayList<String> results = tagView.getTags();
```

License
====
<pre>
Copyright 2015 Loopeer

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
</pre>
