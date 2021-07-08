package com.reconcale.frontEnd.pages;

import com.reconcale.backEnd.db.MyJDBC;
import com.reconcale.backEnd.entity.Customer;
import com.reconcale.frontEnd.elements.Line;
import com.reconcale.backEnd.model.Recommendation;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import java.util.List;


@Route(value = "recs")
public class Recs extends VerticalLayout {
    private MyJDBC jdbc;

    public Recs(MyJDBC jdbc){
        this.jdbc = jdbc;
        add(createMainPart(),new Line());
        produceGrid();
    }

    private VerticalLayout createMainPart() {
        H1 qaRecs = new H1("QA | Recs");

        ComboBox<Customer> customerList = new ComboBox<>("User");
        customerList.setItems(jdbc.getUsersDb().values());
        customerList.setItemLabelGenerator(Customer::getFirstName);

        ComboBox<String> useCaseList = new ComboBox<>("Use Case");
        useCaseList.setItems(List.of("Electronica","Clothes","Home","Sport"));
        TextField productField = new TextField();
        Button selectButton = new Button("Select");
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(customerList,useCaseList,productField,selectButton);
        horizontalLayout.setAlignItems(Alignment.END);
        return new VerticalLayout(qaRecs,horizontalLayout);
    }

    private void produceGrid (){
        Grid<Recommendation> recommendationGrid = new Grid<>(Recommendation.class);
        recommendationGrid.setColumns("id","name","relevance","debug");

        recommendationGrid.addComponentColumn(event -> new Button("Delete")).setHeader("Delete");
        recommendationGrid.getColumns().forEach(col -> col.setAutoWidth(true));
        add(recommendationGrid);
    }

}
