package com.aurora.juhe.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data  
@Builder  
@NoArgsConstructor  
@AllArgsConstructor  
public class JuheToutiaoDetail {
private String Uniquekey;
private JuheToutiaoDetailDetail Detail;
private String Content;
}