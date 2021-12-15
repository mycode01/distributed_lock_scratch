package com.example.scratchspring.advisor;

import com.example.scratchspring.annotation.NeedLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LockingAdvisor {
  private final LockingService lockingService;

  public LockingAdvisor(LockingService lockingService) {
    this.lockingService = lockingService;
  }

  @Around("@annotation(com.example.scratchspring.annotation.NeedLock)")
  public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    NeedLock lockInfo = methodSignature.getMethod().getAnnotation(NeedLock.class);

    lockingService.lock(lockInfo.lockKey(), lockInfo.waitSec(), lockInfo.leaseSec());
    Object returnObject = joinPoint.proceed();
    lockingService.unlock(lockInfo.lockKey());

    return returnObject;
  }


}
