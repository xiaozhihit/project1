package cn.hit.edu.taskjudgebackend.service.impl;

import cn.hit.edu.taskjudgebackend.entity.Score;
import cn.hit.edu.taskjudgebackend.entity.User;
import cn.hit.edu.taskjudgebackend.mapper.ScoreMapper;
import cn.hit.edu.taskjudgebackend.mapper.UserMapper;
import cn.hit.edu.taskjudgebackend.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private ScoreMapper scoreMapper;

    @Override
    public User getByUsername(String username) {
        return userMapper.getByUsername(username);
    }

    @Override
    public List<Score> getScores(Integer userId) {
        User user = userMapper.selectById(userId);
        Integer groupId = user.getGroupId();
        QueryWrapper<Score> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id",groupId);
        return scoreMapper.selectList(queryWrapper);
    }

    @Override
    public List<User> getByIdentity(Integer identity) {//0学生 1评委
        return userMapper.getByIdentity(identity > 0);
    }

    @Override
    public void saveUser(User user) {
        userMapper.save(user);
    }

    @Override
    public void resetPassword(List<Integer> ids) {
        for(Integer id :ids){
            User user = userMapper.selectById(id);
            if(user!=null){
                //重置密码为用户名/学号
                userMapper.updatePassword(user.getUsername(), user.getUsername());
            }
        }
    }

    @Override
    public void updateGroupId(List<Integer> ids, int groupId) {
        for(Integer id:ids ){
            userMapper.updateGroupId(id,groupId);
        }
    }

    @Override
    public void setJudge(List<Integer> ids) {
        for(Integer id:ids){
            userMapper.setJudge(id);
        }
    }

}
