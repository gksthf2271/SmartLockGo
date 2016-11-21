package com.eum.ssrgo;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by kimhansol on 2016-10-11.
 */


public class EditFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();

        Button button1 = (Button) getView().findViewById(R.id.edit_btn1);
        Button button2 = (Button) getView().findViewById(R.id.edit_btn2);
        Button button3 = (Button) getView().findViewById(R.id.edit_btn3);
        Button button4 = (Button) getView().findViewById(R.id.edit_btn4);
        TextView textview = (TextView) getView().findViewById(R.id.edit_textview);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(getApplicationContext(), "블루투스 편집", Toast.LENGTH_SHORT).show();*/
                checkBluetooth();
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                Toast.makeText(getApplicationContext(), "국민은행 000-0000-0000", Toast.LENGTH_SHORT).show();*/
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());

                alertDialog.setTitle("개발자 후원");
                alertDialog.setMessage("국민은행 0000-0000-0000입니다 감사합니다.");
                alertDialog.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "FAQ", Toast.LENGTH_SHORT).show();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "질문하기", Toast.LENGTH_SHORT).show();
            }
        });


    }

    void checkBluetooth() {
        /**
         * getDefaultAdapter() : 만일 폰에 블루투스 모듈이 없으면 null 을 리턴한다.
         이경우 Toast를 사용해 에러메시지를 표시하고 앱을 종료한다.
         */
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {  // 블루투스 미지원
            Toast.makeText(getActivity(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();

          /*  finish();  // 앱종료*/
        } else { // 블루투스 지원
            /** isEnable() : 블루투스 모듈이 활성화 되었는지 확인.
             *               true : 지원 ,  false : 미지원
             */
            if (!mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());

                alertDialog.setTitle("블루투스 사용 유무셋팅");

                alertDialog.setPositiveButton("블루투스 켜기",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                               EditFragment.this.startActivity(enableBtIntent);
                            }

                        });
                alertDialog.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                alertDialog.show();
            }
            else {
                android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(getActivity());

                alertDialog.setTitle("블루투스 셋팅");
                alertDialog.setMessage("블루투스 설정창으로 가시겠습니까?");
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setComponent(new ComponentName("com.android.settings", "com.android.settings.bluetooth.BluetoothSettings"));
                startActivity(i);
            }
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_edit, container, false);
    }



}