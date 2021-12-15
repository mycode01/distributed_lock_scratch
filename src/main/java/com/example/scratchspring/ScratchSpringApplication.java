package com.example.scratchspring;

import com.example.scratchspring.advisor.LockingService;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class ScratchSpringApplication {

  public static void main(String[] args) {
    SpringApplication.run(ScratchSpringApplication.class, args);
  }


  @RestController
  public static class DefaultController {

    private final MemoService service;
    private final LockingService withLock;
    private final static String LOCK_KEY_TEST = "test1";

    public DefaultController(MemoService service, LockingService withLock) {
      this.service = service;
      this.withLock = withLock;
    }

    @PostMapping("test1")
    public Long test1(@RequestBody Map<String, String> reqMap) {

      return withLock.doWork(createMemo(reqMap.getOrDefault("author", "anonymous"),
          reqMap.getOrDefault("content", "empty")), LOCK_KEY_TEST);
    } // case1, lock이 걸려있을때 @Transactional annotation이 동작하는지?

    public Supplier<Long> createMemo(String author, String content){
      return ()->service.createMemo(author, content);
    }

    @GetMapping("get")
    public Memo getMemo(@RequestParam Long id){
      return service.getMemo(id);
    }
    // 확인용 api

    @PostMapping("test2")
    public Long test2(@RequestBody Map<String, String> reqMap) {

      return withLock.doWork(tryingCreateMemo(reqMap.getOrDefault("author", "anonymous"),
          reqMap.getOrDefault("content", "empty")), LOCK_KEY_TEST);
    }
    // case2, 트랜잭션 진행중 키 릴리즈 타임보다 오래걸릴시에는 어떻게 처리되는지?

    public Supplier<Long> tryingCreateMemo(String author, String content){
      return ()->service.tryingCreateMemo(author, content);
    }

    @PostMapping("test3")
    public Long test3(@RequestBody Map<String, String> reqMap) {

      return service.createMemo2(reqMap.getOrDefault("author", "anonymous"),
          reqMap.getOrDefault("content", "empty"));
    } // case3, case1을 annotation으로 변경함
  }
}
