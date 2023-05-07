import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.example.client.OrderClient;
import org.example.client.UserClient;
import org.example.generator.UserGenerator;
import org.example.model.OrderData;
import org.example.model.User;
import org.example.model.UserCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

//Получить заказы конкретного пользователя
public class GetOrdersSpecificUserTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;

    @BeforeClass
    public static void globalSetUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @After
    @Description("Удаление созданного пользователя")
    public void clearData() {
        if (accessToken != null) {
            userClient.delete(accessToken);
        }
    }

    @Test
    @Description("Получить заказы конкретного авторизованного пользователя")
    public void getOrdersFromSpecificAuthorizedUserTest() {
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

        List<String> ingredients = List.of("61c0c5a71d1f82001bdaaa6c");
        OrderData orderData = new OrderData(ingredients);
        ValidatableResponse createOrderResponse = orderClient.create(orderData, accessToken);
        statusCode = createOrderResponse.extract().statusCode();
        isSuccess = createOrderResponse.extract().path("success");
        assertEquals("Неверный status code", 200, statusCode);
        assertTrue("Заказ не создан", isSuccess);

        ValidatableResponse getOrderResponse = orderClient.get(orderData, accessToken);
        statusCode = getOrderResponse.extract().statusCode();
        isSuccess = getOrderResponse.extract().path("success");
        assertEquals("Неверный status code", 200, statusCode);
        assertTrue("Списока заказов нет", isSuccess);
    }

    @Test
    @Description("Получить заказы конкретного не авторизованного пользователя")
    public void getOrdersFromSpecificNonAuthorizedUserTest() {
        List<String> ingredients = List.of("61c0c5a71d1f82001bdaaa6c");
        OrderData orderData = new OrderData(ingredients);
        ValidatableResponse getOrderResponse = orderClient.get(orderData, "");
        int statusCode = getOrderResponse.extract().statusCode();
        boolean isSuccess = getOrderResponse.extract().path("success");
        String message = getOrderResponse.extract().path("message");
        assertEquals("Неверный status code", 401, statusCode);
        assertFalse("Есть список заказов", isSuccess);
        assertEquals("Текст сообщения не совпадает", "You should be authorised", message);
    }
}
