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
@TableName("t_category")
public class Category {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String categoryName;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updateTime;

}
