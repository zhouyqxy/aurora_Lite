package com.aurora.juhe.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JuheNetworkhotResult {
private List<JuheNetworkhotResultList> list;

}