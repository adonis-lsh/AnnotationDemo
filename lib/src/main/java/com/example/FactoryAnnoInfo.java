package com.example;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;

/**
 * Created by adonis_lsh on 2017/7/6
 */

public class FactoryAnnoInfo {

    private TypeElement mClassElement;
    private  String mFactoryId;
    private  String mTypeClassSimpleName;
    private  String mTypeCanonicalName;

    // =======================================
    // =============这个类的主要作用就是提供id,已经对应的类名   ==============
    // =======================================
    public FactoryAnnoInfo(Element element) throws ProcessingException {
        mClassElement = (TypeElement) element;
        Factory annotation = mClassElement.getAnnotation(Factory.class);

        mFactoryId = annotation.id();
        //Returns the fully qualified name of this type element. More precisely, it returns the canonical
        // name. For local and anonymous classes, which do not have canonical names, an empty name is
        // returned.
        //  The name of a generic type does not include any reference to its formal type parameters. For
        // example, the fully qualified name of the interface java.util.Set<E> is "java.util.Set". Nested
        // types use
        // "." as a separator, as in "java.util.Map.Entry".
        //从官方的文档上可以看出QualifiedName() = canonicalName()
        if (mFactoryId.isEmpty()) {
            throw new ProcessingException(element, "必须有ID%s", ((TypeElement) element).getQualifiedName());
        }

        try {
            Class clazz = annotation.type();
            mTypeClassSimpleName = clazz.getSimpleName();
            mTypeCanonicalName = clazz.getCanonicalName();
        } catch (MirroredTypeException e) {
            //TypeMirror是一个类型接口,这些类型包括基本类型、声明类型（类和接口类型）、数组类型、类型变量和 null 类型。还可以表示通配符类型参数、executable
            // 的签名和返回类型，以及对应于包和关键字 void 的伪类型。,getKind(),返回次类型的种类

            //TypeElement 表示一个类或接口元素，而 DeclaredType 
            // 表示一个类或接口类型，后者将成为前者的一种使用（或调用）。这种区别对于一般的类型是最明显的，对于这些类型，单个元素可以定义一系列完整的类型。例如，元素 java.util.Set 
            // 对应于参数化类型 java.util.Set<String> 和 java.util.Set<Number>（以及其他许多类型），还对应于原始类型 java.util.Set。
            
            //就是这样是元素的类型,不过是一个类或者接口的类型
            DeclaredType typeMirror = (DeclaredType) e.getTypeMirror();

            //返回此类型对应的元素
            TypeElement typeElement = (TypeElement) typeMirror.asElement();
            mTypeCanonicalName = typeElement.getQualifiedName().toString();
            mTypeClassSimpleName = typeElement.getSimpleName().toString();
        }
    }

    public TypeElement getClassElement() {
        return mClassElement;
    }

    public String getFactoryId() {
        return mFactoryId;
    }

    public String getTypeClassSimpleName() {
        return mTypeClassSimpleName;
    }

    public String getTypeCanonicalName() {
        return mTypeCanonicalName;
    }
}
