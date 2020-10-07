package com.foodPro.demo.food.domain.item;

import com.foodPro.demo.food.domain.Item;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@DiscriminatorValue("C")
public class Clothes extends Item {
    private String size; // LINE :: 옷 사이즈

    protected Clothes(){
        super();
    }

    @Builder
    public Clothes(String name, int price, int stockQuantity, String size) {
        super(name, price, stockQuantity);
        this.size = size;
    }
}
