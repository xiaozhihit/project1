package cn.hit.edu.taskjudgebackend.controller;

import cn.hit.edu.taskjudgebackend.entity.RestBean;
import cn.hit.edu.taskjudgebackend.entity.Score;
import cn.hit.edu.taskjudgebackend.entity.Task;
import cn.hit.edu.taskjudgebackend.entity.User;
import cn.hit.edu.taskjudgebackend.service.ScoreService;
import cn.hit.edu.taskjudgebackend.service.TaskService;
import cn.hit.edu.taskjudgebackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@Slf4j
@RestController
public class TaskController {
    @Resource
    private TaskService taskService;

    @Resource
    private ScoreService scoreService;

    @Resource
    private UserService userService;


    @PostMapping("/api/user/submit-task")
    public RestBean<?> submitTask(@RequestParam("file") MultipartFile zipFile, @RequestParam("remark") String remark) throws Exception {
        if (zipFile.isEmpty()) {
            return RestBean.failure(400, "文件不能为空!");
        }
        //todo 在此可以更改处理文件的逻辑
        // 默认文件的命名规则为 组长学号-组长姓名(不是用户名)-小组编号
        // 解析文件名，获取组号
        String originalFilename = zipFile.getOriginalFilename();
        if (originalFilename == null) return RestBean.failure(400, "文件不能为空");
        String[] splitName = originalFilename.split("-");
        if (splitName.length < 3) {
            return RestBean.failure(400, "文件名格式不正确!");
        }
        int groupId = 0;//通过当前登录用户信息 获取 正确小组编号
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            User user = userService.getByUsername(username);
            if (user != null) {
                groupId = user.getGroupId();
            }
        }
        if (groupId == 0) return RestBean.failure(400, "用户未登录");

        Path dirPath = Paths.get("src/main/java/taskfiles/group_" + groupId);

        // 如果目录不存在则创建
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // 存放文件
        Path zipPath = dirPath.resolve(originalFilename);
        try (InputStream in = zipFile.getInputStream()) {
            Files.copy(in, zipPath, StandardCopyOption.REPLACE_EXISTING);
        }

        if (taskService.saveTask(groupId, dirPath.toString(), null, null/*map.get("video")*/, null/*map.get("doc")*/, remark)) {
            return RestBean.success("大作业提交成功");
        } else {
            return RestBean.failure(500, "未知错误!");
        }
    }

    @GetMapping("/api/tasks")
    public RestBean<?> getUsersTasks() {
        List<Task> tasks = taskService.getAllTasks();
        return RestBean.success(tasks);
    }

    @PutMapping("/api/tasks")
    public RestBean<?> setTasksUrl(@RequestBody List<Task> tasks) {
        boolean success = true;
        for (Task task : tasks) {
            success &= taskService.saveTask(task);
        }
        return success ? RestBean.success("设置成功！") : RestBean.failure(500, "服务器内部错误");
    }

    @GetMapping("/api/tasks-scores")
    public RestBean<?> getUserTasksWithScores() {
        List<Task> tasks = taskService.getAllTasks();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
        String username = authentication.getName();
        if (roles.contains("ROLE_user")) {//当前是学生兼评委 对大作业评分 不应该获取查看到自己所在小组的大作业
            User user = userService.getByUsername(username);
            tasks.removeIf(task -> task.getGroupId().equals(user.getGroupId()));//去掉自己所在小组作业
        }
        List<Score> scores = scoreService.getByJudgeUsername(username);
        //使用map表示一个带着score的task对象
        //由于时间关系，没有对数据库做太多的判断，默认数据库中已经创建好数据记录，只是没有打分或者提交
        List<Map<String, Object>> taskList = new ArrayList<>();
        for (Task task : tasks) {
            Integer groupId = task.getGroupId();
            Score score = null;
            for (Score s : scores) {
                if (s.getGroupId().equals(groupId)) {
                    score = s;
                }
            }
            //这里默认一定会有groupId能匹配的分数数据记录
            if (score == null) {
                log.error("管理员请检查数据库表信息是否完善正确!!!");
                return RestBean.failure(500);
            }
            HashMap<String, Object> obj = new HashMap<>();
            obj.put("groupId", groupId);
            obj.put("storageUrl", task.getStorageUrl());
            obj.put("demoUrl", task.getDemoUrl());
            obj.put("videoUrl", task.getVideoUrl());
            obj.put("workloadScore", score.getWorkloadScore());
            obj.put("completionScore", score.getCompletionScore());
            obj.put("designScore", score.getDesignScore());
            obj.put("divisionScore", score.getDivisionScore());
            obj.put("remark", task.getRemark());
            obj.put("isSubmitted", score.getIsSubmitted());
            taskList.add(obj);
        }

        return RestBean.success(taskList);
    }

    @PutMapping("/api/tasks-scores")
    public RestBean<?> saveJudgingScore(@RequestBody List<Map<String, Object>> taskList) {
        if(taskList==null||taskList.size()==0)
            return RestBean.failure(400);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String msg = taskList.get(0).get("isSubmitted").equals(true)?"评分提交成功!":"评分保存成功!";
        for (Map<String, Object> taskWithScore : taskList) {
            Integer groupId = (int) taskWithScore.get("groupId");
            Double workloadScore = Double.parseDouble(String.valueOf(taskWithScore.get("workloadScore")));
            Double completionScore = Double.parseDouble(String.valueOf( taskWithScore.get("completionScore")));
            Double designScore = Double.parseDouble(String.valueOf( taskWithScore.get("designScore")));
            Double divisionScore = Double.parseDouble(String.valueOf( taskWithScore.get("divisionScore")));
            boolean isSubmitted = (boolean) taskWithScore.get("isSubmitted");
            scoreService.save(new Score(null,groupId,username,workloadScore,completionScore,designScore,divisionScore,isSubmitted));
        }
        return RestBean.success(msg);
    }

}
