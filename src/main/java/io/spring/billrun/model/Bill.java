package io.spring.billrun.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bill {
    private Long id;
    private String firstName;
    private String lastName;
    private Long dataUsage;
    private Long minutes;
    private Double billAmount;
}
