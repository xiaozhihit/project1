package cn.hit.edu.taskjudgebackend.controller;

import cn.hit.edu.taskjudgebackend.entity.RestBean;
import cn.hit.edu.taskjudgebackend.entity.Score;
import cn.hit.edu.taskjudgebackend.entity.User;
import cn.hit.edu.taskjudgebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    @Resource
    private UserService userService;

    //查询大作业成绩
    @GetMapping("/api/user/scores")
    public RestBean<?> getTaskScores(String username) {
        // 根据用户名查询用户信息
        User user = userService.getByUsername(username);
        if(null==user){
            throw new UsernameNotFoundException(username);
        }
        // 获取学生的成绩数据
        List<Score> scores = userService.getScores(user.getId());
//        log.info(scores.toString());
        return RestBean.success(scores);
    }

    @GetMapping("/api/user")
    public RestBean<?> getUserByIdentity(@RequestParam Integer identity,//0学生  1评委
                                         @RequestParam(required = false ,value = "searchKeyword") String keyWord){
        if(identity!=0&&identity!=1)
            return RestBean.failure(400,"非法请求参数");
        List<User> users = userService.getByIdentity(identity);
        users.removeIf(u-> !u.getName().contains(keyWord)&&!u.getUsername().contains(keyWord));
        return RestBean.success(users);
    }

    @PutMapping("/api/user")
    public RestBean<?> resetPassword(@RequestBody List<Integer> ids){
        if(ids==null)return RestBean.failure(400);
        userService.resetPassword(ids);
        return RestBean.success("密码重置成功！");
    }

    @PutMapping("/api/user/groupId")
    public RestBean<?> assignGroupId(@RequestBody Map<String,Object> requestBody){
        List<Integer> ids = (List<Integer>)requestBody.get("ids");
        Integer groupId=(Integer)requestBody.get("groupId");
        if(ids==null||groupId==null)
            return RestBean.failure(400);
        userService.updateGroupId(ids,groupId);
        return RestBean.success("分配成功");
    }

    @PostMapping("/api/judge" )
    public RestBean<?> createNewJudge(@RequestBody Map<String,String> judge){
        String name = judge.get("name");
        String username = judge.get("username");
        String password = judge.get("password");
        User user = userService.getByUsername(username);
        if(user!=null)return RestBean.failure(400,"用户名已存在");
        userService.saveUser(new User(username,name,password,true,true,0));
        return RestBean.success(name+" 评委创建成功");
    }

    @PutMapping("/api/judge")
    public RestBean<?> setJudge(@RequestBody List<Integer> ids){
        if(ids==null)return RestBean.failure(400);
        userService.setJudge(ids);
        return RestBean.success("设置成功");
    }

    @PostMapping("/api/user") //直接录入学生信息
    public RestBean<?> register(@RequestBody Map<String,String> form){
        if(form==null|| form.isEmpty())return RestBean.failure(400);
        String name=form.get("name");
        String username=form.get("username");
        String password=form.get("password");
        String groupIdstr=form.get("groupId");
        Integer groupId =groupIdstr.equals("")?null: Integer.parseInt(groupIdstr);
        User user = userService.getByUsername(username);
        if(user!=null)return RestBean.failure(400,"用户名已存在");
        userService.saveUser(new User(username,name,password,false,false,groupId));
        return RestBean.success("学生 "+name+" 添加成功!");
    }

    //TODO 使用excel表格上传用户信息注册新学生
    @PostMapping("/api/users")//使用excel表格注册
    public RestBean<?> register(@RequestBody MultipartFile file){
        return null;
    }

}
