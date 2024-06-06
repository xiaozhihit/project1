package cn.hit.edu.taskjudgebackend.mapper;

import cn.hit.edu.taskjudgebackend.entity.Score;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface ScoreMapper extends BaseMapper<Score> {
    @Select("select * from score where group_id =#{groupId}")
    List<Score> getByGroupId(Integer groupId);

    @Update("update score set is_submitted =false where id = #{id}")
    void updateSubmission(Integer id);

    @Select("select * from score where judge_username = #{username}")
    List<Score> getByJudgeUsername(String username);
}
