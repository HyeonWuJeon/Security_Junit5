package com.foodPro.demo.order.service;

import com.foodPro.demo.delivery.Delivery;
import com.foodPro.demo.item.domain.Item;
import com.foodPro.demo.item.repository.ItemRepository;
import com.foodPro.demo.member.domain.Member;
import com.foodPro.demo.member.dto.MemberDto;
import com.foodPro.demo.member.repository.MemberRepository;
import com.foodPro.demo.order.domain.Order;
import com.foodPro.demo.order.domain.OrderItem;
import com.foodPro.demo.order.domain.OrderStatus;
import com.foodPro.demo.order.dto.OrderDto;
import com.foodPro.demo.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    /**
     * FUNCTION :: 상품 주문
     * @param itemId :: 아이템 정보
     * @param count :: 수량
     * @return
     */
    public Long save(Long memberId, Long itemId, int count){
        // LINE :: 인증정보 추출
//        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        // LINE :: 회원 조회
        Member member = memberRepository.findById(memberId).orElseThrow(()->
                new IllegalStateException("로그아웃 상태에서 주문시도"));
        // LINE :: 아이템 조회
        Item item = itemRepository.findById(itemId).orElseThrow(()->new IllegalStateException("없는 아이템"));
        // LINE :: 배송정보 생성
        Delivery delivery = new Delivery();
        // LINE :: 배송주소
        delivery.setAddress(member.getAddress());
        // LINE :: 주문상품 생성
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        // LINE :: 주문 생성
        Order order = Order.createOrder(member, delivery, orderItem);

        //주문 저장
        orderRepository.save(order);
        return order.getId();
    }
    /**
     * FUNCTION :: 주문 취소
     */
    public void cancleOrder(Long orderId){
        //주문 엔티티 조회
        Order order = orderRepository.findById(orderId).orElseThrow(()->new IllegalStateException("주문되지 않은 상품"));
        //주문 엔티티 취소
        order.cancle();
    }

    /**
     * FUNCTION :: 주문상품 전체조회
     * @param status
     * @param email
     * @return
     */
    @Transactional(readOnly = true)
    public List<OrderDto.Response> findAllDesc(OrderStatus status, String email) {
        //페이징 적용하기, 검색정보
        return orderRepository.OrderSearch(status, email).stream()
                .map(OrderDto.Response::new)
                .collect(Collectors.toList());
    }

    /**
     * FUNCTION :: 상품 개별 조회
     */
    @Transactional(readOnly = true)
    public OrderDto.Response findById(Long id) {
        Order entity = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("조회된 주문내역이 없습니다. id=" + id));
        return new OrderDto.Response(entity);
    }

}