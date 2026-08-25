package com.apihub.admin.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum HttpMethodEnum {

    /*
       查询方法
     */
    GET("GET","获取"),
    /*
    新增方法
     */
    POST("POST","新增"),
    /*
    修改方法
     */
    PUT("PUT","修改"),
    /*
   删除方法
    */
    DELETE("DELETE","删除"),
    /*
    部分修改方法
*/
    PATCH("PATCH","部分修改");

    /** 数据库存储值（如 GET/POST），MyBatis-Plus 按此映射 */
    @EnumValue
    private final String code;

    private final String desc;

    HttpMethodEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /** 按 HTTP 方法名解析，忽略大小写；无法识别时返回 null */
    public static HttpMethodEnum fromCode(String code) {
        if (code == null || code.isBlank()) {
            return null;
        }
        String normalized = code.trim().toUpperCase();
        for (HttpMethodEnum item : values()) {
            if (item.code.equals(normalized)) {
                return item;
            }
        }
        return null;
    }
}
