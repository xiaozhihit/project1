package cn.hit.edu.taskjudgebackend.mapper;

import cn.hit.edu.taskjudgebackend.entity.Admin;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

public interface AdminMapper extends BaseMapper<Admin> {
    @Select("select * from admin where username =#{username}")
    Admin getByUsername(String username);
}
