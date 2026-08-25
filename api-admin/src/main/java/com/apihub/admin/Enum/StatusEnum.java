package com.apihub.admin.Enum;

import com.baomidou.mybatisplus.annotation.EnumValue;

public enum StatusEnum {


    /*
    启用
     */
    ENABLE(1,"启用"),
    /*
    禁用
     */
    DISABLE(0,"禁用");

    /** 数据库存储值（1 启用 0 禁用），MyBatis-Plus 按此映射 */
    @EnumValue
    private final Integer code;

    private final String message;

    StatusEnum(Integer code, String message) {

        this.code = code;

        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    /** 按数据库存储值解析；无法识别时返回 null */
    public static StatusEnum fromCode(Integer code) {
        if (code == null) {
            return null;
        }
        for (StatusEnum item : values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }
}
