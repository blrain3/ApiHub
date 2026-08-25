package com.apihub.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 创建接口资产请求。
 */
@Data
public class InterfaceCreateRequest {

    @NotBlank(message = "接口名称不能为空")
    @Size(max = 128, message = "接口名称最长 128 字符")
    private String name;

    @NotBlank(message = "接口路径不能为空")
    @Size(max = 256, message = "接口路径最长 256 字符")
    private String path;

    /** GET / POST / PUT / DELETE / PATCH */
    @NotBlank(message = "请求方法不能为空")
    @Size(max = 16, message = "请求方法不合法")
    private String method;

    @Size(max = 512, message = "描述最长 512 字符")
    private String description;

    @Size(max = 16, message = "版本最长 16 字符")
    private String version;

    @Size(max = 64, message = "分类最长 64 字符")
    private String category;
}
