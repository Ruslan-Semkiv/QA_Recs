package com.reconcale.backEnd.entity;

import com.reconcale.backEnd.model.Content;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Random;

@Data
@Slf4j
@NoArgsConstructor
public class VisitCell {
    private Logger logger = LoggerFactory.getLogger(VisitCell.class);

    private Long id;

    private Event event;

    private Long argument;

    private String context;

    private CustomerVisit owner;

    private Content content;

    public VisitCell(Integer id , String event, Long argument, String context, CustomerVisit owner, Content content) {
        this.id = Long.valueOf(id);
        this.event = Event.valueOf(event);
        this.argument = argument;
        this.context = context;
        this.owner = owner;
        this.content =content;
    }

    private enum Event {
        BUY, SELL , NONE
    }

    public String getEvent() {
        return event != null ? event.toString() : Event.NONE.toString();
    }

    public void setEvent(String event) {
        if (event != null) {
            try {
                this.event = Event.valueOf(event.toUpperCase());
            } catch (IllegalArgumentException exe) {
                logger.warn("Event in VisitCell doesn't exist");
            }
        }
    }

    public Long getArgument() {
        return argument != null ? argument : 0;
    }

    public void setArgument(Long argument) {
        this.argument = argument;
    }

    public String getContext() {
        return context != null ? context : "Some context";
    }

    public void setContext(String context) {
        this.context = context;
    }

    public CustomerVisit getOwner() {
        return owner;
    }

    public void setOwner(CustomerVisit owner) {
        this.owner = owner;
    }

    public Content getContent() {
        Random random = new Random();
        return content != null ? content : new Content(random.nextInt(100),"Coca",100, "MEAL" ,"SMALL","Fresh drink","Drink","Fresh drink","None");
    }

    public void setContent(Content content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VisitCell visitCell = (VisitCell) o;
        return Objects.equals(id, visitCell.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

