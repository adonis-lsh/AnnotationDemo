package com.demo.lsh.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.lsh.demo.annobean.Meal;
import com.demo.lsh.demo.annobean.MealFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.textView)
    private TextView mTextView;

    @BindView(R.id.edit_name)
    private EditText mEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        demo1();
        try {
//            demo2();
            //通过反射实例化对象
//            demo3();
            //通过java反射机制得到类的构造函数,通过构造函数创建实例对象
//            demo4();
            //通过Java反射操作成员变量
//            demo5();
            //通过java反射机制得到类的接口,父类,韩束信息,类型
//            demo6();
            //通过反射调用类中的方法
//            demo7();
            //测试注解
//            test1();
            //编译时注解的测试
            test2();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================================================
    // ============= 模拟一个去披萨店点餐的一个过程  ==============
    // ======================================================
   private MealFactory mealFactory = new MealFactory();
    public Meal order(String mealName) {
        return mealFactory.create(mealName);
    }
    public void click(View view) {
        String mealName = mEditText.getText().toString();
        Meal meal = order(mealName);
        if (meal != null) {
            Toast.makeText(this, "您点饭的价格为" + meal.getPrice() + "美元", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有这样的饭", Toast.LENGTH_SHORT).show();
        }
    }
    private void test2() throws IllegalAccessException {
        ParseXml.reject(this);
        mTextView.setText("请输入您要点东西?");
    }

    private void test1() throws IllegalAccessException {
        ParseXml.reject(this);
        mTextView.setText("请输入您要点东西?");
    }

    private void demo7() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InstantiationException, InvocationTargetException {
        Class<?> aClass = Class.forName("com.demo.lsh.demo.SuperStudent");
        Method study = aClass.getMethod("study");
        study.invoke(aClass.newInstance());
        Log.e(TAG, "=================");
        Method add = aClass.getDeclaredMethod("add", String.class, String.class);
        add.setAccessible(true);
        String invoek = (String) add.invoke(aClass.newInstance(), "李宁","说的啥");
        Log.e(TAG, invoek);
        Log.e(TAG, "===================");

        String name = aClass.getClassLoader().getClass().getName();
        Log.e(TAG, "类加载器的名字" + name);
    }

    private void demo6() throws ClassNotFoundException {
        Class<?> aClass = Class.forName("com.demo.lsh.demo.SuperStudent");
        Class<?> superclass = aClass.getSuperclass();
        Log.e(TAG, "学霸的父类是" + superclass.getName());

        Log.e(TAG, "===================================");

        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Log.e(TAG, "类中的成员是" + declaredField);
        }
        Log.e(TAG, "===================================");

        Method[] methods = aClass.getMethods();
        for (Method method : methods) {
            Log.e(TAG, "方法名"+method.getName());
            Log.e(TAG, "返回类型"+method.getReturnType());
            Log.e(TAG, "方法访问修饰符"+ Modifier.toString(method.getModifiers()));
            Log.e(TAG, "方法代码写法"+method);
        }

        Log.e(TAG, "===================================");

        Class<?>[] interfaces = aClass.getInterfaces();
        for (Class<?> anInterface : interfaces) {
            Log.e(TAG, anInterface.getName());
        }
    }

    private void demo5() throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException,
            InstantiationException {
        Class<?> aClass = Class.forName("com.demo.lsh.demo.Student");
        Field declaredField = aClass.getDeclaredField("name");
        declaredField.setAccessible(true);
        Object obj = aClass.newInstance();
        declaredField.set(obj,"李胜辉");

        Log.e(TAG, declaredField.get(obj)+"");
    }

    private void demo4() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<?> aClass = Class.forName("com.demo.lsh.demo.Student");
        Constructor<?> constructor = aClass.getConstructor(int.class, String.class);
        Student student = (Student) constructor.newInstance(25, "李宁");
        Log.e(TAG, "带参构造"+student.toString());

        Constructor<?> constructor1 = aClass.getConstructor();
        Student o = (Student) constructor1.newInstance();
        o.setName("李胜辉");
        o.setAge(26);
        Log.e(TAG, "无参构造"+o.toString());

        Constructor<?> declaredConstructor = aClass.getDeclaredConstructor(String.class, int.class);
        declaredConstructor.setAccessible(true);
        Student instance = (Student) declaredConstructor.newInstance("吴典", 22);
        Log.e(TAG, instance.toString());
    }

    private void demo3() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class<?> aClass = Class.forName("com.demo.lsh.demo.Student");
        Student s = (Student) aClass.newInstance();
        s.setAge(25);
        s.setName("李宁");
        Log.e(TAG, s.toString());
    }

    private void demo2() throws ClassNotFoundException {
        Class<?> aClass = Class.forName("com.demo.lsh.demo.Student");
        Log.e(TAG, "demo2第一种写法,包名:" + aClass.getPackage().getName() + "---------" + "完整类名" + aClass.getName
               ());

        Class<Student> aClass1 = Student.class;
        Log.e(TAG, "demo2第二种写法,包名:" + aClass1.getPackage().getName() + "---------" + "完整类名" + aClass1.getName
                ());
    }

    private void demo1() {
        Student s = new Student();
        Log.e(TAG, "包名" + s.getClass().getPackage().getName() + "---------" + "完整类名" + s.getClass().getName
                ());
    }


}
