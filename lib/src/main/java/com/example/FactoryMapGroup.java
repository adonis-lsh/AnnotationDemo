package com.example;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by adonis_lsh on 2017/7/6
 */

// =============传过来一个FactoryAnnoIfno对象,我们就把对应的id和对象保存起来,因为对象里面的属性都是我们将来写文件的时候要用的  ==============
public class FactoryMapGroup {


    private String mQualifiedClassName;

    //定义一个map集合
    Map<String, FactoryAnnoInfo> itemMap = new LinkedHashMap<>();

    public FactoryMapGroup(String qualifiedClassName) {
        mQualifiedClassName = qualifiedClassName;
    }

    public void add(FactoryAnnoInfo factoryAnnoInfo) throws ProcessingException {
        String factoryId = factoryAnnoInfo.getFactoryId();
        FactoryAnnoInfo annoInfo = itemMap.get(factoryId);
        if (annoInfo != null) {
            throw new ProcessingException(factoryAnnoInfo.getClassElement(), "id已经存在%s", annoInfo
                    .getClassElement().getQualifiedName());
        }
        itemMap.put(factoryId, factoryAnnoInfo);
    }

    public void generateCode(Elements elementUtils, Filer filer) throws IOException{
        TypeElement typeElement = elementUtils.getTypeElement(mQualifiedClassName);
        String factoryName = typeElement.getSimpleName() + "Factory";
        PackageElement pkgElement = elementUtils.getPackageOf(typeElement);
        //如果此包是一个未命名的包，则返回 true，否则返回 false。
        String pkgName = pkgElement.isUnnamed() ? null : pkgElement.getQualifiedName().toString();

        MethodSpec.Builder builder = MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC)
                .addParameter(String.class, "id")
                .returns(TypeName.get(typeElement.asType()));

        builder.beginControlFlow("if(id == null)")
                .addStatement("throw new IllegalArgumentException($S)", "id is null!")
                .endControlFlow();

        for (FactoryAnnoInfo factoryAnnoInfo : itemMap.values()) {
            builder.beginControlFlow("if($S.equals(id))", factoryAnnoInfo.getFactoryId())
                    .addStatement("return new $L()", factoryAnnoInfo.getClassElement().getQualifiedName()
                            .toString())
                    .endControlFlow();
        }

        builder.addStatement("throw new IllegalArgumentException($S + id)", "Unknown id = ");

        TypeSpec typeSpec = TypeSpec.classBuilder(factoryName)
                .addMethod(builder.build())
                .addModifiers(Modifier.PUBLIC)
                .build();

        JavaFile.builder(pkgName, typeSpec).build().writeTo(filer);
    }
}
