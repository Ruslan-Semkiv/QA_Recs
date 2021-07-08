package com.reconcale.backEnd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Action {

    private Long id;

    private String userId;

    private String contentId;

    private String event;

    private String argument;

    // Time of visit
    private LocalDate time;

    private String scope;

    private String context;

    private String dataScope;

    private Long createDate = new Date().getTime();

    public Action(String userId, String contentId, String event, String argument, LocalDate time, String scope, String context) {
        this.userId = userId;
        this.contentId = contentId;
        this.event = event;
        this.argument = argument;
        this.time = time;
        this.scope = scope;
        this.context = context;
    }

}