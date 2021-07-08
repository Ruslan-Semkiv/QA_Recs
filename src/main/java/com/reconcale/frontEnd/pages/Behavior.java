package com.reconcale.frontEnd.pages;

import com.reconcale.backEnd.db.MyJDBC;
import com.reconcale.backEnd.entity.Customer;
import com.reconcale.backEnd.entity.CustomerVisit;
import com.reconcale.backEnd.entity.VisitCell;
import com.reconcale.backEnd.model.Content;
import com.reconcale.frontEnd.elements.Line;
import com.reconcale.frontEnd.elements.TopSection;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

@Route(value = "behavior")
@PageTitle("Qa | Simulator")
@CssImport(value = "./styles/upload-grid.css")
public class Behavior extends VerticalLayout {
    private VerticalLayout userLayout = new VerticalLayout();
    private Logger logger = LoggerFactory.getLogger(Behavior.class);
    private TopSection topSection;
    private final MyJDBC jdbcConnector;
    private TextField userField = new TextField();
    private TextField visitField = new TextField();
    private DatePicker visitCreationTime;

    public Behavior(MyJDBC jdbcConnector, TopSection topSection) {
        this.jdbcConnector = jdbcConnector;
        this.topSection = topSection;
        add(topSection.createTopSection(), buttonForDb(), sectionUserAndVisit(), new Line());
    }


    public HorizontalLayout buttonForDb() {
        Button loadButt = new Button("Load");
        loadButt.addClickShortcut(Key.ENTER);
        loadButt.addClickListener(click -> {
            jdbcConnector.performPrepareBeforeLoad();
            if (jdbcConnector.loadContainedValues().equals("Successfully")) {
                Notification.show("Data was successfully uploaded to the DB");
            } else {
                Notification.show("Data for upload are empty");
            }
            add(listOfUsers());
        });

        Button deleteButt = new Button("Wipe", click -> {
            try {
                userLayout.removeAll();
                jdbcConnector.truncateActionTable();
            } catch (SQLException e) {
                logger.error("Wipe all members in ACTIONS wasn't successfully");
            }
            jdbcConnector.getSessionContext().setUserInAction(null);
            jdbcConnector.getSessionContext().setVisitInAction(null);
            Notification.show("Now you should to create new USER");
        });
        deleteButt.addClickShortcut(Key.DELETE);

        ComboBox<String> useAlgorithm = new ComboBox<>();

        Button runButt = new Button("Run");

        return new HorizontalLayout(loadButt, deleteButt, useAlgorithm, runButt);
    }

    public HorizontalLayout sectionUserAndVisit() {
        userField.setPlaceholder("User name");
        userField.setClearButtonVisible(true);
        Button createUser = new Button("Create user");
        createUser.addClickListener(click -> {
            createNewUser();
        });

        visitField.setPlaceholder("Enter some name");
        visitCreationTime = new DatePicker();
        visitCreationTime.setValue(LocalDate.now());
        visitCreationTime.setReadOnly(true);

        Button createVisitButt = new Button("Create visit");
        createVisitButt.addClickListener(click -> {
            createVisit();
        });
        return new HorizontalLayout(userField, createUser, visitField, visitCreationTime, createVisitButt);
    }

    public void createVisit() {
        if (jdbcConnector.getSessionContext().getUserInAction() == null) {
            Notification.show("First you should create User");
            return;
        } else if (visitField.getValue().isEmpty() || visitField.getValue() == null) {
            Notification.show("Visit name cannot be empty");
            return;
        }
        jdbcConnector.createVisitInSession(visitField.getValue(), visitCreationTime.getValue().toString());

        Notification.show("Visit was created successfully");
        visitField.clear();
    }

    public void createNewUser() {
        if (userField.getValue() == null || userField.getValue().isEmpty()) {
            Notification.show("User name cannot be empty");
            return;
        }
        jdbcConnector.createUserInAction(userField.getValue());

        userField.clear();
        Notification.show("User created successfully");
        Notification.show("Now you in " + jdbcConnector.getSessionContext().getUserInAction().getFirstName() + " session");
    }


    private VerticalLayout listOfUsers() {
        userLayout.removeAll();
        userLayout.setSpacing(true);

        for (Customer customer : jdbcConnector.getUsersDb().values()) {
            VerticalLayout visitLayout = new VerticalLayout();

            for (CustomerVisit visit : customer.getOwnVisits()) {
                HorizontalLayout visitTempLay = new HorizontalLayout();

                Div visitDiv = new Div();
                visitDiv.setId(visit.getId().toString());
                visitDiv.add(visit.getVisitName());
                visitDiv.setClassName("visit-box");

                Button deleteVisit = new Button("Delete", click -> {
                    jdbcConnector.getUsersDb().get(customer.getId())
                            .getOwnVisits().remove(visit);
                    listOfUsers();
                });
                visitTempLay.add(visitDiv, deleteVisit);
                visitTempLay.setMargin(false);
                visitTempLay.setAlignItems(Alignment.BASELINE);
                visitLayout.add(visitTempLay, setItemsForGrid(visit));
            }
            Div userDiv = new Div();
            UUID customerId = customer.getId();
            userDiv.setText(customer.getFirstName());
            userDiv.setClassName("user-box");

            HorizontalLayout customerCaptionLayout = new HorizontalLayout(
                    userDiv, new Button("Delete", buttonClickEvent -> {
                jdbcConnector.getUsersDb().remove(customerId);
                listOfUsers();
            }));

            customerCaptionLayout.setMargin(false);
            customerCaptionLayout.setAlignItems(Alignment.START);
            userLayout.add(customerCaptionLayout);
            userLayout.setAlignItems(Alignment.BASELINE);
            userLayout.add(visitLayout);
        }

        return userLayout;
    }

    private Grid<VisitCell> setItemsForGrid(CustomerVisit visit) {
        Grid<VisitCell> cellGrid = new Grid<>();
        cellGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_COLUMN_BORDERS);

        cellGrid.addColumn(content -> {
            Content cont = content.getContent();
            return cont == null ? "-" : cont.getId();
        }).setHeader("Content").getElement().getStyle().set("background", "red");

        cellGrid.addColumn(event -> {
            String eve = event.getEvent();
            return eve == null ? "-" : eve;
        }).setHeader("Event").getElement().getStyle().set("background", "red");


        cellGrid.addColumn(VisitCell::getArgument).setHeader("Argument");

        cellGrid.addColumn(context -> {
            String description = context.getContext();
            return description == null ? "-" : description;
        }).setHeader("Context").getElement().getStyle().set("background", "red");


        cellGrid.addComponentColumn(event -> new Button("Delete", buttonClickEvent -> {
            topSection.getJdbcConnector()
                    .deleteCellInCustomerVisit(event);

            cellGrid.setItems(visit.getVisitCells());
        })).setHeader("Delete").getElement().getStyle().set("background", "red");

        cellGrid.setItems(visit.getVisitCells());
        cellGrid.setHeightByRows(true);

        return cellGrid;
    }
}
