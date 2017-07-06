package com.demo.lsh.demo.annobean;

import com.example.Factory;

/*
 * Created by adonis_lsh on 2017/7/3
 */
@Factory(type = Meal.class,id = "A")
public class APizza implements Meal {
    @Override
    public float getPrice() {
        return 6.0f;
    }
}
