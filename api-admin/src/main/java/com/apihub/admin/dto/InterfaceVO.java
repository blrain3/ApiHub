package com.apihub.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 接口资产对外展示对象。
 */
@Data
public class InterfaceVO {

    private Long id;
    private String name;
    private String path;
    private String method;
    private String description;
    private String version;
    private String category;
    /** 0 下线 1 上线 */
    private Integer status;
    private LocalDateTime createTime;
}
