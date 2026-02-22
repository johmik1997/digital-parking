package com.example.digitalparking.Dto.Request;


import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customization {
    private String title;
    private String description;
    private String logo;
}