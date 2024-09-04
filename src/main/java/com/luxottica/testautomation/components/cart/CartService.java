package com.luxottica.testautomation.components.cart;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.luxottica.testautomation.components.cart.dto.CartContentDTO;
import com.luxottica.testautomation.components.cart.dto.CartDTO;
import com.luxottica.testautomation.configuration.Config;
import com.luxottica.testautomation.models.MyelStore;
import com.luxottica.testautomation.models.User;
import com.luxottica.testautomation.utils.RequestUtils;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CartService {

    @Autowired
    private Config config;

    public CartDTO getCart(Playwright playwright, User user) {
        String url = config.getBaseUrl().concat(config.getPrecart());
        APIRequestContext context = RequestUtils.buildContext(playwright, user.getUsername());
        url = url.replace("{storeIdentifier}", user.getStore()).replace("{locale}", user.getLocale());

        APIResponse response = context.get(url);
        JsonObject responseBody = JsonParser.parseString(new String(response.body())).getAsJsonObject();

        JsonObject data = responseBody.getAsJsonObject("data");
        JsonObject cart = data.getAsJsonArray("multidoorResponseList").get(0).getAsJsonObject();
        JsonArray contents = cart.getAsJsonArray("categoryResponseList");

        CartDTO cartDTO = new CartDTO();

        contents.asList().stream().map(JsonObject.class::cast).forEach(content -> {
            String categoryIdentifier = content.get("productCategoryIdentifier").getAsString();
            Set<CartContentDTO> categoryContents = new HashSet<>();

            JsonArray itemList = content.getAsJsonArray("orderResponseItemList");
            itemList.asList().stream().map(JsonObject.class::cast).forEach(item -> {
                String upc = item.getAsJsonObject("productsDetails").get("upc").getAsString();
                CartContentDTO cartContent = new CartContentDTO(upc);
                categoryContents.add(cartContent);
            });

            cartDTO.getContent().put(categoryIdentifier, categoryContents);
        });

        return cartDTO;
    }

    public void clearCart(Playwright playwright, User user) {
        String url = config.getBaseUrl().concat(config.getPrecart());
        APIRequestContext context = RequestUtils.buildContext(playwright, user.getUsername());
        url = url.replace("{storeIdentifier}", user.getStore()).replace("{locale}", user.getLocale());

        APIResponse response = context.delete(url);

        if (response.status() != 204) {
            throw new RuntimeException("Failed to clear cart");
        }
    }
}
