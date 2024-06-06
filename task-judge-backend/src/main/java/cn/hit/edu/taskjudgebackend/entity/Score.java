package cn.hit.edu.taskjudgebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Score {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer groupId;
    private String judgeUsername;
    private Double workloadScore;//工作量占比40%，分数均是百分制
    private Double completionScore;//完成度占比40%
    private Double designScore;//界面设计占比10%
    private Double divisionScore;//分工占比10%
    @TableField(exist = false)
    private String total;//总分 默认保留2位小数
    private Boolean isSubmitted;//评分提交状态 0评委可修改 1不可修改

    public Score(Integer id, Integer groupId, String judgeUsername, Double workloadScore, Double completionScore, Double designScore, Double divisionScore) {
        this.id = id;
        this.groupId = groupId;
        this.judgeUsername = judgeUsername;
        this.workloadScore = workloadScore;
        this.completionScore = completionScore;
        this.designScore = designScore;
        this.divisionScore = divisionScore;
    }

    public Score(Integer id, Integer groupId, String judgeUsername, Double workloadScore, Double completionScore, Double designScore, Double divisionScore,boolean isSubmitted) {
        this.id=id;
        this.groupId = groupId;
        this.judgeUsername = judgeUsername;
        this.workloadScore = workloadScore;
        this.completionScore = completionScore;
        this.designScore = designScore;
        this.divisionScore = divisionScore;
        this.isSubmitted =isSubmitted;
    }

    public String getTotal() {
        return String.format("%.2f", this.getWorkloadScore() * 0.4
                + this.getCompletionScore() * 0.4
                + this.getDesignScore() * 0.1
                + this.getDivisionScore() * 0.1);
    }
}
