package com.eum.ssrgo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ListviewAdapter extends BaseAdapter {
    Context context;
    ArrayList<ListviewFragment> data;

    TextView num_textView;
    TextView riding_date_textView;
    TextView section1_textView;
    TextView section2_textView;


    public ListviewAdapter(Context context,ArrayList<ListviewFragment> data){
        this.context = context;
        this.data=data;
    }

    /*int getCount()는 이 리스트뷰가 몇개의 아이템을 가지고있는지를 알려주는 함수입니다. 우리는 arraylist의 size(갯수) 만큼 가지고있으므로
    return 0 ; ->      this.data.size()();
    으로 변경합니다.*/
    @Override
    public int getCount(){return this.data.size();}

/*
    Object getItem(int position)은 현재 어떤 아이템인지를 알려주는 부분으로 우리는 arraylist에 저장되있는 객체중 position에 해당하는것을 가져올것이므로
    return null; ->return this.data.get(position)으로 변경합니다.*/
    @Override
    public String getItem(int position){return String.valueOf(this.data.get(position));}

/*
    현재 어떤 포지션인지를 알려주는 부분으로
    return 0; -> return postion으로 변경합니다*/
    @Override
    public long getItemId(int position){return position;}






    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView==null) {
            LayoutInflater.from(context).inflate(R.layout.riding_list, null);
            /*convertView=inflater.inflate(layout,parent,false);*/

            convertView = LayoutInflater.from(context).inflate(R.layout.riding_list,null);
            num_textView = (TextView)convertView.findViewById(R.id.riding_list_num);
            riding_date_textView = (TextView)convertView.findViewById(R.id.riding_list_date);
            section1_textView = (TextView)convertView.findViewById(R.id.section1);
            section2_textView  =(TextView)convertView.findViewById(R.id.section2);

            num_textView.setText(data.get(position).getmNum());
            riding_date_textView.setText(data.get(position).getmDate());
            section1_textView.setText(data.get(position).getmSection1());
            section2_textView.setText(data.get(position).getmSection2());

        }
            return convertView;
    }
}
