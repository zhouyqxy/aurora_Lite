package com.aurora.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_topic")
public class Topic {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 课题名称
     */
    private String topicName;
    /**
     * 项目数量
     */
    private Integer projectNum;
    /**
     * 收录标记
     */
    private Integer pullTag;
    private String giteeUrl;


}
