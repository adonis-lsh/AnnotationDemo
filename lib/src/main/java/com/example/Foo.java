package com.example; //PackageElement 包元素

/**
 * Created by adonis_lsh on 2017/7/4
 */

public class Foo {   //TypeElement 类型元素(可以是接口,抽象类)
    private int a;   //variableElement变量元素
    private Foo other; //variableElement

    public Foo() {   //ExecuteElement 执行元素(方法元素)
    }

    public void setA( //ExecuteElement
            int newA){  //TypeElement
    }
}
