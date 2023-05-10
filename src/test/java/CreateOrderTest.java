import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.ValidatableResponse;
import org.example.client.IngredientsClient;
import org.example.client.OrderClient;
import org.example.client.UserClient;
import org.example.generator.UserGenerator;
import org.example.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

//Создать заказ
public class CreateOrderTest {
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
    @Description("Создать заказ с ингредиентами")
    public void createOrderWithIngredientsTest() {
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

        List<IngredientData> ingredientDataList = getIngredientsDataList();

        OrderData orderData = new OrderData(List.of(ingredientDataList.get(0).getId()));
        ValidatableResponse createOrderResponse = orderClient.create(orderData, accessToken);
        statusCode = createOrderResponse.extract().statusCode();
        isSuccess = createOrderResponse.extract().path("success");
        assertEquals("Неверный status code", 200, statusCode);
        assertTrue("Заказ не создан", isSuccess);
    }

    @Test
    @Description("Создать заказ без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
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

        OrderData orderData = new OrderData(null);
        ValidatableResponse createOrderResponse = orderClient.create(orderData, accessToken);
        statusCode = createOrderResponse.extract().statusCode();
        isSuccess = createOrderResponse.extract().path("success");
        String message = createOrderResponse.extract().path("message");
        assertEquals("Неверный status code", 400, statusCode);
        assertFalse("Заказ с ингридиентами", isSuccess);
        assertEquals("Текст сообщения не совпадает", "Ingredient ids must be provided", message);
    }

    @Test
    @Description("Создать заказ с неверным хешем ингредиентов")
    public void createOrderWithInvalidHashOfIngredientsTest() {
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

        List<String> ingredients = List.of("61c0c5aa6c");
        OrderData orderData = new OrderData(ingredients);
        ValidatableResponse createOrderResponse = orderClient.create(orderData, accessToken);
        statusCode = createOrderResponse.extract().statusCode();
        assertEquals("Неверный status code", 500, statusCode);
    }

    @Test
    @Description("Создать заказ без авторизации")
    public void createOrderWithoutAuthorizationTest() {
        List<IngredientData> ingredientDataList = getIngredientsDataList();

        OrderData orderData = new OrderData(List.of(ingredientDataList.get(0).getId()));
        ValidatableResponse createOrderResponse = orderClient.create(orderData, "");
        int statusCode = createOrderResponse.extract().statusCode();
        boolean isSuccess = createOrderResponse.extract().path("success");
        assertEquals("Неверный status code", 200, statusCode);
        assertTrue("Заказ не создан", isSuccess);
    }

    @Test
    @Description("Создать заказ c авторизации")
    public void createOrderWithAuthorizationTest() {
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

        List<IngredientData> ingredientDataList = getIngredientsDataList();

        OrderData orderData = new OrderData(List.of(ingredientDataList.get(0).getId()));
        ValidatableResponse createOrderResponse = orderClient.create(orderData, accessToken);
        statusCode = createOrderResponse.extract().statusCode();
        isSuccess = createOrderResponse.extract().path("success");
        String id = createOrderResponse.extract().path("order.ingredients[0]._id");
        assertEquals("Неверный status code", 200, statusCode);
        assertEquals("Не совпадают id", ingredientDataList.get(0).getId(), id);
        assertTrue("Заказ не создан", isSuccess);
    }
    private List<IngredientData> getIngredientsDataList() {
        IngredientsClient ingredientsClient = new IngredientsClient();
        Ingredients ingredients = ingredientsClient.get();
        assertTrue("Ингредиенты не получены", ingredients.isSuccess());
        assertFalse("Список ингредиентов пуст", ingredients.getData().isEmpty());

        return ingredients.getData();
    }
}
