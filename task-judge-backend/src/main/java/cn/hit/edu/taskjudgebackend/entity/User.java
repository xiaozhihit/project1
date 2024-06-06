package cn.hit.edu.taskjudgebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username;//用户名 学生为学号
    private String name;//姓名
    private String password;
    private Boolean identity;//f-学生，t-评委
    private Boolean isJudge;//f-非评委，t-评委
    private Integer groupId;//身份仅为评委才为0 学生不为0

    public User(String username, String name, String password, Boolean identity, Boolean isJudge, Integer groupId) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.identity = identity;
        this.isJudge = isJudge;
        this.groupId = groupId;
    }
}
