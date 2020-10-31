package com.tongji.boying.controller;

import cn.hutool.core.collection.CollUtil;
import com.tongji.boying.common.api.CommonPage;
import com.tongji.boying.common.api.CommonResult;
import com.tongji.boying.dto.AdminInfoParam;
import com.tongji.boying.dto.AdminLoginParam;
import com.tongji.boying.dto.AdminParam;
import com.tongji.boying.dto.AdminPasswordParam;
import com.tongji.boying.model.Admin;
import com.tongji.boying.model.Role;
import com.tongji.boying.service.UmsAdminService;
import com.tongji.boying.service.UmsRoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 后台管理员管理
 */
@Controller
@Api(tags = "UmsAdminController", description = "后台管理员管理")
@RequestMapping("/admin")
public class UmsAdminController
{
    @Value("${jwt.tokenHeader}")
    private String tokenHeader;
    @Value("${jwt.tokenHead}")
    private String tokenHead;
    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsRoleService roleService;

    @ApiOperation(value = "管理员注册")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Admin> register(@Validated @RequestBody AdminParam adminParam)
    {
        Admin admin = adminService.register(adminParam);
        if (admin == null)
        {
            return CommonResult.failed();
        }
        return CommonResult.success(admin);
    }

    @ApiOperation(value = "登录以后返回token")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult login(@Validated @RequestBody AdminLoginParam adminLoginParam)
    {
        String token = adminService.login(adminLoginParam.getUsername(), adminLoginParam.getPassword());
        if (token == null)
        {
            return CommonResult.validateFailed("管理员名或密码错误");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation(value = "刷新token")
    @RequestMapping(value = "/refreshToken", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult refreshToken(HttpServletRequest request)
    {
        String token = request.getHeader(tokenHeader);
        String refreshToken = adminService.refreshToken(token);
        if (refreshToken == null)
        {
            return CommonResult.failed("token已经过期！");
        }
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", refreshToken);
        tokenMap.put("tokenHead", tokenHead);
        return CommonResult.success(tokenMap);
    }

    @ApiOperation(value = "获取当前登录管理员信息")
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult getAdminInfo(Principal principal)
    {
        if (principal == null)
        {
            return CommonResult.unauthorized(null);
        }
        String username = principal.getName();
        Admin admin = adminService.getAdminByUsername(username);
        Map<String, Object> data = new HashMap<>();
        data.put("username", admin.getUsername());
        data.put("menus", roleService.getMenuList(admin.getAdminId()));
        data.put("icon", admin.getIcon());
        List<Role> roleList = adminService.getRoleList(admin.getAdminId());
        if (CollUtil.isNotEmpty(roleList))
        {
            List<String> roles = roleList.stream().map(Role::getName).collect(Collectors.toList());
            data.put("roles", roles);
        }
        return CommonResult.success(data);
    }

    @ApiOperation("根据管理员名分页获取管理员列表")
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<CommonPage<Admin>> list(@RequestParam(value = "keyword", required = false) String keyword,
                                                @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum)
    {
        List<Admin> adminList = adminService.list(keyword, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(adminList));
    }

    @ApiOperation("获取指定管理员信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<Admin> getItem(@PathVariable Integer id)
    {
        Admin admin = adminService.getItem(id);
        return CommonResult.success(admin);
    }

    @ApiOperation("修改指定管理员信息")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult update(@PathVariable Integer id, @Validated @RequestBody AdminInfoParam param)
    {
        int count = adminService.update(id, param);
        if (count > 0)
        {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改指定管理员密码")
    @RequestMapping(value = "/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updatePassword(@Validated @RequestBody AdminPasswordParam passwordParam)
    {
        int status = adminService.updatePassword(passwordParam);
        if (status > 0)
        {
            return CommonResult.success(status);
        }
        else if (status == -1)
        {
            return CommonResult.failed("提交参数不合法");
        }
        else if (status == -2)
        {
            return CommonResult.failed("找不到该管理员");
        }
        else if (status == -3)
        {
            return CommonResult.failed("旧密码错误");
        }
        else
        {
            return CommonResult.failed();
        }
    }

    @ApiOperation("删除指定管理员信息")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@PathVariable Integer id)
    {
        int count = adminService.delete(id);
        if (count > 0)
        {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("修改帐号状态")
    @RequestMapping(value = "/updateStatus/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@PathVariable Integer id, @RequestParam(value = "status") Boolean status)
    {

        AdminInfoParam param = new AdminInfoParam();
        param.setStatus(status);
        int count = adminService.update(id, param);
        if (count > 0)
        {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("给管理员分配角色")
    @RequestMapping(value = "/role/update", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateRole(@RequestParam("adminId") Integer adminId,
                                   @RequestParam("roleIds") List<Integer> roleIds)
    {
        int count = adminService.updateRole(adminId, roleIds);
        if (count >= 0)
        {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取指定管理员的角色")
    @RequestMapping(value = "/role/{adminId}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<List<Role>> getRoleList(@PathVariable Integer adminId)
    {
        List<Role> roleList = adminService.getRoleList(adminId);
        return CommonResult.success(roleList);
    }
}
