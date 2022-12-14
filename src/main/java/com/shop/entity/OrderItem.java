package com.shop.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private int orderPrice;

    private int count;

    //private LocalDateTime regTime;

    //private LocalDateTime updateTime;

    public static OrderItem createOrderItem(Item item, int count) {
        OrderItem orderItem = new OrderItem();

        //주문할 상품과 주문 수량을 세팅
        orderItem.setItem(item);
        orderItem.setCount(count);

        //현재 시간을 기준으로 상품 가격을 주문 가격으로 세팅. 상품 가격은 시간에 따라서 달라질 수 있음.
        orderItem.setOrderPrice(item.getPrice());

        //주문 수량만큼 상품의 재고 수량 감소
        item.removeStock(count);
        return orderItem;
    }

    /**
     * 주문 가격과 주문 수량을 곱해서 해당 상품을 주문한 총 가격을 계산하는 메서드
     *
     * @return
     */
    public int getTotalPrice() {
        return orderPrice * count;
    }

    public void cancel(){
        this.getItem().addStock(count);
    }
}
