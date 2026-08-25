package com.apihub.admin.service;

import com.apihub.admin.dto.InterfaceCreateRequest;
import com.apihub.admin.dto.InterfaceStatusRequest;
import com.apihub.admin.dto.InterfaceUpdateRequest;
import com.apihub.admin.dto.InterfaceVO;

import java.util.List;

/**
 * 接口资产管理：增删改查与上下线。
 */
public interface InterfaceService {

    /** 列表；普通用户仅返回已上线接口，管理员可按状态筛选全部 */
    List<InterfaceVO> list(String keyword, Integer status, String rolesHeader);

    InterfaceVO detail(Long id, String rolesHeader);

    InterfaceVO create(InterfaceCreateRequest request, String rolesHeader);

    InterfaceVO update(Long id, InterfaceUpdateRequest request, String rolesHeader);

    void updateStatus(Long id, InterfaceStatusRequest request, String rolesHeader);

    void delete(Long id, String rolesHeader);
}
