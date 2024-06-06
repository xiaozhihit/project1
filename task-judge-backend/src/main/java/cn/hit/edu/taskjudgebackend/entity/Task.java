package cn.hit.edu.taskjudgebackend.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer groupId;
    private String storageUrl;
    private String demoUrl;
    private String videoUrl;
    private String docUrl;
    private String remark;
    private Boolean isSubmitted;

    public Task(Integer groupId, String storageUrl, String demoUrl, String videoUrl, String docUrl, String remark,Boolean isSubmitted) {
        this.groupId = groupId;
        this.storageUrl = storageUrl;
        this.demoUrl = demoUrl;
        this.videoUrl = videoUrl;
        this.docUrl = docUrl;
        this.remark = remark;
        this.isSubmitted = isSubmitted;
    }


    public Task(Integer groupId, String storageUrl, String demoUrl, String videoUrl) {
        this.groupId = groupId;
        this.storageUrl = storageUrl;
        this.demoUrl = demoUrl;
        this.videoUrl = videoUrl;
    }
}
