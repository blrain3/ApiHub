package com.apihub.admin.service.impl;

import com.apihub.admin.enums.HttpMethodEnum;
import com.apihub.admin.enums.StatusEnum;
import com.apihub.admin.dto.InterfaceCreateRequest;
import com.apihub.admin.dto.InterfaceStatusRequest;
import com.apihub.admin.dto.InterfaceUpdateRequest;
import com.apihub.admin.dto.InterfaceVO;
import com.apihub.admin.entity.AppInterfaceEntity;
import com.apihub.admin.entity.InterfaceEntity;
import com.apihub.admin.mapper.AppInterfaceMapper;
import com.apihub.admin.mapper.InterfaceMapper;
import com.apihub.admin.service.InterfaceService;
import com.apihub.common.exception.BizException;
import com.apihub.common.result.ErrorCode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 接口资产 CRUD 实现：管理员可增删改与上下线；普通用户仅可浏览已上线接口。
 */
@Service
public class InterfaceServiceImpl implements InterfaceService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final int LIST_LIMIT = 500;

    private final InterfaceMapper interfaceMapper;
    private final AppInterfaceMapper appInterfaceMapper;

    public InterfaceServiceImpl(InterfaceMapper interfaceMapper, AppInterfaceMapper appInterfaceMapper) {
        this.interfaceMapper = interfaceMapper;
        this.appInterfaceMapper = appInterfaceMapper;
    }

    @Override
    public List<InterfaceVO> list(String keyword, Integer status, String rolesHeader) {
        LambdaQueryWrapper<InterfaceEntity> wrapper = new LambdaQueryWrapper<InterfaceEntity>()
                .orderByDesc(InterfaceEntity::getId)
                .last("LIMIT " + LIST_LIMIT);

        // 普通用户市场页：只展示已上线接口
        if (!isAdmin(rolesHeader)) {
            wrapper.eq(InterfaceEntity::getStatus, StatusEnum.ENABLE);
        } else if (status != null) {
            StatusEnum statusEnum = StatusEnum.fromCode(status);
            if (statusEnum == null) {
                throw new BizException(ErrorCode.BAD_REQUEST.getCode(), "status 只能为 0 或 1");
            }
            wrapper.eq(InterfaceEntity::getStatus, statusEnum);
        }

        if (StringUtils.hasText(keyword)) {
            String like = "%" + keyword.trim() + "%";
            wrapper.and(w -> w.like(InterfaceEntity::getName, like).or().like(InterfaceEntity::getPath, like));
        }

        return interfaceMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    @Override
    public InterfaceVO detail(Long id, String rolesHeader) {
        InterfaceEntity entity = requireInterface(id);
        if (!isAdmin(rolesHeader) && entity.getStatus() != StatusEnum.ENABLE) {
            throw new BizException(ErrorCode.NOT_FOUND);
        }
        return toVO(entity);
    }

    @Override
    public InterfaceVO create(InterfaceCreateRequest request, String rolesHeader) {
        requireAdmin(rolesHeader);
        HttpMethodEnum method = requireMethod(request.getMethod());
        String path = normalizePath(request.getPath());
        ensurePathMethodUnique(path, method, null);

        InterfaceEntity entity = new InterfaceEntity();
        entity.setName(request.getName().trim());
        entity.setPath(path);
        entity.setMethod(method);
        entity.setDescription(trimToNull(request.getDescription()));
        entity.setVersion(StringUtils.hasText(request.getVersion()) ? request.getVersion().trim() : "v1");
        entity.setCategory(trimToNull(request.getCategory()));
        // 新建默认下线，需管理员显式上线后才可被开通/调用
        entity.setStatus(StatusEnum.DISABLE);
        interfaceMapper.insert(entity);
        return toVO(entity);
    }

    @Override
    public InterfaceVO update(Long id, InterfaceUpdateRequest request, String rolesHeader) {
        requireAdmin(rolesHeader);
        InterfaceEntity entity = requireInterface(id);
        HttpMethodEnum method = requireMethod(request.getMethod());
        String path = normalizePath(request.getPath());
        ensurePathMethodUnique(path, method, id);

        entity.setName(request.getName().trim());
        entity.setPath(path);
        entity.setMethod(method);
        entity.setDescription(trimToNull(request.getDescription()));
        entity.setVersion(StringUtils.hasText(request.getVersion()) ? request.getVersion().trim() : "v1");
        entity.setCategory(trimToNull(request.getCategory()));
        interfaceMapper.updateById(entity);
        return toVO(entity);
    }

    @Override
    public void updateStatus(Long id, InterfaceStatusRequest request, String rolesHeader) {
        requireAdmin(rolesHeader);
        InterfaceEntity entity = requireInterface(id);
        StatusEnum statusEnum = StatusEnum.fromCode(request.getStatus());
        if (statusEnum == null) {
            throw new BizException(ErrorCode.BAD_REQUEST.getCode(), "status 只能为 0（下线）或 1（上线）");
        }
        entity.setStatus(statusEnum);
        interfaceMapper.updateById(entity);
    }

    @Override
    @Transactional
    public void delete(Long id, String rolesHeader) {
        requireAdmin(rolesHeader);
        requireInterface(id);
        // 同步清理应用开通关系，避免残留授权
        appInterfaceMapper.delete(new LambdaQueryWrapper<AppInterfaceEntity>()
                .eq(AppInterfaceEntity::getInterfaceId, id));
        interfaceMapper.deleteById(id);
    }

    // ---------- 私有辅助 ----------

    private InterfaceEntity requireInterface(Long id) {
        InterfaceEntity entity = interfaceMapper.selectById(id);
        if (entity == null) {
            throw new BizException(ErrorCode.NOT_FOUND.getCode(), "接口不存在");
        }
        return entity;
    }

    private void ensurePathMethodUnique(String path, HttpMethodEnum method, Long excludeId) {
        LambdaQueryWrapper<InterfaceEntity> wrapper = new LambdaQueryWrapper<InterfaceEntity>()
                .eq(InterfaceEntity::getPath, path)
                .eq(InterfaceEntity::getMethod, method);
        if (excludeId != null) {
            wrapper.ne(InterfaceEntity::getId, excludeId);
        }
        Long count = interfaceMapper.selectCount(wrapper);
        if (count != null && count > 0) {
            throw new BizException(ErrorCode.BAD_REQUEST.getCode(),
                    "路径与方法组合已存在：" + method.getCode() + " " + path);
        }
    }

    private HttpMethodEnum requireMethod(String method) {
        HttpMethodEnum parsed = HttpMethodEnum.fromCode(method);
        if (parsed == null) {
            throw new BizException(ErrorCode.BAD_REQUEST.getCode(), "不支持的请求方法：" + method);
        }
        return parsed;
    }

    private String normalizePath(String path) {
        String trimmed = path.trim();
        if (!trimmed.startsWith("/")) {
            trimmed = "/" + trimmed;
        }
        return trimmed;
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

    private void requireAdmin(String rolesHeader) {
        if (!isAdmin(rolesHeader)) {
            throw new BizException(ErrorCode.FORBIDDEN);
        }
    }

    private boolean isAdmin(String rolesHeader) {
        if (!StringUtils.hasText(rolesHeader)) {
            return false;
        }
        for (String role : rolesHeader.split(",")) {
            if (ROLE_ADMIN.equals(role.trim())) {
                return true;
            }
        }
        return false;
    }

    private InterfaceVO toVO(InterfaceEntity entity) {
        InterfaceVO vo = new InterfaceVO();
        vo.setId(entity.getId());
        vo.setName(entity.getName());
        vo.setPath(entity.getPath());
        vo.setMethod(entity.getMethod() == null ? null : entity.getMethod().getCode());
        vo.setDescription(entity.getDescription());
        vo.setVersion(entity.getVersion());
        vo.setCategory(entity.getCategory());
        vo.setStatus(entity.getStatus() == null ? null : entity.getStatus().getCode());
        vo.setCreateTime(entity.getCreateTime());
        return vo;
    }
}
