package com.pig.android.asm

import android.view.View
import android.widget.Toast

class Tracker2 {

    companion object {

        fun click(view: View) {
            Toast.makeText(view.context, "点击事件被记录了", Toast.LENGTH_SHORT).show()
        }
    }
}