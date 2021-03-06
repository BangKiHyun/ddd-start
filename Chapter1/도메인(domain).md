# 도메인(domain)

## 도메인

소프트웨어로 해결하고자 하는 문제 영역

### 하위 도메인

- 한 도메인은 여러 하위 도메인으로 나눌 수 있다.
- 한 하위 도메인은 다른 하위 도메인과 연동하여 완전한 기능을 제공한다.
  - ex) 고객이 물건을 구매하면 주문, 결제, 배송, 혜택 하위 도메인의 기능이 엮이게 된다.
- 하위 도메인이 다루는 영역은 서로 다르기 때문에 같은 용어라도 하위 도메인 마다 의미가 달라질 수 있다.
  - 카탈로그 도메인의 상품: 상품 가격, 상세 내용을 담고 있는 정보 의미
  - 배송 도메인의 상품: 고객에게 실제 배송되는 물리적인 상품을 의미

</br >

## 도메인 모델

특정 도메인을 개념적으로 표현한 것

도메인 모델을 사용하면 여러 관계자들이 동일한 모습으로 도메인을 이해하고 도메인 지식을 공유하는 데 도움이 된다.

### 주문 도메인 사나리오 예제

- 주문을 하려면 상품을 몇 개 살지 선택하고 배송지를 입력한다.
- 선택한 상품 가격을 이용해서 총 지불 금액을 계산하고 금액 지불을 위한 결제 수단을 선택한다.
- 주문한 뒤에도 배송 전이면 배송지 주소를 변경하거나 주문을 취소할 수 있다.

</br >

### 객체 기반 주문 도메인 모델로 구성

![image](https://user-images.githubusercontent.com/43977617/100638116-fbe24680-3376-11eb-9b35-00ac6eb314fd.png)

위 시나리오를 객체 모델로 구성한 그림이다.

### 특징

- 기능과 데이터를 함께 보여준다.
- 도메인이 제공하는 기능과 도메인의 주요 데이터 구성을 파악하는데 용이하다.

</br >

### 상태 다이어그램을 이용한 주문 상태 모델링

![image](https://user-images.githubusercontent.com/43977617/100638993-fc2f1180-3377-11eb-8aea-98d49cd40e13.png)

상태 다이어그램을 이용해서 주문의 상태 전이를 모델링한 그림이다.

이 다이어그램을 보면 상품 준비중 상태에서 주문을 취소하면 결제 취소가 함께 이루어진다는 것을 알 수 있다.

</br >

### 개념 모델

- 순수하게 문제를 분석한 결과물
- **데이터베이스, 트랜잭션 처리, 성능, 구현 기술과 같은 것들을 고려하지 않기 때문에 실제 코드를 작성할 때 개념 모델을 그대로 사용할 수 없음**
- 개념 모델을 처음부터 완벽하게 도메인을 표현하기 힘듦.
  - 소프트웨어를 개발하는 동안 개발자와 관계자들은 해당 도메인을 더 잘 이해하게 됨.
  - 결국 도메인에 대한 새로운 지식이 쌓여 모델을 보완하거나 수정하는 일이 발생
  - 따라서 **전반적인 개요를 알 수 있는 수준으로 개념 모델을 작성한 후, 구현 모델로 점진적으로 발전시켜 나가야 함.**

</br >

## 도메인 모델 패턴

### 일반적인 애플리케이션 아키텍처 구성

- 사용자 인터페이스(UI) 또는 표현(Presentation)
  - 사용자의 요청을 처리하고 사용자에게 정보를 보여줌
  - 여기서 사용자는 소프트웨어를 사용하는 사람뿐만 아니라 외부 시스템도 사용자가 될 수 있음
- 응용(Application)
  - 사용자가 요청한 기능 실행
  - 업무 로직을 구현하지 않으며 도메인 계층을 조합해서 기능 실행
- 도메인
  - 시스템이 제공할 도메인 규칙 구현
- 인프라스트런쳐
  - 데이터베이스나 메시징 시스템과 같은 외부 시스템과의 연동을 처리

</br >

## 도메인 모델 도출

- 도메인을 모델링할 때 기본이 되는 작업을 모델을 구성하는 **핵심 구성요소, 규칙, 기능**을 찾는 것이다. 이 과정은 요구사항에서 출발한다.

### 요구사항 예제

- 한 상품을 한 개 이상 주문할 수 있다.
- 각 상품의 구매 가격 합은 상품 가격에 구매 개수를 곱한 값이다.

### 도출

- 주문 항목을 표현하는 `OrderLine`은 적어도 주문할 상품, 상품의 가격, 구매 개수를 포함하고 있어야 한다.
- 각 구매 항목의 구매 가격도 제공해야 한다.

### 코드

~~~java
public class OrderLine {
    private Product product;
    private int price;
    private int quantity;
    private int amounts;

    public OrderLine(Product product, int price, int quantity) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.amounts = calculateAmounts();
    }

    private int calculateAmounts() {
        return price * quantity;
    }
}
~~~

