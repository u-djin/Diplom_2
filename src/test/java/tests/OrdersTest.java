package tests;

import api.Steps;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import pojo.DataPOJO;
import pojo.GetIngredientsPOJO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class OrdersTest {
    private static String randomEmail = String.format("%s@yandex.ru", RandomStringUtils.random(15, true, true));
    private static String name = "Anonymous";
    private static String password = "password";
    private static String bearerToken;
    private int ingredientsCount = 3;

    // вспомогательная функция для формирования данных об ингредиентах (хэш для оправки заказа и цена для проверки)
    private HashMap<String, List> prepareIngredients(int ingredientsCount) {
        HashMap<String, List> ingredients = new HashMap<>(ingredientsCount);
        ingredients.put("ingredientHash", new ArrayList<>());       // список хешей ингредиентов, используемых в заказе
        ingredients.put("ingredientPrice", new ArrayList<>());      // список цен ингредиентов, используемых в заказе
        List<DataPOJO> dataList;
        Response response = Steps.getIngredients();
        if(response.then().extract().path("success")) {
            // получили список ингредиентов из ответа на GET запрос
            dataList = response.body().as(GetIngredientsPOJO.class).getData();

            for (int i = 0; i < ingredientsCount; i++) {
                ingredients.get("ingredientHash").add(
                        dataList.get(i)                     // получили i-й игредиент из списка
                                .get_id());                 // получили хеш i-го ингредиента
                ingredients.get("ingredientPrice").add(
                        dataList.get(i)                     // получили i-й игредиент из списка
                                .getPrice());               // получили цену i-го ингредиента
            }
        }

        return ingredients;
    }

    @BeforeClass
    public static void init() {
        Response response = Steps.userCreate(randomEmail, password, name);
        response.then().statusCode(SC_OK);
        bearerToken = response.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами с авторизацией")
    @Description("Ожидается статус 200 и сообщение с подробным списком ингредиентов")
    public void createOrderWithIngredientsAuthorizedTest() {
        HashMap<String, List> ingredients = prepareIngredients(ingredientsCount);
        int price = 0;
        for (int i = 0; i < ingredientsCount; i++) {
            price += (Integer) ingredients.get("ingredientPrice").get(i);
        }

        Response response = Steps.orderCreateAuthorized(bearerToken, ingredients.get("ingredientHash"));
        // проверяются несколько характерных для авторизированного заказа полей; не все, чтобы не усложнять тест
        response.then().statusCode(SC_OK);
        response.then().assertThat().body("success", equalTo(true));
        response.then().assertThat().body("name", notNullValue());
        response.then().assertThat().body("order.owner.name", equalTo(name));
        response.then().assertThat().body("order.owner.email", equalTo(randomEmail.toLowerCase()));
        response.then().assertThat().body("order.price", equalTo(price));
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами без авторизации")
    @Description("Ожидается статус 200 и сообщение без подробностей")
    public void createOrderWithIngredientsNonAuthorizedTest() {
        HashMap<String, List> ingredients = prepareIngredients(ingredientsCount);

        Response response = Steps.orderCreateNonAuthorized(ingredients.get("ingredientHash"));
        // проверяются отсутствие характерных для авторизированного заказа полей
        response.then().statusCode(SC_OK);
        response.then().assertThat().body("success", equalTo(true));
        response.then().assertThat().body("name", notNullValue());
        response.then().assertThat().body("order.owner.name", equalTo(null));
        response.then().assertThat().body("order.owner.email", equalTo(null));
        response.then().assertThat().body("order.price", equalTo(null));
    }

    @Test
    @DisplayName("Получение заказов пользователя с авторизацией")
    @Description("Ожидается статус 200 и сообщение со списком заказов")
    public void getOrdersAuthorizedTest() {
        Response response = Steps.ordersGetAuthorized(bearerToken);
        response.then().statusCode(SC_OK);
        response.then().assertThat().body("success", equalTo(true));
        response.then().assertThat().body("orders", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации")
    @Description("Ожидается статус 401 и соответствующее сообщение")
    public void getOrdersWithNonAuthorizedTest() {
        Response response = Steps.ordersGetNonAuthorized();
        response.then().statusCode(SC_UNAUTHORIZED);
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Ожидается статус 400 и соответствующее сообщение")
    public void createOrderWithoutIngredientsTest() {
        Response response = Steps.orderCreateAuthorized(bearerToken, null);
        response.then().statusCode(SC_BAD_REQUEST);
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Ожидается статус 500")
    public void createOrderWithInvalidHashTest() {
        Response response = Steps.orderCreateAuthorized(bearerToken, Arrays.asList("!"));
        response.then().statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    // этого теста не было в задании (и такой случай не описан в документации API)
    // я решил его добавить, потому что обнаружил несоответствие с ожидаемым поведением (из ответа на запрос)
    // он проваливается, потому что заказ создаётся, если есть хотя бы один верный хэш
    // из сообщения следует, что в случае хотя бы одного ошибочного хэша заказ не должен создаваться
    // в действительности заказ не создаётся, только когда все хэши с опечаткой
    @Test
    @DisplayName("Создание заказа с опечаткой в хеше")
    @Description("Ожидается статус 400 и соответствующее сообщение ХОТЯ БЫ ПРИ ОДНОМ неверном хеше")
    public void createOrderWithMisspellHashTest() {
        List<String> ingredientsHash = prepareIngredients(ingredientsCount).get("ingredientHash");
        // назначаем 1-му элементу случайно сгенерированную строку вместо правильного хеша
        ingredientsHash.set(0, RandomStringUtils.random(24, true, true));
        Response response = Steps.orderCreateAuthorized(bearerToken, ingredientsHash);
        response.then().assertThat().statusCode(SC_BAD_REQUEST);
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo("One or more ids provided are incorrect"));
    }

    @AfterClass
    public static void deleteAfter() {
        Response response = Steps.userLogin(randomEmail, password);
        boolean success = response.then().extract().path("success");
        if (success) {
            String bearerToken = response.then().extract().path("accessToken");
            response = Steps.userDelete(bearerToken);
            response.then().statusCode(SC_ACCEPTED);
        }
    }
}
