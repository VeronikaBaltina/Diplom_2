package org.example.client;

import io.restassured.path.json.JsonPath;
import org.example.client.base.StellarBurgersClient;
import org.example.model.Ingredients;

import static io.restassured.RestAssured.given;

public class IngredientsClient extends StellarBurgersClient {
    private static final String INGREDIENTS_URL = BASE_URL + "ingredients";

    public Ingredients get() {
        JsonPath jsonPath = given()
                .spec(getBaseReqSpec())
                .when()
                .get(INGREDIENTS_URL)
                .then()
                .extract()
                .body()
                .jsonPath();

        return jsonPath.getObject("", Ingredients.class);
    }
}
