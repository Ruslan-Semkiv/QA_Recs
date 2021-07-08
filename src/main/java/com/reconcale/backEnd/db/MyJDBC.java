package com.reconcale.backEnd.db;

import com.reconcale.backEnd.entity.Customer;
import com.reconcale.backEnd.entity.CustomerVisit;
import com.reconcale.backEnd.entity.VisitCell;
import com.reconcale.backEnd.model.Content;
import com.reconcale.backEnd.session.SessionContext;
import com.vaadin.flow.component.notification.Notification;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Data
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Component
public class MyJDBC {
    private SessionContext sessionContext;
    public Integer contentGridRowCounter = 1;
    public final Content EMPTY_CONTENT_ROW = new Content(1, "-", 0, "-", "-", "-", "-", "-", "-");
    private Map<UUID, Customer> usersDb = new LinkedHashMap<>();
    private Map<Integer, Content> contentDb = new LinkedHashMap<>();
    private Connection connection;
    private Logger logger = LoggerFactory.getLogger(MyJDBC.class);
    private PreparedStatement preparedStatement;
    private Map<UUID, Customer> inMemoryCustomers = new LinkedHashMap<>();

    public MyJDBC(SessionContext sessionContext) throws SQLException {
        this.sessionContext = sessionContext;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/StartuProject?serverTimezone=UTC", "RuslanSemkiv", "ukraina ternopil 00725");
        } catch (SQLException throwable) {
            logger.error("Connection with MySQL , wasn't run successfully - class [MyJDBC]");
        }
        contentDb.put(contentGridRowCounter, new Content(contentGridRowCounter++, "-", 0, "-", "-", "-", "-", "-", "-"));
        contentDb.put(contentGridRowCounter, new Content(contentGridRowCounter++, "-", 0, "-", "-", "-", "-", "-", "-"));
    }


    public String loadContainedValues() {
        if (usersDb.size() != 0) {
            for (Customer customer : usersDb.values()) {
                if (!inMemoryCustomers.containsKey(customer.getId())) {
                    inMemoryCustomers.put(customer.getId(), customer);
                    for (CustomerVisit visit : customer.getOwnVisits()) {
                        for (VisitCell cell : visit.getVisitCells()) {
                            try {
                                preparedStatement = connection.prepareStatement("insert into actions (user,content,event,argument,time,scope,context,dataScope,createDate)" +
                                        " values (?,?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
                                preparedStatement.setString(1, customer.getId().toString());
                                preparedStatement.setString(2, cell.getContent().getId().toString());
                                preparedStatement.setString(3, cell.getEvent());
                                preparedStatement.setString(4, cell.getArgument().toString());
                                preparedStatement.setString(5, cell.getOwner().getTimeCreation());
                                preparedStatement.setString(6, cell.getOwner().getId().toString());
                                preparedStatement.setString(7, visit.getVisitName());
                                preparedStatement.setString(8, customer.getFirstName());
                                preparedStatement.setString(9, LocalDate.now().toString());
                                preparedStatement.executeUpdate();

                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            return "Successfully";
        }
        return "Failed";
    }

    public void truncateActionTable() throws SQLException {
        preparedStatement = connection.prepareStatement("TRUNCATE TABLE actions;");
        preparedStatement.executeUpdate();
        getUsersDb().clear();
        getInMemoryCustomers().clear();
    }

    public void performPrepareBeforeLoad() {
        if (sessionContext.getUserInAction() != null && sessionContext.getVisitInAction() != null) {
            if (sessionContext.getVisitInAction().getVisitOwner().getId().equals(sessionContext.getUserInAction().getId())) {
                sessionContext.getUserInAction().getOwnVisits().add(sessionContext.getVisitInAction());
            } else {
                usersDb.values().stream().filter(cst -> cst.getId().equals(sessionContext.getVisitInAction().getVisitOwner().getId())).
                        map(per -> per.getOwnVisits().add(sessionContext.getVisitInAction())).count();
            }
            usersDb.put(sessionContext.getUserInAction().getId(), sessionContext.getUserInAction());

            sessionContext.setUserInAction(null);
            sessionContext.setVisitInAction(null);
        }
    }

    public void createVisitInSession(String visitName, String visitCreationTime) {
        if (sessionContext.getVisitInAction() != null) {
            if (sessionContext.getVisitInAction().getVisitOwner().getId().equals(sessionContext.getUserInAction().getId())) {
                sessionContext.getUserInAction().getOwnVisits().add(sessionContext.getVisitInAction());
                Notification.show("Previous visit was terminate");
            } else {
                getUsersDb().values().stream()
                        .filter(cs -> cs.getId().equals(sessionContext.getVisitInAction().getVisitOwner().getId()))
                        .map(usr -> usr.getOwnVisits().add(sessionContext.getVisitInAction())).count();
            }
        }

        CustomerVisit visitInAction = new CustomerVisit();
        visitInAction.setId(UUID.randomUUID());
        visitInAction.setVisitName(visitName);
        visitInAction.setTimeCreation(visitCreationTime);
        visitInAction.setVisitOwner(sessionContext.getUserInAction());

        sessionContext.setVisitInAction(visitInAction);
    }

    public void createUserInAction(String username) {
        Customer customerForSave = new Customer();

        if (sessionContext.getUserInAction() != null) {
            getUsersDb().put(sessionContext.getUserInAction().getId(), sessionContext.getUserInAction());
            Notification.show("Previous user was successfully saved ....");
        }
        customerForSave.setId(UUID.randomUUID());
        customerForSave.setFirstName(username);
        sessionContext.setUserInAction(customerForSave);
    }

    public void deleteCellInCustomerVisit(VisitCell cell) {
        CustomerVisit tempVisit = cell.getOwner();
        Customer customerForUpdating = tempVisit.getVisitOwner();

        int visitIndex = customerForUpdating.getOwnVisits().indexOf(tempVisit);

        tempVisit.getVisitCells().removeIf(tempCell -> tempCell.getId().equals(cell.getId()));
        customerForUpdating.getOwnVisits().removeIf(visit -> visit.getId().equals(tempVisit.getId()));
        customerForUpdating.getOwnVisits().add(visitIndex, tempVisit);

        usersDb.put(customerForUpdating.getId(), customerForUpdating);
    }


    public void loadContentToDb() {
        try {
            for (Content content : contentDb.values()) {
                if (!content.equalsForEmpty(EMPTY_CONTENT_ROW)) {
                    preparedStatement = connection.prepareStatement("insert into contents (name,price,category,groupers,market,family,article,hasImage)" +
                            " values (?,?,?,?,?,?,?,?)");
                    preparedStatement.setString(1, content.getName());
                    preparedStatement.setLong(2, content.getPrice());
                    preparedStatement.setString(3, content.getCategory());
                    preparedStatement.setString(4, content.getGroupers());
                    preparedStatement.setString(5, content.getMarket());
                    preparedStatement.setString(6, content.getFamily());
                    preparedStatement.setString(7, content.getArticle());
                    preparedStatement.setString(8, content.getHasImage());
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        contentDb.clear();
        emptyContentRow();
        Notification.show("Content was successfully uploaded");
    }

    public void truncateContentDB() {
        contentDb.clear();
        emptyContentRow();

        try {
            connection.prepareStatement("TRUNCATE TABLE contents;").executeUpdate();
        } catch (SQLException e) {
            logger.error("Cannot execute -> [ TRUNCATE TABLE contents ];");
        }
        Notification.show("Content was deleted from DB");
    }

    public void deleteContentInGridById(Integer id) {
        for (Content content : contentDb.values()) {
            if (content.getId().equals(id)) {
                if (contentDb.size() == 1) {
                    contentDb.remove(id);
                    emptyContentRow();
                }
                contentDb.remove(id);
                logger.info("Content wit id {} was deleted successfully", id);
                break;
            }
        }

        updateContentID();
    }

    private void updateContentID() {
        contentGridRowCounter = 1;
        List<Content> tempCont = contentDb.values().stream().peek(content -> content.setId(contentGridRowCounter++)).collect(Collectors.toList());
        contentDb.clear();
        for (Content content : tempCont) {
            contentDb.put(content.getId(), content);
        }
    }

    private void emptyContentRow() {
        contentGridRowCounter = 1;
        contentDb.put(contentGridRowCounter, new Content(contentGridRowCounter++, "-", 0, "-", "-", "-", "-", "-", "-"));
        contentDb.put(contentGridRowCounter, new Content(contentGridRowCounter++, "-", 0, "-", "-", "-", "-", "-", "-"));
    }

    public void addNewRow() {
        contentDb.put(contentGridRowCounter, new Content(contentGridRowCounter++, "-", 0, "-", "-", "-", "-", "-", "-"));
    }

}
