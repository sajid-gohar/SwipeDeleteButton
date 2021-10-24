package com.check.swipe_delete;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.check.swipe_delete.AdapterClass.Adapter;
import com.check.swipe_delete.SwipeHelperClass.BtnDeleteClickListner;
import com.check.swipe_delete.SwipeHelperClass.SwipeHelperClass;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<DModelClass> myList;
    Adapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeList();
        bindViews();
    }

    private void initializeList() {
        myList =new ArrayList<>();
        for (int i=0;i<10;i++)
        {
            myList.add(new DModelClass("Item Name:"+i));
        }

    }

    private void bindViews() {
        recyclerView=findViewById(R.id.rcv_swipe);
        adapter=new Adapter(this, myList);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.setAdapter(adapter);
        initializeSwipe();
    }

    private void initializeSwipe() {
        SwipeHelperClass swipeHelperClass=new SwipeHelperClass(this,recyclerView,250) {
            @Override
            public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(getApplicationContext(),"Text not working with path",R.drawable.shp_circle_del_btn,R.color.red , R.color.red, new BtnDeleteClickListner() {
                    @Override
                    public void onClick(int pos) {
                        adapter.removeItem(pos);
                    }
                }));
            }
        };
    }
}