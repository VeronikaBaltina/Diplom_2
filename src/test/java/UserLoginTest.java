import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.example.client.UserClient;
import org.example.generator.UserGenerator;
import org.example.model.User;
import org.example.model.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

//Логин пользователя
public class UserLoginTest {
    private UserClient userClient;
    private String accessToken;

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    @Description("Удаление созданного пользователя")
    public void clearData() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("логин под существующим пользователем")
    public void loginUnderExistingUserTest() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userClient.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isSuccess = createResponse.extract().path("success");
        assertEquals("Неверный status code", 200, statusCode);
        assertTrue("Пользователь не создан", isSuccess);

        ValidatableResponse loginResponse = userClient.login(UserCredentials.from(user));
        accessToken = loginResponse.extract().path("accessToken");
        statusCode = loginResponse.extract().statusCode();
        isSuccess = loginResponse.extract().path("success");
        assertEquals("Неверный status code", 200, statusCode);
        assertTrue("Пользователь не авторизован", isSuccess);
    }

    @Test
    @Description("логин с неверным логином и паролем")
    public void loginWithInvalidUsernameAndPasswordTest() {
        UserCredentials userCredentials = new UserCredentials("tt@t.ru", "password");

        ValidatableResponse loginResponse = userClient.login(userCredentials);
        int statusCode = loginResponse.extract().statusCode();
        boolean isSuccess = loginResponse.extract().path("success");
        String message = loginResponse.extract().path("message");
        assertEquals("Неверный status code", 401, statusCode);
        assertFalse("Пользователь авторизован", isSuccess);
        assertEquals("Текст сообщения не совпадает", "email or password are incorrect", message);
    }

}
