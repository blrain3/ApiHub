package com.apihub.admin.controller;

import com.apihub.admin.dto.InterfaceCreateRequest;
import com.apihub.admin.dto.InterfaceStatusRequest;
import com.apihub.admin.dto.InterfaceUpdateRequest;
import com.apihub.admin.dto.InterfaceVO;
import com.apihub.admin.service.InterfaceService;
import com.apihub.common.constant.ApiHeaders;
import com.apihub.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 接口资产管理：列表、详情、创建、编辑、上下线、删除。
 */
@Tag(name = "接口资产", description = "开放接口资产 CRUD 与上下线")
@RestController
@RequestMapping("/admin/interfaces")
public class InterfaceController {

    private final InterfaceService interfaceService;

    public InterfaceController(InterfaceService interfaceService) {
        this.interfaceService = interfaceService;
    }

    @Operation(summary = "接口列表", description = "普通用户仅已上线；管理员可按 keyword/status 筛选")
    @GetMapping
    public Result<List<InterfaceVO>> list(@RequestParam(required = false) String keyword,
                                          @RequestParam(required = false) Integer status,
                                          @RequestHeader(value = ApiHeaders.USER_ROLES, required = false) String roles) {
        return Result.ok(interfaceService.list(keyword, status, roles));
    }

    @Operation(summary = "接口详情")
    @GetMapping("/{id}")
    public Result<InterfaceVO> detail(@PathVariable Long id,
                                      @RequestHeader(value = ApiHeaders.USER_ROLES, required = false) String roles) {
        return Result.ok(interfaceService.detail(id, roles));
    }

    @Operation(summary = "创建接口", description = "仅管理员；新建默认下线")
    @PostMapping
    public Result<InterfaceVO> create(@Valid @RequestBody InterfaceCreateRequest request,
                                      @RequestHeader(value = ApiHeaders.USER_ROLES, required = false) String roles) {
        return Result.ok(interfaceService.create(request, roles));
    }

    @Operation(summary = "更新接口", description = "仅管理员")
    @PutMapping("/{id}")
    public Result<InterfaceVO> update(@PathVariable Long id,
                                      @Valid @RequestBody InterfaceUpdateRequest request,
                                      @RequestHeader(value = ApiHeaders.USER_ROLES, required = false) String roles) {
        return Result.ok(interfaceService.update(id, request, roles));
    }

    @Operation(summary = "上线/下线接口", description = "仅管理员")
    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id,
                                     @Valid @RequestBody InterfaceStatusRequest request,
                                     @RequestHeader(value = ApiHeaders.USER_ROLES, required = false) String roles) {
        interfaceService.updateStatus(id, request, roles);
        return Result.ok();
    }

    @Operation(summary = "删除接口", description = "仅管理员；同步清理应用开通关系")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id,
                               @RequestHeader(value = ApiHeaders.USER_ROLES, required = false) String roles) {
        interfaceService.delete(id, roles);
        return Result.ok();
    }
}
