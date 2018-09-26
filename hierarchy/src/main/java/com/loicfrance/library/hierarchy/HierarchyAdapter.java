/*
 * Copyright 2018 RichardFrance
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom
 * the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.loicfrance.library.hierarchy;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by Loic France on 21/10/2015.
 */
public abstract class HierarchyAdapter<T> extends BaseAdapter {

    protected int resource;
    protected Context context;
    protected HierarchyManager<T> mng;


    public HierarchyAdapter(Context context, HierarchyManager<T> hierarchyMng, int itemLayoutResId) {
        this.context = context;
        this.resource = itemLayoutResId;
        this.mng = hierarchyMng;
    }

    protected abstract void fillView(View v, T obj, int position, boolean recycled);

    public HierarchyManager<T> getHierarchy() {
        return mng;
    }

    @Override
    public int getCount() {
        return mng.getChildCount();
    }

    @Override
    public T getItem(int position) {
        return mng.getChildObj(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        T obj = mng.getChildObj(position);
        if (convertView == null) {
            view = View.inflate(context, resource, null);
            fillView(view, obj, position, false);
        } else {
            view = convertView;
            fillView(view, obj, position, true);
        }
        return view;
    }
}
