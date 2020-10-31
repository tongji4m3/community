package com.tongji.boying.service;

import com.tongji.boying.dto.RoleParam;
import com.tongji.boying.model.Menu;
import com.tongji.boying.model.Resource;
import com.tongji.boying.model.Role;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 后台角色管理Service
 */
public interface UmsRoleService {
    /**
     * 添加角色
     * @param param
     */
    int create(RoleParam param);

    /**
     * 修改角色信息
     */
    int update(Integer id, RoleParam param);

    /**
     * 批量删除角色
     */
    int delete(List<Integer> ids);

    /**
     * 获取所有角色列表
     */
    List<Role> list();

    /**
     * 分页获取角色列表
     */
    List<Role> list(String keyword, Integer pageSize, Integer pageNum);

    /**
     * 根据管理员ID获取对应菜单
     */
    List<Menu> getMenuList(Integer adminId);

    /**
     * 获取角色相关菜单
     */
    List<Menu> listMenu(Integer roleId);

    /**
     * 获取角色相关资源
     */
    List<Resource> listResource(Integer roleId);

    /**
     * 给角色分配菜单
     */
    @Transactional
    int allocMenu(Integer roleId, List<Integer> menuIds);

    /**
     * 给角色分配资源
     */
    @Transactional
    int allocResource(Integer roleId, List<Integer> resourceIds);

    /**
     * 判断一个角色Id对应的角色是否存在
     * @param roleId
     * @return
     */
    boolean hasRole(Integer roleId);
}
