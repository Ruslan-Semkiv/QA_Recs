package com.reconcale.backEnd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VolatileProperty implements Serializable{

    private Long id;
    private String owner; // Discriminator: User, Content
    private String referenceId;
    private String name;
    private String type;
    private String value;
    private Date startDate;
    private Date endDate;
}
