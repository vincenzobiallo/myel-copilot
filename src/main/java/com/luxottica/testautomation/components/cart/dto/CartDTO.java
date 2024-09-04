package com.luxottica.testautomation.components.cart.dto;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
public class CartDTO {
    private final Map<String, Set<CartContentDTO>> content = new HashMap<>();
}
