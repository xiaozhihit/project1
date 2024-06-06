package cn.hit.edu.taskjudgebackend.service;

import cn.hit.edu.taskjudgebackend.mapper.AdminMapper;
import cn.hit.edu.taskjudgebackend.mapper.UserMapper;
import lombok.val;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private AdminMapper adminMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!StringUtils.hasLength(username))
            throw new UsernameNotFoundException("用户名不能为空");
        val admin = adminMapper.getByUsername(username);
        if(admin!=null)
            return User.withUsername(username).password(admin.getPassword()).roles("admin").build();

        val user = userMapper.getByUsername(username);
        if(user==null)
            throw new UsernameNotFoundException("用户名或密码错误!");
        List<String> roles=new ArrayList<>();
        if(!user.getIdentity())//不是评委而是学生，有学生用户身份
            roles.add("user");
        if(user.getIsJudge())//有评委身份
            roles.add("judge");
        return User.withUsername(username).password(user.getPassword()).roles(roles.toArray(new String[0])).build();
    }
}
