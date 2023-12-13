package com.aurora.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_about")
public class About {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String content;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updateTime;

}
