package com.reconcale.backEnd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recommendation {
    private  Long id;
    private String name;
    private Double relevance;
    private Integer debug;

}
