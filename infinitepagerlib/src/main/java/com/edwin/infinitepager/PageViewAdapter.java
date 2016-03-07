package com.edwin.infinitepager;

import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chen xue yu  on 2016/1/27.
 *
 * @author 陈学玉
 */
public abstract class PageViewAdapter<T> {
    private final List<T> dataSet = new ArrayList<T>();
    protected Context context;
    private int curPos;
    protected LayoutInflater inflater;
    private DataSetObservable datasObservable = new DataSetObservable();

    public PageViewAdapter(Context context, List<T> datas) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        replaceDatas(datas);
    }

    public void replaceDatas(List<T> datas) {
        dataSet.clear();
        addDatas(datas);
    }

    public void addDatas(List<T> datas) {
        if (datas != null) {
            dataSet.addAll(datas);
        }
        datasObservable.notifyChanged();
    }

    public T getItem(int pos) {
        return dataSet.get(pos);
    }

    public abstract View getView(View convertView, int pos);

    public abstract int getId(int pos);

    public int getCurrent() {
        return curPos;
    }

    public int getViewType(int position) {
        return 0;
    }

    public void setCurent(int pos) {
        curPos = (pos + getCount()) % getCount();
    }

    public int getCount() {
        return dataSet.size();
    }

    public void registerObservable(DataSetObserver observable) {
        datasObservable.registerObserver(observable);
    }

    public void unregisterObservable(DataSetObserver observable) {
        datasObservable.unregisterObserver(observable);
    }

    public void notifyDataSetchanged() {
        datasObservable.notifyChanged();
    }

    public void notifyDataSetInvalidated() {
        datasObservable.notifyInvalidated();
    }
}
