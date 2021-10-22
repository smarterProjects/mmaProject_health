package com.health.security;

import com.alibaba.dubbo.config.annotation.Reference;
import com.health.pojo.Permission;
import com.health.pojo.Role;
import com.health.pojo.User;
import com.health.service.UserService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 登录用户的认证与授权信息
 */
@Component
public class springSecurityUserService implements UserDetailsService {

    @Reference
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 通过用户名查询用户信息
        User user = userService.findByUsername(username);
        // 用户的权限集合
        List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
        if(null != user) {
            // 能查询到，就要授权，且返回UserDetails
            GrantedAuthority authority = null;
            // 登陆用户所拥有的角色集合
            Set<Role> roles = user.getRoles();
            if(null != roles){
                for (Role role : roles) {
                    // keyword ROLE_ADMIN
                    // 将来支持角色访问控制 hasRole hasAnyRole
                    authority = new SimpleGrantedAuthority(role.getKeyword());
                    authorityList.add(authority);
                    // 角色下的权限集合
                    Set<Permission> permissions = role.getPermissions();
                    if(null != permissions){
                        for (Permission permission : permissions) {
                            // 添加权限 hasAuthority hasAnyAuthority...
                            authority = new SimpleGrantedAuthority(permission.getKeyword());
                            authorityList.add(authority);
                        }
                    }
                }
            }
            // 返回登陆用户信息给security，会保存到session
            return new org.springframework.security.core.userdetails.User(username, user.getPassword(), authorityList);
        }
        // 查询不到就返回null
        return null;
    }
}