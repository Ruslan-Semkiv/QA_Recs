package com.reconcale.backEnd.entity;

import com.reconcale.backEnd.model.Content;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

@Data
@AllArgsConstructor
public class CustomerVisit {
    private UUID id;
    private String visitName;
    private List<VisitCell> visitCells = new LinkedList<>();
    private String timeCreation;
    private Customer visitOwner;

    public CustomerVisit() {
        Random random = new Random();
        visitCells.add(new VisitCell(random.nextInt(100),"BUY",2L,"Product of blala",this, new Content(20,"Coca",100, "MEal", "Small","24/7","Fresh drink","Article","None")));
        visitCells.add(new VisitCell(random.nextInt(100),"SELL",5L,"Nice meal",this, new Content(6,"Mentos",135, "Meal", "Medium","24/7","Delicious sweet","Article","None")));
        visitCells.add(new VisitCell(random.nextInt(100),"BUY",9L,"i like it",this, new Content(14,"Chocolate",350 ,"Meal", "Medium","24/7","From cacao","Article","None")));

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomerVisit that = (CustomerVisit) o;
        return Objects.equals(id, that.id) && Objects.equals(visitName, that.visitName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, visitName);
    }

    @Override
    public String toString() {
        return "CustomerVisit{" +
                "id=" + id +
                ", visitName='" + visitName + '\'' +
                ", timeCreation='" + timeCreation + '\'' +
                ", visitOwner = {id}[" + visitOwner.getId()+"] {name}["+ visitOwner.getFirstName()+"]" +
                '}';
    }
}
