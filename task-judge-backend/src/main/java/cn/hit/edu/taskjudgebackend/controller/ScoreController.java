package cn.hit.edu.taskjudgebackend.controller;

import cn.hit.edu.taskjudgebackend.entity.RestBean;
import cn.hit.edu.taskjudgebackend.service.ScoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
public class ScoreController {
    @Resource
    private ScoreService scoreService;

    @GetMapping("/api/score")
    public RestBean<?> getScores(){
        return RestBean.success(scoreService.getAllScores());
    }

    @PutMapping("/api/score")
    public RestBean<?> resetSubmission(@RequestBody List<Integer> ids){
        if(ids==null||ids.size()==0)return RestBean.failure(400);
        scoreService.resetSubmission(ids);
        return RestBean.success("重置成功");
    }
}
