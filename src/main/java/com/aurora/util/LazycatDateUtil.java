package com.aurora.util;

import org.apache.commons.lang3.time.DateUtils;

import java.time.*;
import java.util.Date;

/**
 * @program: aurora_Lite
 * @description: 时间转换工具
 * @author: jonk
 * @create: 2023-12-05 15:47
 **/

public class LazycatDateUtil {

    public static LocalDateTime timestamToDatetime(String timestamp){
        return timestamToDatetime(Long.parseLong(timestamp));
    }

    public static LocalDateTime timestamToDatetime(long timestamp){
        if(timestamp==0){
            return null;
        }
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static long datatimeToTimestamp(LocalDateTime ldt){
        long timestamp = ldt.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return timestamp;
    }

    //将java.util.Date 转换为java8 的java.time.LocalDateTime,默认时区为东8区
    public static LocalDateTime dateConvertToLocalDateTime(Date date) {
        return date.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime();
    }


    //将java8 的 java.time.LocalDateTime 转换为 java.util.Date，默认时区为东8区
    public static Date localDateTimeConvertToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.toInstant(ZoneOffset.of("+8")));
    }

    //将java8 的 java.time.LocalDateTime 转换为 java.util.Date，默认时区为东8区
    public static String  dateTimeConvertToTimestamp(Date date) {
        return  date.getTime()+"";
    }


    public static String localDateTimeToTim(LocalDateTime localDateTime) {
        return  datatimeToTimestamp(localDateTime)+"";

    }

    public static Date timestamToDate(Long createTime) {
      return   new Date(createTime);
    }
}