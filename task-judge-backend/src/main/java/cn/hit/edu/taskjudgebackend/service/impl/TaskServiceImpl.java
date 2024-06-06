package cn.hit.edu.taskjudgebackend.service.impl;

import cn.hit.edu.taskjudgebackend.entity.Task;
import cn.hit.edu.taskjudgebackend.mapper.TaskMapper;
import cn.hit.edu.taskjudgebackend.service.TaskService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class TaskServiceImpl implements TaskService {
    @Resource
    private TaskMapper taskMapper;

    @Override
    public boolean saveTask(Task task) {
        return saveTask(
                task.getGroupId(),task.getStorageUrl()
                ,task.getDemoUrl(),task.getVideoUrl()
                ,task.getDocUrl(),task.getRemark());
    }

    @Override
    public boolean saveTask(int groupId, String storageUrl, String demoUrl,
                            String videoUrl, String docUrl, String remark) {
        Task task = taskMapper.getTaskByGroupId(groupId);
        if(task!=null){
            UpdateWrapper<Task> updateWrapper=new UpdateWrapper<>();
            updateWrapper.eq("group_id",groupId);
            task=new Task(groupId,storageUrl,demoUrl,videoUrl,docUrl,remark,true);
            return taskMapper.update(task,updateWrapper)>0;
        }else{
            task =new Task(groupId,storageUrl,demoUrl,videoUrl,docUrl,remark,true);
            return taskMapper.insert(task)>0;
        }
    }

    @Override
    public List<Task> getAllTasks() {
        return taskMapper.selectList(null);
    }
}
