

# CQRS(Command Query Responsibility Segregation)

## 단일 모델의 단점

주문 내역 조회 기능을 구현하려면 여러 애그리거트에서 데이터를 가져와야 한다.

- Order: 주문정보
- Product: 상품 이름
- Member: 회원 이름, 아이디

조회 하면 특성상 조회 속도가 빠를수록 좋은데 여러 애그리거트에서 데이터를 가져와야 할 경우 구현 방법을 고민해야 한다.

조회 화면의 특성에 따라 같은 연관도 즉시 로딩이나 지연 로딩을 처리해야 하기 때문이다. 또한, 경우에 따라 DBMS가 제공하는 전용 기능을 이용해서 조회 쿼리를 작성해야 해서 JPA의 네이티브 쿼리를 사용해야 할 수도 있다.

이런 고민이 발생하는 이유는 시스템의 상태를 변경할 때와 조회할 때 단일 도메인 모델을 사용하기 때문이다.

객체 지향으로 도메인 모델을 구현할 때 주로 사용하년 ORM 기법은 Order.cancel()처럼 도메인의 상태 변경을 구현하는 데는 적합하지만, 주문 상세 조회 화면처럼 여러 애그리거트에서 데이터를 가져와 출력하는 기능을 구현하기에는 고려해야할 점이 많다.

</br >

## CQRS(Command Query Responsibility Segregation)

시스템이 제공하는 기능은 크게 두 가지로 나눌수 있다.

1. 상태 변경(Create, Update, Delete)
2. 상태 정보 조회(Select)

도메인 모델 관점에서 상태 변경 기능은 주로 한 애그러트의 상태를 변경한다. 반면에 조회 기능은 한 애그리거트의 데이터를 조회할 수도 있지만 두 개 이상의 애그리거트에서 데이터를 조회할 수도 있다.

이는 상태를 변경하는 범위와 상태를 조회하는 범위가 정확하게 일치하지 않기 때문에 단일 모델로 두 종류의 기능을 구현하면 모델이 불필요하게 복잡해진다.

단일 모델을 사용할 때 발생하는 복잡도를 해결하기 위한 방법이 바로 CQRS다.

</br >

## CQRS란?

CQRS는 상태를 변경하는 명령(Command)을 위한 모델과 상태를 제공하는 조회(Query)를 위한 모델을 분리하는 패턴이다.

![image](https://user-images.githubusercontent.com/43977617/122908646-57b20280-d38f-11eb-8c41-d3cd6ae912c1.png)

도메인이 복잡할수록 명령 기능과 조회 기능이 다루는 데이터 범위에 차이가 발생하는데, 이 두 기능을 단일 모델로 처리하게 되면 조회 기능의 로딩 속도를 위해 모델 구현이 필요 이상으로 복잡해지는 문제가 밸상한다.

예를 들어, 온라인 쇼핑에서 다양한 차원에서 주문/판매 통계를 조회해야 한다고 하면, JPA기반의 단일 도메인 모델을 사용하면 통계 값을 빠르게 조회하기 위해 JPA와 관련된 다양한 성능 관련 기능을 모델에 적용해야 한다.

이런 도메인에 CQRS를 적용하면 통계를 위한 조회 모델을 별도로 만들기 때문에 조회로 인해 도메인 모델이 복잡해지는 것을 막을 수 있다.

</br >

## 서로 다른 데이터 저장소

명령 모델과 조회 모델이 서로 다른 데이터 저장소를 사용할 수도 있다.

명령 모델은 트랜잭션을 지원해주는 RDBMS를 사용하고, 조회 모델을 조회 성능이 좋은 메모리 기반 NoSQL을 사용할 수 있을 것이다.

![image](https://user-images.githubusercontent.com/43977617/122909431-24bc3e80-d390-11eb-9450-e7ed3a0f865d.png)

</br >

## CQRS와 동기화

두 데이터 저장소 간의 데이터 동기화가 필요할 때가 있다. 이때는 이벤트를 활용해 처리하면 된다.

명령 모델에서 상태를 변경하면 이에 해당하는 이벤트가 발생하고, 그 이벤트를 조회 모델에 전달해서 변경 내역을 반영하면 된다.

</br >

명령 모델과 조회 모델이 서로 다른 데이터 저장소를 사용할 경우 데이터 동기화 시점에 따라 구현 방식이 달라질 수 있다.

1. 명령 모델에서 데이터가 바뀌자마자 변경 내용을 바로 조회 모델에 반영해야 할 경우
2. 서로 다른 저장소의 데이터를 특정 시간 안에만 동기화 해주면 되는 경우

첫 번째 경우에는 동기 이벤트와 글로벌 트랜잭션을 사용해서 실시간으로 동기화를 해주면 된다. 하지만 동기 이벤트와 글로벌 트랜잭션을 사용하면 응답 속도와 처리량을 떨어지는 단점이 있다.

두 번째 경우에는 비동기로 데이터를 전송해도 된다. 이러한 경우라면 비동기 데이터를 보냄으로써 데이터 동기화로 인해 명령 모델의 성능이 나빠지지 않도록 할 수 있다.

</br >

## CQRS 장단점

### 장점

- 명령 모델을 구현할 때 도메인 자체에 집중할 수 있다. 
  - 복잡한 도메인은 주로 상태 변경 로직이 복잡한데 명령 모델과 조회 모델을 구분하면 조회 성능을 위한 코드가 명령 모델에 없으므로 도메인 로직을 구현하는 데 집중할 수 있다.
  - 명령 모델에서 조회 관련 로직이 사라져 복잡도를 낮춰준다.
- 조회 성능을 향상시키는 데 유리하다.
  - 조회 단위로 캐시 기술을 적용할 수 있고, 조회에 특화된 쿼리를 마음대로 사용할 수 있다.
  - 조회 전용 저장소를 사용하면 조회 처리량을 대폭 놀릴 수도 있다. 조회 전용 모델을 사용하기 때문에 조회 성능을 높이기 위한 코드가 명령 모델에 영향을 주지 않는다.

### 단점

- 구현해야 할 코드가 많다.
  - 단일 모델을 사용할 때 발생하는 복잡함 때문에 발생하는 구현 비용과 조회 전용 모델을 만들 때 발생하는 구현 비용을 따져봐야 한다.
- 더 많은 구현 기술이 필요하다.
  - 명령 모델과 조회 모델을 다른 구현 기술을 사용해서 구현하기도 하고 경우에 따라 다른 저장소를 사용하기도 한다. 데이터 동기화를 위해 메시징 시스템을 도입해야 할 수도 있다.

</br >

## 마무리

CQRS 패턴은 장단점을 고려해서 도입할지 여부를 결정해야 한다. 도메인이 복잡하지 않은데 CQRS를 도입하면 두 모델을 유지하는 비용만 높아지고 얻을 수 있는 이점은 없다.

반면에 트래픽이 높은 서비스인데 단일 모델을 고집하면 유지보수 비용이 오히려 높아질 수 있으므로 CQRS 도입을 고려해보자.

