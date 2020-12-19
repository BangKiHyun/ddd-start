# 선점 잠금(비관적 잠금, Pessimistic Lock)

선점 잠금은 먼저 애그리거트를 구한 스레드가 애그리거트 사용이 끝날 때까지 다른 스레드가 해당 애그리거트를 수정하는 것을 막는 방식이다.

### 선점 잠금 동작 방식 예제

![image](https://user-images.githubusercontent.com/43977617/102688156-0d14c980-4238-11eb-8f9e-a6d26b42e762.png)

- 스레드1이 애그리거트에 대한 잠금을 해제할 때까지 블로킹된다. 이후 애그리거트를 수정하고 커밋하면 잠금을 해제한다.
- 이 순간 **대기하고 있던 스레드2가 애그리거트에 접근**하게 된다.

한 스레드가 애그리거트를 구하고 수정하는 동안 다른 스레드가 수정할 수 없으므로 동시에 애그리거트를 수정할 때 발생하는 데이터 충돌 문제를 해소할 수 있다.

선점 잠금은 보통 DBMS가 제공하는 **행 단위 잠금**을 사용해서 구현된다. 오라클을 비롯한 다수 DBMS가 `for update`와 같은 쿼리를 사용해 특정 레코드에 한 사용자만 접근할 수 있는 잠금 장치를 제공한다.

- `select ~ for update` 구문을 풀어 말하면 '데이터 수정하려고 select하는 중이니 다른 사람들은 데이터에 손대지 마' 가 될 수 있다. 

</br >

## 선점 잠금의 주의점(교착 상태)

선점 잠금 기능을 사용할 때 잠금 순서에 따라 교착 상태(deadlock)가 발생하지 않도록 주의해야 한다.

### 교착 상태 예

1. 스레드1: A 애그리거트에 대해 선점 잠금 구함
2. 스레드2: B 애그리거트에 대해 선점 잠금 구함
3. 스레드1: B 애그리거트에 대한 선점 잠금 시도(대기)
4. 스레드2: A 애그리거트에 대해 선점 잠금 시도(대기)

선점 잠금에 따른 교착 상태는 상대적으로 사용자 수가 많을 때 발생할 가능성이 높고, 사용자 수가 많아지면 교착 상태에 빠지는 스레드가 더 빠르게 증가하게 된다.

해결책으로 잠금을 구할 때 **최대 대기 시간을 지정**하면 된다.

</br >

### JPA에서 최대 대기 시간 지정 방법

JPA에서 최대 대기 시간을 지정하려면 `hint`를 사용하면 된다.

~~~java
Map<String, Object> hints = new HashMap<>();
hints.put("javax.persistence.lock.timeout", 2000);
Order order = entityManager.find(Order.class, LockModeType.PESSIMISTIC_WRITE, hints);
~~~

</br >

## JPA에서의 선점 잠금 방법

JPA는 세 가지 선점 잠금 모드를 정의한다.

### PESSIMISTIC_READ

- `dirty read`가 발생하지 않을 때마다 공유 잠금(shared lock)을 획득하고 데이터가 `UPDATE`, `DELETE` 되는 것을 방지 할 수 있다.

### PESSIMISTIC_WRITE

- 배타적 잠금(exclusive lock)을 획득하고 데이터를 다른 트랜잭션에 `READ`, `UPDATE`, `DELETE` 하는것을 방지 할 수 있다.

### PESSIMISTIC_FORCE_INCREMENT

- `@Version`이 지정된 Entity와 협력하기 위해 도입되었다. `PESSIMISTIC_WRITE`와 유사하게 작동한다.

</br >

## Lock Scope

### PessimisticLockScope.NORMAL

- 기본적으로 해당 Entity만 잠금이 설정된다.
- 조인 상속을 사용하면 부모도 함께 잠금이 설정된다.

### PessimisticLockScope.EXTENDED

- `@ElementCollection`,` @OneToOne`, `@OneToMany` 등 연관된 Entity들도 잠금이 설정된다.

</br >

### 사용 예제

~~~java
Order order = entityManager.find(Order.class, orderNo, LockModeType.PESSIMISTIC_WRITE)
~~~

