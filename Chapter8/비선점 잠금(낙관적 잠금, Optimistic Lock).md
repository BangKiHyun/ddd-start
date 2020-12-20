# 비선점 잠금(낙관적 잠금, Optimistic Lock)

## 선점 잠금으로 해결되지 않는 트랙잭셕

다음은 선점 잠금으로 해결되지 않는 트랜잭션 문제다.

1. 운영자가 배송을 위해 주문 정보를 조회한다. 시스템은 정보를 제공한다.
2. 고객이 배송지 변경을 위해 변경 폼을 요청한다. 시스템은 변경 폼을 제공한다.
3. 고객이 새로운 배송지를 입력하고 폼을 전송해서 배송지를 변경한다.
4. 운영자가 1번에서 조회한 주문 정보를 기준으로 배송지를 정하고 배송 상태 변경을 요청한다.

위와 같은 상황이라면 운영자가 배송 상태 변경 전에 배송지를 한 번 더 확인하지 않으면 운영자는 다른 배송지로 물건을 발송하게 되고, 고객은 배송지를 변경했음에도 불구하고 엉뚱한 곳을 주문한 물건을 받게 된다.

이 문제를 해결하기 위해 비선점 잠금을 사용하면 된다.

</br >

## 비선점 잠금(Optimistic Lock)

비선점 잠금 방식은 잠금을 해서 동시에 접근하는 것을 막는 대신 **변경한 데이터를 실제 DBMS에 반영하는 시점에 변경 가능 여부를 확인하는 방식**이다.

비선점 잠금을 구하려면 **애그리거트에 버전으로 사용할 숫자 타입의 프로퍼티를 추가**해야 한다. **애그리거트를 수정할 때마다 버전 값이 1씩 증가한다.**

~~~java
UPDATE aggtable Set version = version + 1, colx = ?, coly = ?
WHERE aggid = ? and version = 현재 버전 // 현재 버전과 동일할 경우에만 수정
~~~

</br >

### 시간 순서에 따른 그림

![image](https://user-images.githubusercontent.com/43977617/102707254-aac1d480-42dc-11eb-8880-999017c8a9f8.png)

위 그림에서 스레드1에서 먼저 애그리거트를 수정 해서 버전이 증가했다. 그렇기에 현재 버전과 일치하지 않은 스레드2는 데이터 수정에 실패하게 된다.

</br >

## JPA 비선점 잠금

JPA에서 비선점 잠금을 사용할때 버전으로 사용할 필드에 @Version 애노테이션을 붙이고 매핑되는 테이블에 버전을 저장할 칼럼을 추가하면 된다.

~~~java
@Entity
public class Order {
    
    @Id
    private Long id;
    
    @Version
    private long version;
}
~~~

기능을 실행하는 과정에서 애그리거트의 데이터가 변경되면 **JPA는 트랜잭션 종료 시점에 비선점 잠금을 위한 쿼리를 실행**한다.

</br >

### Exception

OptimisticLockingFailureException: 비선점 잠금에서 발생하는 exception으로 트랜잭션의 충돌 여부를 확인할 수 있다.

### Exception 세분화

```java
public class StartShippingService {

    private OrderRepository orderRepository;

    @Transactional
    public void startShipping(StartShippingRequest req){
        Order order = orderRepository.findById(req.getOrderNumber())
                .orElseThrow(() -> new NoSuchElementException());

        if(!order.matchVersion(req.getVersion())){
            throw new VersionConflictException(); // CustomException
        }
        order.startShipping();
    }
}
```

위 코드는 현재 애그리거트의 버전과 인자로 전달 받은 버전이 일치하지 않으면 버전이 충돌했다는 exception을 발생시켜 표현 계층에 알린다.

이렇게 되면 비선점 잠금과 관련해서 발생하는 두 개의 exception을 처리할 수 있게 된다.

- 스프링 프레임워크가 발생시키는 OptimisticLockingFailureException
  - 누군가가 거의 동시에 애그리거트를 수정했다는 것을 의미
- 응용 서비스 코드에서 발생시키는 VersionConflictException
  - 이미 누군가 애그리거트를 수정했다는 것을 의미

</br >

## 강제 버전 증가

애그리거트 루트 외에 다른 엔티티가 존재하는데 기능 실행 도중 **루트가 아닌 다른 엔티티의 값이 변경된다고 하자. 이 경우 JPA는 루트 엔티티의 버전 값을 증가하지 않는다.** 즉, 연관된 엔티티의 값이 변경된다고 해도 루트 엔티티 자체의 값을 바뀌는 게 없으므로 루트 엔티티의 버전 값을 갱신하지 않는다.

루트 애그리거트의 구성요소 중 일부 값이 바뀌면 논리적으로 그 애그리거트는 바뀐것이다. 따라서, 애그리거트 내의 어떤 구성요소의 상태가 바뀌면 루트 애그리거트의 버전 값이 증가해야 비선점 잠금이 올바르게 동작한다.

이 때 JPA에서 비선점 강제 버전 증가 잠금 모드를 사용할 수 있다.

### 코드 예제

~~~java
entityManager.find(Order.class, id, LockModeType.OPTIMISTIC_FORCE_INCREMENT)
~~~

`LockModeType.OPTIMISTIC_FORCE_INCREMENT`을 사용하면 **해당 엔티티의 상태가 변경되었는지 여부에 상관없이 트랜잭션 종료 시점에 버전 값 증가 처리를 한다.**

