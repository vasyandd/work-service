package ru.workservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExportTableRow {
    private String productOrContract;
    private String period;
    private String actualProductQuantity;
    private String scheduledProductQuantity;
    private String productPrice;
    private String janQuantity;
    private String febQuantity;
    private String marQuantity;
    private String aprQuantity;
    private String mayQuantity;
    private String junQuantity;
    private String julQuantity;
    private String augQuantity;
    private String sepQuantity;
    private String octQuantity;
    private String novQuantity;
    private String decQuantity;
    private String note;
}
