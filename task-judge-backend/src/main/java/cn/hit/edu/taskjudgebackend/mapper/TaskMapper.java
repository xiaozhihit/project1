package cn.hit.edu.taskjudgebackend.mapper;
import cn.hit.edu.taskjudgebackend.entity.Task;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

public interface TaskMapper extends BaseMapper<Task> {
    @Select("select * from task where group_id = #{groupId}")
    Task getTaskByGroupId(Integer groupId);
}
