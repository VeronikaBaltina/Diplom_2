package org.example.client;

import io.restassured.response.ValidatableResponse;
import org.example.client.base.StellarBurgersClient;
import org.example.model.User;
import org.example.model.UserCredentials;

import static io.restassured.RestAssured.given;


public class UserClient extends StellarBurgersClient {
    private static final String REGISTER_USER_URL = BASE_URL + "auth/register";
    private static final String AUTH_USER_URL = BASE_URL + "auth/user";
    private static final String LOGIN_USER_URL = BASE_URL + "auth/login";
    public ValidatableResponse create(User user) {
        return given()
                .spec(getBaseReqSpec())
                .body(user)
                .when()
                .post(REGISTER_USER_URL)
                .then();

    }
    public ValidatableResponse delete(String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", accessToken)
                .when()
                .delete(AUTH_USER_URL)
                .then();
    }

    public ValidatableResponse login(UserCredentials userCredentials) {
        return given()
                .spec(getBaseReqSpec())
                .body(userCredentials)
                .when()
                .post(LOGIN_USER_URL)
                .then();

    }
    public ValidatableResponse update (User user, String accessToken){
        return given()
         .spec(getBaseReqSpec())
                .header("authorization", accessToken)
                .body(user)
                .when()
                .patch(AUTH_USER_URL)
                .then();
    }
}
