package com.reconcale.frontEnd.elements;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;

//Element for pages styling
@Tag("vaadin-line")
public class Line extends Div {
    public Line() {
        getStyle().set("width","100%").set("border-top","4px solid blue");
    }
}