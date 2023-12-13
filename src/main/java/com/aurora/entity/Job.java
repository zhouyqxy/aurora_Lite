package com.aurora.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_job")
public class Job {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String jobName;

    private String jobGroup;

    private String invokeTarget;

    private String cronExpression;

    private Integer misfirePolicy;

    private Integer concurrent;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.UPDATE)
    private String updateTime;

    private String remark;

    @TableField(exist = false)
   private String nextValidTime;

}
