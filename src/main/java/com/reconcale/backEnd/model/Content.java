package com.reconcale.backEnd.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Content {

    private Integer id;
    @NotNull
    @NotEmpty
    @Length(min = 2)
    private String name;
    @NotNull
    @NotBlank
    @Pattern(regexp = "[0-9]*", message = "Only numbers")
    private Integer price;
    @NotNull
    @NotEmpty
    @Pattern(regexp = "[A-Za-z]*", message = "Only numbers")
    private String category;

    private String groupers;

    private String market;

    private String family;

    private String article;

    private String hasImage;


    public boolean equalsForEmpty(Content o) {
        return this.name.equals(o.name) && this.price.equals(o.price) && this.category.equals(o.category) && this.groupers.equals(o.groupers) && this.market.equals(o.market) && this.family.equals(o.family) && this.article.equals(o.article) && this.hasImage.equals(o.hasImage);
    }
}


