package org.example.client;


import io.restassured.response.ValidatableResponse;
import org.example.client.base.StellarBurgersClient;
import org.example.model.OrderData;

import static io.restassured.RestAssured.given;

public class OrderClient extends StellarBurgersClient {
    private static final String ORDER_URL = BASE_URL + "orders/";

    public ValidatableResponse create(OrderData orderData, String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", accessToken)
                .body(orderData)
                .when()
                .post(ORDER_URL)
                .then();
    }

    public ValidatableResponse get(OrderData orderData, String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", accessToken)
                .body(orderData)
                .when()
                .get(ORDER_URL)
                .then();
    }
}
