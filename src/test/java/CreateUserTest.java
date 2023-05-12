import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.example.client.UserClient;
import org.example.generator.UserGenerator;
import org.example.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

//Создать пользователя
public class CreateUserTest {
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
    @Description("создать уникального пользователя")
    public void createUniqueUserTest() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userClient.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isSuccess = createResponse.extract().path("success");
        accessToken = createResponse.extract().path("accessToken");
        assertEquals("Неверный status code", 200, statusCode);
        assertTrue("Пользователь не создан", isSuccess);
    }

    @Test
    @Description("создать пользователя, который уже зарегистрирован")
    public void createAlreadyRegisteredUserTest() {
        User user = UserGenerator.getRandom();
        ValidatableResponse createResponse = userClient.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isSuccess = createResponse.extract().path("success");
        accessToken = createResponse.extract().path("accessToken");
        assertEquals("Неверный status code", 200, statusCode);
        assertTrue("Пользователь не создан", isSuccess);

        ValidatableResponse failedResponse = userClient.create(user);
        int failedStatusCode = failedResponse.extract().statusCode();
        String message = failedResponse.extract().path("message");
        isSuccess = failedResponse.extract().path("success");
        assertEquals("Неверный status code", 403, failedStatusCode);
        assertEquals("Текст сообщения не совпадает", "User already exists", message);
        assertFalse("Пользователь создается", isSuccess);

    }

    @Test
    @Description("создать пользователя и не заполнить одно из обязательных полей")
    public void createUserWithAnEmptyFieldTest() {
        User user = UserGenerator.getRandom();
        user.setName("");
        ValidatableResponse createResponse = userClient.create(user);
        int statusCode = createResponse.extract().statusCode();
        boolean isSuccess = createResponse.extract().path("success");
        String message = createResponse.extract().path("message");
        assertEquals("Неверный status code", 403, statusCode);
        assertEquals("Текст сообщения не совпадает", "Email, password and name are required fields", message);
        assertFalse("Пользователь создается", isSuccess);
    }
}
