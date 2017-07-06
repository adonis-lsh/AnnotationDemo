package com.demo.lsh.demo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * Created by adonis_lsh on 2017/7/3
 */

public class ParseXml {
    //注入到注解的类里面
    public static void reject(Context context) throws IllegalAccessException {
        //判断传过来的上下文的类型
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            Class<? extends Activity> aClass = activity.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField != null) {
                    if (declaredField.getDeclaredAnnotations() != null) {
                        if (declaredField.isAnnotationPresent(BindView.class)) {
                            BindView bindView = declaredField.getAnnotation(BindView.class);
                            declaredField.isAnnotationPresent(BindView.class);
                            declaredField.setAccessible(true);
                            Log.e("11111", activity.findViewById(bindView.value()) + "");
                            declaredField.set(activity,activity.findViewById(bindView.value()));
                        }
                    }
                }

            }
        }
    }
}
