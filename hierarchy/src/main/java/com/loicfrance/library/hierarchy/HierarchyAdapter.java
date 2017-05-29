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
