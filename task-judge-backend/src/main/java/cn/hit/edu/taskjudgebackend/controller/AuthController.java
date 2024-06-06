package cn.hit.edu.taskjudgebackend.controller;

import cn.hit.edu.taskjudgebackend.entity.Admin;
import cn.hit.edu.taskjudgebackend.entity.RestBean;
import cn.hit.edu.taskjudgebackend.entity.User;
import cn.hit.edu.taskjudgebackend.mapper.AdminMapper;
import cn.hit.edu.taskjudgebackend.mapper.UserMapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
public class AuthController {
    @Resource
    private UserMapper userMapper;
    @Resource
    private AdminMapper adminMapper;

    @GetMapping("/api/auth/check-login")
    public RestBean<?> checkLogin(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        if (authentication != null && authentication.isAuthenticated()) {
            response.put("isLoggedIn", true);
            response.put("username", authentication.getName());
            response.put("roles", AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        } else {
            response.put("isLoggedIn", false);
        }
//        log.info(response.toString());
        return RestBean.success(response);
    }

    @PutMapping("/api/auth/change-password")
    public RestBean<?> changePassword(@RequestBody Map<String,String> passwords){
        String oldPassword = passwords.get("oldPassword");
        String newPassword = passwords.get("newPassword");
        // 获取当前用户的认证信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null&&authentication.isAuthenticated()){
            String username=authentication.getName();
            String password = null;
            Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
            if(roles.contains("ROLE_admin")){//管理员更改密码
                password=adminMapper.getByUsername(username).getPassword();
                // 对比用户的密码和输入的旧密码
                if(!password.equals(oldPassword)){
                    return RestBean.failure(400,"原密码不正确");
                }
                UpdateWrapper<Admin> adminQueryWrapper=new UpdateWrapper<>();
                adminQueryWrapper.eq("username",username).set("password",newPassword);
                adminMapper.update(null,adminQueryWrapper);
            }else{//User（学生或评委）更改密码
                password = userMapper.getByUsername(username).getPassword();
                // 对比用户的密码和输入的旧密码
                if(!password.equals(oldPassword)){
                    return RestBean.failure(400,"原密码不正确");
                }
                UpdateWrapper<User> userQueryWrapper =new UpdateWrapper<>();
                userQueryWrapper.eq("username",username).set("password",newPassword);
                userMapper.update(null,userQueryWrapper);
            }
            return RestBean.success("密码修改成功");
        }
        return RestBean.failure(401,"用户未授权");
    }
}
