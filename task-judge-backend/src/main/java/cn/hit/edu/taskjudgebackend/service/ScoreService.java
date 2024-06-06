package cn.hit.edu.taskjudgebackend.service;

import cn.hit.edu.taskjudgebackend.entity.Score;

import java.util.List;

public interface ScoreService {
    List<Score> getAllScores();

    void resetSubmission(List<Integer> ids);

    List<Score> getByJudgeUsername(String username);

    void save(Score score);
}
