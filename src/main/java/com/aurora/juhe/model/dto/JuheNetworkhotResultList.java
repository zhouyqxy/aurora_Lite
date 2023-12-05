package com.aurora.juhe.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JuheNetworkhotResultList {
    private String title;
    private Number hotnum;
    private String digest;


}