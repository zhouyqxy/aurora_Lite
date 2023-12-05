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
public class JuheToutiao {
    private String Stat;
    private List<JuheToutiaoData> Data;
    private String Page;
    private String PageSize;
}
