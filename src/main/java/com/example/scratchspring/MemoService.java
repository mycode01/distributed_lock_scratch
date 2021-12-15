package com.example.scratchspring;

import com.example.scratchspring.annotation.NeedLock;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Random;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemoService {

  private final MemoRepository repo;
  private final ObjectMapper objectMapper;

  public MemoService(MemoRepository repo, ObjectMapper objectMapper) {
    this.repo = repo;
    this.objectMapper = objectMapper;
  }

  @Transactional
  public long createMemo(String author, String content) {
    final Memo newMemo = new Memo(author, content);
    repo.save(newMemo);

    somethingHappened();

    newMemo.addContent(" \n by " + author);
    repo.save(newMemo);
    return newMemo.getId();
  }

  @Transactional
  public long tryingCreateMemo(String author, String content) {
    final Memo newMemo = new Memo(author, content);
    repo.save(newMemo);

    itSeemsLikeTakesForever(5000);
    // 락 반환은 lockingservice에 지정한대로 2초후에 반환되기 때문에
    // 정상적으로 저장처리되며, 다만 락을 반환을 할 시점에 이미 반환된 락에 대한 exception이 발생함

    newMemo.addContent(" \n by " + author);
    repo.save(newMemo);
    return newMemo.getId();
  }

  private final static String LOCK_KEY_TEST3 = "test3";

  @NeedLock(lockKey = LOCK_KEY_TEST3, waitSec = 1, leaseSec = 2)
  @Transactional
  public long createMemo2(String author, String content) {
    final Memo newMemo = new Memo(author, content);
    repo.save(newMemo);

    somethingHappened();

    newMemo.addContent(" \n by " + author);
    repo.save(newMemo);
    return newMemo.getId();
  }


  private void somethingHappened() {
    if (((new Random()).nextInt() % 2) == 1) {
      throw new RuntimeException("throw exception");
    }
  }

  private void itSeemsLikeTakesForever(long millis) {
    try {
      Thread.sleep(millis);
    } catch (Exception e) {
    }
  }

  @Transactional(readOnly = true)
  public Memo getMemo(Long id) {
    return repo.findById(id).orElseThrow();
  }

}
