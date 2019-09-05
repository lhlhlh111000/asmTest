package com.pig.android.asm;

import android.view.View;
import android.widget.Toast;

public class Tracker {

    public static void click(View view) {
        Toast.makeText(view.getContext(), "点击事件被记录了", Toast.LENGTH_SHORT).show();
    }
}
