package com.reconcale.frontEnd.elements;

import com.reconcale.backEnd.db.MyJDBC;
import com.reconcale.frontEnd.pages.Behavior;
import com.reconcale.frontEnd.pages.ContentPage;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Data
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public class TopSection extends VerticalLayout {
    private VerticalLayout verticalLayout;
    private  MyJDBC jdbcConnector;
    private Logger logger = LoggerFactory.getLogger(TopSection.class);

    public TopSection() {
        add(createTopSection());
    }


    public VerticalLayout createTopSection() {
        H1 pageLabel = new H1();
        pageLabel.add("QA | Simulation");


        Button behaviorButt = new Button("Behavior");
        behaviorButt.addClickShortcut(Key.KEY_B);
        behaviorButt.addClickListener(click -> {
            UI.getCurrent().navigate(Behavior.class);
        });

        Button contentButt = new Button("Content");
        contentButt.addClickShortcut(Key.KEY_C);
        contentButt.addClickListener(click -> {
            UI.getCurrent().navigate(ContentPage.class);
        });

        verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        verticalLayout.setMargin(false);
        verticalLayout.add(pageLabel, new HorizontalLayout(behaviorButt, contentButt),new Line());
        return verticalLayout;
    }


    @Autowired
    public void setJdbcConnector(MyJDBC jdbcConnector){
        this.jdbcConnector = jdbcConnector;
    }
}
