package com.chen.xy.mypagescrollview;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.edwin.infinitepager.transformer.HorizontalOverride;
import com.edwin.infinitepager.transformer.HorizontalScroll;
import com.edwin.infinitepager.transformer.HorizontalStack;
import com.edwin.infinitepager.InfinitePager;
import com.edwin.infinitepager.PageViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    InfinitePager psv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_pager);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        psv = (InfinitePager) findViewById(R.id.page_psv);
        psv.setSlideMode(InfinitePager.SlideMode.HORIZONTAL);
        psv.reverseContainer(false);
        psv.enableIntervalLoop(3000);
        psv.setTransformer(new HorizontalScroll(500));
//        psv.setMinmumLoopCount(10);
        List<String> datas = new ArrayList<String>();
        datas.add("aaaa");
        datas.add("bbbb");
        datas.add("cccc");
        datas.add("addddaaa");
        datas.add("yyyyyyyyyyyyyyyyyyyyy");
        datas.add("zzzzzzzzzzz");

        adapter = new PageViewAdapter<String>(this, datas) {
            private int color[] = {Color.GRAY, Color.BLACK, Color.RED, Color.GREEN, Color.CYAN, Color.BLUE, Color.YELLOW};

            @Override
            public View getView(View convertView, int pos) {
                Log.d("mmmm", "position : " + pos);
                View v = null;
                if (convertView == null) {
                    v = inflater.inflate(R.layout.psv_item_layout, null);
                } else {
                    v = convertView;
                }
                TextView tv = (TextView) v.findViewById(R.id.tv);
                v.setBackgroundColor(color[pos % color.length]);
                tv.setText(getItem(pos).toString());
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "ajsodgijeowrgja;eoir", Toast.LENGTH_LONG).show();
                    }
                });
                return v;
            }

            @Override
            public int getId(int pos) {
                return pos;
            }
        };
        psv.setPageAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mif = new MenuInflater(this);
        mif.inflate(R.menu.page_scroll_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.override:
                psv.reverseContainer(false);
                psv.setTransformer(new HorizontalOverride(500));
                break;
            case R.id.scroll:
                psv.reverseContainer(false);
                psv.setTransformer(new HorizontalScroll(500));
                break;
            case R.id.stack:
                psv.reverseContainer(true);
                psv.setTransformer(new HorizontalStack(500));
                break;
            case R.id.loop_toggle:
                psv.setIsLoopMode(!psv.isLoopMode());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private PageViewAdapter<String> adapter;

}