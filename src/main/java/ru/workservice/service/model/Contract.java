package ru.workservice.service.model;

import lombok.*;

import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Contract {
    private String contractNumber;
    private LocalDate contractDate;
    private Integer additionalAgreement;

    @Override
    public String toString() {
        return "№ " + contractNumber + " от " + contractDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))
                + (additionalAgreement != null ? " д.c. " + additionalAgreement : "");
    }
}
