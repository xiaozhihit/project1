package cn.hit.edu.taskjudgebackend.service;

import cn.hit.edu.taskjudgebackend.entity.Score;
import cn.hit.edu.taskjudgebackend.entity.User;

import java.util.List;

public interface UserService {
    User getByUsername(String username);
    List<Score> getScores(Integer userId);

    List<User> getByIdentity(Integer identity);

    void saveUser(User user);

    void resetPassword(List<Integer> id);

    void updateGroupId(List<Integer> ids, int groupId);

    void setJudge(List<Integer> ids);
}
