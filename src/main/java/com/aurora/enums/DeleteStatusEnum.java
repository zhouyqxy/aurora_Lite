package com.aurora.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeleteStatusEnum {

    F(0, "否"),

    Y(1, "是");

    private final Integer status;

    private final String desc;

}
