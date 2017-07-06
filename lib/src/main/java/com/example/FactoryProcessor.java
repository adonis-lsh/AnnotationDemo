package com.example;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

/**
 * Created by adonis_lsh on 2017/7/6
 */

@AutoService(Processor.class)
public class FactoryProcessor extends AbstractProcessor {

    //处理元素的工具类
    private Elements mElementUtils;
    //创建一个源文件的工具
    private Filer mFiler;
    //输出消息,包括错误的位置信息
    private Messager mMessager;
    //操作元素类型的工具类
    private Types mTypeUtils;

    //定义一个map集合,用来对应type的类标准名和FactoryMapGroup
    Map<String, FactoryMapGroup> itemMap = new LinkedHashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();
        mTypeUtils = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        /**
         * 完成一个文件生成的布局
         * 1,拿到我们注解@Factory集合,要获取每个被@Factory注解的元素
         * 2,获取注解元素中的Type变量,判断是否为Meal.calss,是的话,就保存id
         * 3,检查元素类型是否符合我们的要求,(必须是类,并且只是是实现我们type中的写的接口),必须public修饰并有一个公开的构造方法
         * 4,保存id和对应的元素类型到map集合中
         * 5.写源码文件
         */
        try {
            for (Element annoElement : roundEnv.getElementsAnnotatedWith(Factory.class)) {
                if (annoElement.getKind() != ElementKind.CLASS) {
                    throw new ProcessingException(annoElement, "只能注解类%s", Factory.class.getSimpleName());
                }

                TypeElement classElement = (TypeElement) annoElement;

                //获取我们注解得到信息
                FactoryAnnoInfo factoryAnnoInfo = new FactoryAnnoInfo(classElement);

                //检查元素类型是否符合我们的要求
                checkValidClass(factoryAnnoInfo);

                /**
                 * 这里面是两个map,外面的map是保证创建一个Meal相关的type的注解,当然,Meal也可以是是其他
                 */
                FactoryMapGroup mapGroup = itemMap.get(factoryAnnoInfo.getTypeCanonicalName());
                if (mapGroup == null) {
                    String typeCanonicalName = factoryAnnoInfo.getTypeCanonicalName();
                    mapGroup = new FactoryMapGroup(typeCanonicalName);
                    itemMap.put(typeCanonicalName, mapGroup);
                }

                //把这个注解信息传过去
                mapGroup.add(factoryAnnoInfo);

            }
            //生成代码,type有可能是多中类型,所以有可能生成多个不同的factoryMapGroup
            for (FactoryMapGroup factoryMapGroup : itemMap.values()) {
                factoryMapGroup.generateCode(mElementUtils,mFiler);
            }
            itemMap.clear();
        } catch (ProcessingException e) {
            error(e.getElement(), e.getMessage());
        } catch (IOException e) {
            error(null, e.getMessage());
        }
        return false;
    }

    private void error(Element element, String message) {
        mMessager.printMessage(Diagnostic.Kind.ERROR,message,element);
    }

    private void checkValidClass(FactoryAnnoInfo factoryAnnoInfo) throws ProcessingException {
        //拿到元素
        TypeElement classElement = factoryAnnoInfo.getClassElement();

        //只能注解公开类
        if (!classElement.getModifiers().contains(Modifier.PUBLIC)) {
            throw new ProcessingException(classElement, "只能注解公开类%s", classElement.getQualifiedName()
                    .toString());
        }

        //不能是抽象类
        if (classElement.getModifiers().contains(Modifier.ABSTRACT)) {
            throw new ProcessingException(classElement, "不能为抽象类%s", classElement.getQualifiedName()
                    .toString());
        }

        //必须继承或者实现Factory中type类或接口
        TypeElement typeElement = mElementUtils.getTypeElement(factoryAnnoInfo.getTypeCanonicalName());
        //TypeElement getKind()返回直接由此类实现或直接由此接口扩展的接口类型。
        //typeElement.asType()获取它的类型 返回值类型TypeMirror
        if (typeElement.getKind() == ElementKind.INTERFACE) {
            //整体的意思就是,这个类实现的接口中必须包括type类型的接口
            if (!classElement.getInterfaces().contains(typeElement.asType())) {
                throw new ProcessingException(typeElement, "必须实现type中的接口%s", typeElement.getQualifiedName());
            }
        } else {
            //返回此类型元素的直接超类。如果此类型元素表示一个接口或者类 java.lang.Object，则返回一个种类为 NONE 的 NoType。
            TypeElement currentClass = classElement;
            while (true) {
                TypeMirror superclass = currentClass.getSuperclass();
                //说明父类是Objcet
                if (superclass.getKind() == TypeKind.NONE) {
                    throw new ProcessingException(classElement, "必须继承type中的类%s", classElement
                            .getQualifiedName());
                }

                //TypeMirror toString()此类型的字符串表示形式,会尽可能的返回qualifiedName,也就是 canonicalName()标注名称
                if (superclass.toString().equals(factoryAnnoInfo.getTypeCanonicalName())) {
                    break;
                }

                currentClass = (TypeElement) mTypeUtils.asElement(superclass);
            }
        }

        //用于封装它直接声明的字段、方法、构造方法和成员类型,如果没有，则返回一个空列表
        for (Element element : classElement.getEnclosedElements()) {
            if (element.getKind() == ElementKind.CONSTRUCTOR) {
                //把元素强转为一个方法元素
                ExecutableElement executableElement = (ExecutableElement) element;
                if (executableElement.getParameters().size() == 0 && executableElement.getModifiers().contains
                        (Modifier.PUBLIC)) {
                    return;
                }
        }
        }

        //没有空的构造
        throw new ProcessingException(classElement, "被注解的类必须有一个空的构造方法%", classElement.getQualifiedName());

    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new LinkedHashSet<>();
        //只支持Factory注解,getCannicalName()于getName()一般情况下是一样的,两者在内部类,数组时,返回的不一样,前者输出的方式更像导包一样的输出方式
        set.add(Factory.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


}
