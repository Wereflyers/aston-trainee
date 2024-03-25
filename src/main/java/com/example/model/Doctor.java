package com.example.model;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Doctor {
    private Long id;
    private String name;
    private String specialty;
}
