package com.apihub.admin.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 接口上线 / 下线请求。
 */
@Data
public class InterfaceStatusRequest {

    /** 1 上线 0 下线 */
    @NotNull(message = "status 不能为空")
    @Min(value = 0, message = "status 只能为 0（下线）或 1（上线）")
    @Max(value = 1, message = "status 只能为 0（下线）或 1（上线）")
    private Integer status;
}
