package cn.hit.edu.taskjudgebackend.service.impl;

import cn.hit.edu.taskjudgebackend.entity.Score;
import cn.hit.edu.taskjudgebackend.mapper.ScoreMapper;
import cn.hit.edu.taskjudgebackend.service.ScoreService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Slf4j
@Service
public class ScoreServiceImpl implements ScoreService {

    @Resource
    private ScoreMapper scoreMapper;
    @Override
    public List<Score> getAllScores() {
        return scoreMapper.selectList(null);
    }

    @Override
    public void resetSubmission(List<Integer> ids) {
        for (Integer id :ids) {
            scoreMapper.updateSubmission(id);
        }
    }

    @Override
    public List<Score> getByJudgeUsername(String username) {
        return scoreMapper.getByJudgeUsername(username);
    }

    @Override
    public void save(Score score) {
        UpdateWrapper<Score> wrapper =new UpdateWrapper<>();
        wrapper.eq("judge_username",score.getJudgeUsername());
        wrapper.eq("group_id",score.getGroupId());
        try {
            int affectedRows = scoreMapper.update(score, wrapper);
            if (affectedRows <= 0) {
                throw new RuntimeException("Failed to update score");
            }
        } catch (Exception e) {
            log.error("请管理员检查数据库表中数据");
            e.printStackTrace();
        }
    }
}
