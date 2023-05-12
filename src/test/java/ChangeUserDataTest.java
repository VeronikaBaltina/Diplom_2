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

//Изменить данные пользователя
public class ChangeUserDataTest {
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
    @Description("Изменить данные пользователя с авторизацией")
    public void changeUserDataWithAuthorizationTest() {
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

        User updatedUser = UserGenerator.getRandom();
        ValidatableResponse updateResponse = userClient.update(updatedUser, accessToken);
        statusCode = updateResponse.extract().statusCode();
        isSuccess = updateResponse.extract().path("success");
        String updatedEmail = updateResponse.extract().path("user.email");
        String updatedName = updateResponse.extract().path("user.name");
        assertEquals("Не изменилось поле email", updatedUser.getEmail().toLowerCase(), updatedEmail);
        assertEquals("Не изменилось поле name", updatedUser.getName(), updatedName);
        assertEquals("Неверный status code", 200, statusCode);
        assertTrue("Данные пользователя не изменены", isSuccess);

    }

    @Test
    @Description("Изменить данные пользователя без авторизации")
    public void changeUserDataWithoutAuthorizationTest() {
        User updatedUser = UserGenerator.getRandom();
        ValidatableResponse updateResponse = userClient.update(updatedUser, "");
        int statusCode = updateResponse.extract().statusCode();
        boolean isSuccess = updateResponse.extract().path("success");
        String message = updateResponse.extract().path("message");
        assertEquals("Неверный status code", 401, statusCode);
        assertFalse("Данные пользователя не изменены", isSuccess);
        assertEquals("Текст сообщения не совпадает", "You should be authorised", message);
    }

}
