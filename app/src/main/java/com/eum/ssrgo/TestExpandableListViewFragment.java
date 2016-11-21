package com.eum.ssrgo;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Toast;

import java.util.ArrayList;

public class TestExpandableListViewFragment extends Fragment {

	private ArrayList<String> mGroupList = null;
	private ArrayList<ArrayList<String>> mChildList = null;
/*	private ArrayList<String> mChildListContent = null;*/
	private int num;

	@Override
	public void onStart() {
		super.onStart();

		setLayout();

		mGroupList = new ArrayList<String>();
		mChildList = new ArrayList<ArrayList<String>>();
/*		mChildListContent = new ArrayList<String>();*/

		num=1;
		mGroupList.add("num");





//		mChildListContent.add("1");

//		mChildList.add(mChildListContent);


		mListView.setAdapter(new BaseExpandableAdapter(getActivity(), mGroupList, mChildList));
		
		// 그룹 클릭 했을 경우 이벤트
		mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				Toast.makeText(getActivity(), num + " click = " + groupPosition,
						Toast.LENGTH_SHORT).show();

				return false;
			}
		});
		
/*		// 차일드 클릭 했을 경우 이벤트
		mListView.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Toast.makeText(getApplicationContext(), "c click = " + childPosition, 
						Toast.LENGTH_SHORT).show();
				return false;
			}
		});*/
		
/*		// 그룹이 닫힐 경우 이벤트
		mListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
			@Override
			public void onGroupCollapse(int groupPosition) {
				Toast.makeText(getApplicationContext(), "g Collapse = " + groupPosition, 
						Toast.LENGTH_SHORT).show();
			}
		});*/
		
		// 그룹이 열릴 경우 이벤트
		mListView.setOnGroupExpandListener(new OnGroupExpandListener() {
			@Override
			public void onGroupExpand(int groupPosition) {
				Toast.makeText(getActivity(), "g Expand = " + groupPosition,
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	/*
	 * Layout
	 */
	private ExpandableListView mListView;

	private void setLayout(){
		mListView = (ExpandableListView) getView().findViewById(R.id.elv_list);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		return inflater.inflate(R.layout.fragment_riding_list_main, container, false);
	}

}