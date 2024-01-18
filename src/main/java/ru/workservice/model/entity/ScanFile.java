package ru.workservice.model.entity;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Entity
@Table(name = "FILES")
public class ScanFile {
    @Id
    @GeneratedValue
    private Long id;

    private byte[] content;
    private String name;
    private String extension;

    @ManyToOne
    @JoinColumn(name = "ds_id")
    private DeliveryStatement deliveryStatement;
}