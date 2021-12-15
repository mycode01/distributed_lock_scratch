package com.example.scratchspring.advisor;


import com.example.scratchspring.RedissonLockProvider;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

@Service
public class LockingService {

  private final RedissonLockProvider lockProvider;

  public LockingService(RedissonLockProvider lockProvider) {
    this.lockProvider = lockProvider;
  }

  public <T> T doWork(Supplier<T> sp, String key) {
    RLock lock = lockProvider.getLock(key);
    try {
      boolean isUnlocked = lock.tryLock(1, 3, TimeUnit.SECONDS);
      if (!isUnlocked) {
        throw new RuntimeException("get lock fail");
      }
      return sp.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
      return null;
    } finally {
      lock.unlock();
    }
  }

  public void lock(String key, long waitSec, long leaseSec) {
    RLock lock = lockProvider.getLock(key);
    try {
      boolean isUnlocked = lock.tryLock(waitSec, leaseSec, TimeUnit.SECONDS);
      if (!isUnlocked) {
        throw new RuntimeException("get lock fail");
      }

    } catch (InterruptedException e) {
      e.printStackTrace();
      lock.unlock();
    }
  }

  public void unlock(String key) {
    RLock lock = lockProvider.getLock(key);
    lock.unlock();
  }
}
