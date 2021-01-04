# BOUNDED CONTEXT 간 통합

## 온라인 쇼핑 사이트 예제

카탈로그 하위 도메인에 개인화 추천 기능을 도입한다고 가정해보자. 카탈로그 시스템을 개발하던 팀과 별도로 추천 시스템을 담당하는 팀에서 추천 시스템을 만들기로 했다. 이렇게 되면 카탈로그 BOUNDED CONTEXT와 추천 기능을 위한 BOUNDED CONTEXT가 생긴다.

</br >

## BOUNDED CONTEXT 간 통합 필요성

두 팀이 관련된 BOUNDED CONTEXT를 개발하면 자연스럽게 두 BOUNDED CONTEXT간 통합이 발생한다.

### 카탈로그와 추천 BOUNDED CONTEXT 간 통합이 필요한 기능

- 사용자가 제품 상세 페이지를 볼 때, 보고 있는 상품과 유사한 상품 목록을 하단에 보여주기.

</br >

### BOUNDED CONTEXT 간 흐름

1. 사용자가 카탈로그 BOUNDED CONTEXT에 추천 제품 목록을 요청
2. 카탈로그 BOUNDED CONTEXT는 추천 BOUNDED CONTEXT로부터 추천 정보를 요청
3. 추천 BOUNDED CONTEXT로부터 받아온 추천 제품 목록을 제공

위와 같은 흐름에서 카탈로그 컨텍스트와 추천 컨텍스트의 도메인 모델은 서로 다르다.

- 카탈로그: 제품을 중심으로 도메인 모델 구현
- 추천: 추천 연산을 위한 모델을 구현
  - 상품의 상세 정보를 포함하지 않음
  - 상품 번호 대신 아이템 ID라는 용어를 사용해서 식별자를 표현
  - 추천 순위와 같은 데이터 포함

</br >

## 외부 연동을 위한 도메인 서비스

카탈로그 시스템은 추천 시스템으로부터 추천 데이터를 받아오지만, 카탈로그 시스템에서는 추천의 도메인 모델을 사용하기보다는 카탈로그 도메인 모델을 사용해서 추천 상품을 표현해야 한다.

즉, 카탈로그 모델을 기반으로 하는 도메인 서비스를 이용해서 상품 추천 기능을 표현해야 한다.

### 도메인 서비스 예제

~~~java
public interface ProductRecommendationService {
    public List<Product> getRecommendationOf(ProductId id);
}
~~~

</br >

### 도메인 서비스를 구현한 클래스

~~~java
public class RecSystemClient implements ProductRecommendationService{
    private ProductRepository productRepository;

    @Override
    public List<Product> getRecommendationOf(ProductId id) {
        final List<RecommendationItem> items = getRecItems(id.getId());
        return toProducts(items);
    }

    private List<RecommendationItem> getRecItems(String id) {
        // 외부 추천 시스템을 위한 클라이언트라고 가정(REST API 호출)
        return externalRecClient.getRecs(id);
    }

    // 카탈로그 모델로 변환
    private List<Product> toProducts(List<RecommedationItem> items){
        return items.stream()
                .map(item -> toProductId(item.getItemId()))
                .map(productId -> productRepository.findById(productId))
                .collect(Collectors.toList());
    }

    private ProductId toProductId(String itemId) {
        return new ProductId(itemId);
    }
}
~~~

- 도메인 서비스를 구현한 클래스는 인프라스트럭처 영역에 위치한다.
- 외부 시스템과의 연동을 처리하고 외부 시스템의 모델과 현재 도메인 모델 간의 변환을 책임진다.

만약 두 모델 간의 변환 과정이 복잡하면 변환 처리를 위한 별도의 클래스를 만들어 처리해도 된다.

</br >

## 메시지 큐를 사용한 BOUNDED CONTEXT 통합

- REST API를 호출하는 것은 두 BOUNDED CONTEXT를 직접 통합하는 방식이다. 메시지 큐를 이용하면 간접 통합하는 방식을 사용할 수 있다.
- 이때 두 BOUNDED CONTEXT가 사용할 메시지의 데이터 구조를 맞춰야 한다.
  - **메시지 큐에 담을 데이터를 누가 제공하느냐에 따라 데이터 구조가 달라진다.**
  - 예로, 카탈로그 시스템에서 큐를 제공한다면 큐에 담기는 내용은 카탈로그 도메인을 따른다.
  - 다른 BOUNDED CONTEXT는 이 큐로부터 필요한 메시지를 수신하는 방식을 사용한다.
  - 이 방식은 한쪽에서 메시지를 출판하고 다른 쪽에서 메시지를 구독하는 **출판/구독 모델**을 따른다.

REST API와의 차이는 비동기로 데이터를 전달하는 것뿐이다. 이를 제외하면 REST API를 사용하는 것과 차이가 없다.

