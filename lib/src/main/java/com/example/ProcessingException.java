package com.example;

import javax.lang.model.element.Element;

/**
 * Created by adonis_lsh on 2017/7/6
 */

class ProcessingException extends Exception {
    private  Element mElement;

    public ProcessingException(Element element, String msg, Object... args) {
        mElement = element;
    }

    public Element getElement() {
        return mElement;
    }
}
