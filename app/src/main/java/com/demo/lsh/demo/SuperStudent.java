package com.demo.lsh.demo;

import android.util.Log;

/**
 * Created by adonis_lsh on 2017/6/30
 */

public class SuperStudent extends Student implements Skill {
    private static final String TAG = "SuperStudent";

    @Override
    public void study() {
        Log.e(TAG, "我是学霸");
    }

    private String add(String name1, String name2) {
        return name1 + name2;
    }
}
