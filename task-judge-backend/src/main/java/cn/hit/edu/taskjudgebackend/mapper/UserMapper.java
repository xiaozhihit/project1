package cn.hit.edu.taskjudgebackend.mapper;

import cn.hit.edu.taskjudgebackend.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    //根据用户名查询用户：getByUsername(String username)
    @Select("select * from user where username = #{username}")
    User getByUsername(String username);

    //根据身份（学生/评委）查询用户列表：getByIdentity(int identity)
    @Select("select * from user where identity = #{identity}")
    List<User> getByIdentity(boolean identity);

    //根据组号查询学生列表：getByGroupId(int groupId)
    @Select("select * from user where group_id =#{groupId}")
    List<User> getByGroupId(int groupId);

    //保存用户
    @Insert("insert into user values(#{id},#{username},#{name},#{password},#{identity},#{isJudge},#{groupId})")
    void save(User user);

    //更新用户密码：updatePassword(String username, String newPassword)
    @Update("update user set password=#{newPassword} where username=#{username}")
    void updatePassword(String username,String newPassword);

    //更新用户分组：updateGroup(String username, int groupId)
    @Update("update user set group_id = #{groupId} where id = #{id}")
    void updateGroupId(int id,int groupId);

    //设置普通学生用户为评委 setJudge(int id)
    @Update("update user set is_judge = true where id=#{id}")
    void setJudge(int id);
}
