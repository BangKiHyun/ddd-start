# 이벤트(Event) 개요

## 시스템 간 강결합 문제

### 환불 기능 예제

쇼핑몰에서 구매를 취소하면 환불을 처리해야 한다. 다음은 응용 서비스에 환불 기능을 실행하는 코드다.

```java
public class CancelOrderService {
    
    private RefundService refundService;
    
    @Transactional
    public void cancel(OrderNo orderNo) {
        Order order = findOrder(orderNo);
        order.cancel();
        
        order.refundStarted();
        try {
            refundService.refund(order.getPaymentId()); // 외부 서비스 성능에 직접 영향을 받음
            order.refundCompleted();
        }catch (Exception ex){
            
        }
    }
}
```

보통 결제 시스템은 외부에 존재하므로 `RefundService`는 외부의 환불 시스템 서비스를 호출하는데, 이때 두 가지 문제가 발생한다.

### 1. 외부 서비스가 정상이 아닐 경우 트랜잭션 처리 문제

- 환불 기능을 실행하는 과정에서 익셉션이 발생하면 트랜잭션을 롤백해야 할까? 일단 커밋해야 할까?
  - 환불에 실패했으므로 주문 취소 트랜잭션을 롤백한다.
  - 주문 취소 상태로 변경하고 환불만 나중에 다시 시도한다.

### 2.  성능 문제

- 환불을 처리하는 외부 시스템의 응답 시간이 길어지면 그만큼 대기 시간도 발생한다.
- 환불 처리 기능이 30초가 걸리면 주문 취소 기능은 30초만큼 대시 시간이 증가한다.
- 즉, 외부 서비스 성능에 직접적인 영향을 받는 문제가 있다.

위의 문제가 발생하는 이유는 주문 BOUNDED CONTEXT와 결제 BOUNDED CONTEXT 간의 강결합(high coupling) 때문이다.

이런 강결합을 없앨 수 있는 방법으로 이벤트가 있다. 특히 비동기 이벤트를 사용하면 두 시스템 간의 결합을 크게 낮출 수 있다.

</br >

## 이벤트(Event)

### 이벤트란?

- 이벤트는 '과거에 벌어진 어떤 것'을 뜻한다. 예로, 주문을 취소했다면 '주문을 취소했음 이벤트'가 발생했다고 할 수 있다.
- **이벤트가 발생한다는 것은 상태가 변경됐다는 것을 의미힌다.** 즉, '주문 취소됨 이벤트'가 발생한 이유는 주문이 취소 상태로 바뀌었기 때문이다.
- 이벤트가 발생하면 **그 이벤트에 반응하여 원하는 동작을 수행하는 기능을 구현한다.**

</br >

### 도메인 모델에서의 이벤트

- 도메인 모델에서 도메인의 상태 변경을 이벤트로 표현할 수 있다.
- '~할 때', '~가 발생하면', '만약 ~하면' 과 같은 요구사항은 도메인의 상태 변경과 관련된 경우가 많다.
  - 즉, 이런 요구사항을 이벤트를 이용해 구현할 수 있다.
- ex) '주문을 취소할 때 이메일을 보낸다' 라는 요구사항
  - 상태 변경: '주문을 취소 할 때', 주문이 취소 상태롤 바뀌는 것을 의미
  - '주문 취소됨 이벤트'를 활용

</br >

## 이벤트 관련 구성요소

![image](https://user-images.githubusercontent.com/43977617/104020983-5cc82e80-5201-11eb-924a-182513fec5d7.png)

도메인 모델에서 이벤트 주체는 엔티티, 벨류, 도메인 서비스와 같은 도메인 객체다.

도메인 객체는 도메인 로직을 실행해서 상태가 바뀌면 관련 이벤트를 발생한다.

</br >

### 이벤트 핸들러(handler)

- 이벤트 핸들러는 생성 주체가 발생한 이벤트를 전달받아 이벤트에 담긴 데이터를 이용해서 원하는 기능을 실행한다.
- 예로, '주문 취소됨 이벤트'를 받는 이벤트 핸들러는 해당 주문의 주문자에게 SMS로 주문 취소 사실을 통지할 수 있다.

### 이벤트 디스패처(dispatcher)

- 이벤트 디스패처는 이벤트 생성 주체와 이벤트 핸들러를 연결해 준다.
- 이벤트 생성 주체는 이벤트를 생성해서 디스패처에 이벤트를 전달한다.
- 이벤트 디스패처의 구현 방식에 따라 이벤트 생성과 처리를 동기나 비동기로 실행하게 된다.

</br >

## 이벤트의 구성

이벤트는 다음과 같이 구성된다.

- 이벤트 종류: 클래스 이름으로 이벤트 종류를 표현
- 이벤트 발생 시간
- 추가 데이터: 주문번호, 신규 배송지 정보 등 이벤트와 관련된 정보

</br >

## 배송지 변경 이벤트 예제

### 배송지 변경을 위한 이벤트 클래스

```java
public class ShippingInfoChangedEvent {
    
    private String orderNumber;
    private long timestamp;
    private ShippingInfo newShippingInfo;
}
```

이벤트는 현재 기준으로 과거에 벌어진 것을 표현하기 때문에 이벤트 이름에 과서 시제를 사용한다.(Changed)

</br >

### Order 클래스(이벤트 발생 주체)

```java
public class Order {
    
    public void changeShippingInfo(ShippingInfo newShippingInfo){
        verifyNotYetShipped();
        setShippingInfo(newShippingInfo);
        Events.raise(new ShippingInfoChangedEvent()number, newShippingInfo);
    }
}
```

</br >

### EventHandler

```java
public class ShippingInfoChangedHandler implements EventHandler<ShippingInfoChangedEvent> {

    @Override
    public void handle(ShippingInfoChangedEvent event) {
        shippingInfoSynchronizer.sync(
                event.getOrderNumber(),
                event.getNewShippingInfo()
        );
    }
}
```

- `ShippingInfoChangedEvent`를 처리하는 Handler는 Dispatcher로부터 이벤트를 전달받아 필요한 작업을 수행한다.
- **이벤트는 이벤트 핸들러가 작업을 수행하는 데 필요한 최소한의 데이터를 담아야 한다.** 
  - 그렇지 않으면 핸들러는 필요한 데이터를 읽기 위해 관련 API를 호추하거나 DB에서 데이터를 직접 읽어와야 한다.

</br >

## 이벤트 용도

### 1. 트리거

- 도메인의 상태가 바뀔 때 마다 후처리를 해야 할 경우 후처리를 실행하기 위한 트리거로 이벤트를 사용할 수 있다.
- 주문의 경우 주문 취소 이벤트가 트러거가 되고, 주문을 취소하면 환불을 처리해야 하는데, 이때 환불 처리를 위한 트리거로 주문 취소 이벤트를 사용할 수 있다.

### 2. 데이터 동기화

- 이벤트는 서로 다른 시스템 간의 데이터 동기화로 사용할 수 있다.
- 예로, 배송지를 변경하면  외부 배송 서비스에 바뀐 배송지 정보를 전송해야 한다.
  - 주문 도메인은 배송지 변경 이벤트를 발생시키고 이벤트 핸들러는 외부 배송 서비스와 배송지 정보를 동기화한다.

</br >

## 이벤트 장점

### 1. 서로 다른 도메인 로직이 섞이는 것을 방지할 수 있다.

**주문 로직과 결제 로직이 섞여있는 Order class**

```java
public class Order {
    
    public void cancel(RefundService refundService) {
        // 주문 로직
        verifyNotYetShipped();
        this.state = OrderState.CANCELED;
        
        // 결제 로직
        this.refundStatus = State.REFUND_STARTED;
        try {
            refundService.refund(getpaymentId());
            this.refundStatus = State.REFUND_COMPLETED;
        }catch (Exception e){
            
        }
    }
}
```

- 위 코드를 보면 주문 로직과 결제 로직이 섞여있다.
- 구매 취소 로직에 이벤트를 적용하면 환불 로직을 없앨 수 있다.

**이벤트 적용**

~~~java
public class Order {
    
    public void cancel(RefundService refundService) {
        verifyNotYetShipped();
        this.state = OrderState.CANCELED;
        
        this.refundStatus = State.REFUND_STARTED;
        Events.raise(new OrderCanceledEnvent(number.getNumber()));
    }
 }
~~~

</br >

### 2. 확장에 용이하다.

- 이벤트 핸들러를 사용하면 기능 확장이 용이하다.
- 구매 취소 시 환불과 함께 이메일로 취소 내용을 보내고 싶다면 이메일 발송을 처리하는 핸들러를 구현하고 디스패처에 등록하면 된다.

