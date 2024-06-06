package cn.hit.edu.taskjudgebackend.service;

import cn.hit.edu.taskjudgebackend.entity.Task;

import java.util.List;

public interface TaskService {

    boolean saveTask(Task task);
    boolean saveTask(int groupId,String storageUrl,String demoUrl,String videoUrl,String docUrl,String remark);

    List<Task> getAllTasks();
}
