
단순 spring cache 테스트를 하다가 어쩌다보니 redisson을 이용한 분산락 처리를 테스트해보았음.

redis client로 jedis, lettuce가 있고 부트 스타터로 lettuce를 지원하지만,
lettuce는 분산락 처리를 지원하지 않음. 직접 구현해야함.

redisson은 스타터로 제공하지 않지만  
기본적으로 타임아웃과 publish/subscribe 전략으로 스핀락을 구현하지 않아 부하를 줄이며,   
라이브러리에서 제공하는 자료형으로 분산락 처리를 도와줄수 있으므로 그냥 redisson을 이용해서 테스트진행함.

소스코드랑 패키지가 약간 지저분한데, 처음에는 분산락 처리가 가능한가만 테스트하다가
@Transactional과 함께 사용하지 못한다는 블로그 글이 있어서 확인을 해보려고 코드를 상세하게 늘렸음.

controller 기준  
case1
  - @Transactional 이 걸려있을 경우 락 반환, 트랜잭션 처리 잘 됨을 확인함.  
    다만 lock 획득과 트랜잭션 획득이 교차되어 사용된다면  
    @Transaction 의 exception 처리 규칙에 따라 문제가 생길수 있을거 같음.  
    LockingService를 공용으로 사용할수 있도록 callback을 전달받는 형태로 구현
    
case2 
  - 트랜잭션이 키의 릴리즈 타임보다 오래걸릴시에 락의 반환처리.  
    곤란하게도 트랜잭션이 완료되지 않아도 ```lock.tryLock(waitSec, leaseSec, ..)``` 으로  
    릴리즈 제한시간을 파라메터로 넘겼기 때문에
    트랜잭션은 끝나지 않아도 락의 반환처리가 먼저 일어남.  
    정말 원자성이 확실히 필요하고 작업시간을 예상 할수 없는 경우   
    leaseSec 파라메터를 사용하지 않아야하는데,  
    이 경우 락의 반환처리를 수동으로 빈틈없이 처리해야 할거 같음.

case3
  - case1을 콜백형태로 넘기기보다는 어노테이션과 aop로 처리하는게  
    자연스러워 보일수도 있다는 생각이 들어 ```LockingAdvisor```와 ```NeedLock``` 어노테이션을 추가함.  
    다만 이 경우 런타임에 분산락 획득을 하고 안하고를 선택할수 없게 됨.  
    결국 선택의 문제..


참고한 문서 

[https://hyperconnect.github.io/2019/11/15/redis-distributed-lock-1.html](https://hyperconnect.github.io/2019/11/15/redis-distributed-lock-1.html)  
[https://kkambi.tistory.com/196](https://kkambi.tistory.com/196)  
[https://github.com/redisson/redisson#quick-start](https://github.com/redisson/redisson#quick-start)  