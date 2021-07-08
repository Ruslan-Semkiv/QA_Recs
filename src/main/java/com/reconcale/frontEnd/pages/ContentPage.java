package com.reconcale.frontEnd.pages;

import com.reconcale.backEnd.model.Content;
import com.reconcale.frontEnd.elements.Line;
import com.reconcale.frontEnd.elements.TopSection;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "content")
@PageTitle("Qa | Simulator")
public class ContentPage extends VerticalLayout {
    private TopSection topSection;
    private Grid<Content> contentGridList = new Grid<>();

    public ContentPage(TopSection topSection) {
        this.topSection = topSection;
        add(topSection.createTopSection(), createDbButtons(), new Line());
        createContentGrid();
    }

    public HorizontalLayout createDbButtons() {
        Button loadButton = new Button("Load", clickEvent -> {
            contentGridList.getEditor().closeEditor();
            topSection.getJdbcConnector().loadContentToDb();
            contentGridList.setItems(topSection.getJdbcConnector().getContentDb().values());
        });

        Button wipeButton = new Button("Wipe", clickEvent -> {
            topSection.getJdbcConnector().truncateContentDB();
        });

        ComboBox<String> comboBox = new ComboBox<>();
        Button runButton = new Button("Run");
        return new HorizontalLayout(loadButton, wipeButton, comboBox, runButton);
    }

    private void createContentGrid() {
        contentGridList.addThemeVariants(GridVariant.LUMO_ROW_STRIPES,GridVariant.LUMO_COLUMN_BORDERS);
        contentGridList.setItems(topSection.getJdbcConnector().getContentDb().values());


        Grid.Column<Content> idColumn = contentGridList.addColumn(Content::getId).setHeader("Id");
        Grid.Column<Content> nameColumn = contentGridList.addColumn(Content::getName).setHeader("Name");
        Grid.Column<Content> categoryColumn = contentGridList.addColumn(Content::getCategory).setHeader("Category");
        Grid.Column<Content> priceColumn = contentGridList.addColumn(Content::getPrice).setHeader("Price");


        contentGridList.addComponentColumn(content -> new Button("Delete", buttonClickEvent -> {
            topSection.getJdbcConnector().deleteContentInGridById(content.getId());
            contentGridList.setItems(topSection.getJdbcConnector().getContentDb().values());
        }));

        Binder<Content> contentBinder = new Binder<>(Content.class);
        contentGridList.getEditor().setBinder(contentBinder);

        TextField nameField = new TextField();
        TextField categoryField = new TextField();
        TextField priceField = new TextField();

        contentBinder.forField(nameField)
                .withValidator(new StringLengthValidator("Name should be more tall", 3, 20))
                .bind("name");
        nameColumn.setEditorComponent(nameField);

        contentBinder.forField(categoryField)
                .withValidator(new StringLengthValidator("Category should be more tall", 3, 20))
                .bind("category");
        categoryColumn.setEditorComponent(categoryField);

        contentBinder.forField(priceField)
                .withConverter(new StringToIntegerConverter("Price should be only created bu numbers"))
                .bind("price");
        priceColumn.setEditorComponent(priceField);

        contentGridList.addItemDoubleClickListener(event -> {
            contentGridList.getEditor().editItem(event.getItem());
            nameField.focus();
        });

        contentGridList.getEditor().addCloseListener(event -> {
            if (contentBinder.getBean() != null) {
                Content content = topSection.getJdbcConnector().getContentDb().get(event.getItem().getId());
                content.setName(event.getItem().getName());
                content.setCategory(event.getItem().getCategory());
                content.setId(event.getItem().getId());
                content.setPrice(event.getItem().getPrice());
                topSection.getJdbcConnector().getContentDb().put(content.getId(), content);
                topSection.getJdbcConnector().addNewRow();

                contentGridList.setItems(topSection.getJdbcConnector().getContentDb().values());
            }
        });

        contentGridList.setItems(topSection.getJdbcConnector().getContentDb().values());
        contentGridList.getColumns().forEach(col -> col.setAutoWidth(true));
        add(contentGridList);
    }
}
