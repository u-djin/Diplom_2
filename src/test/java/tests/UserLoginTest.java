package tests;

import api.Steps;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;


public class UserLoginTest {
    private static String randomEmail = String.format("%s@yandex.ru", RandomStringUtils.random(15, true, true));
    private static String name = "Anonymous";
    private static String password = "password";
    private static Response response;

    @BeforeClass
    public static void init() {
        response = Steps.userCreate(randomEmail, password, name);
        response.then().statusCode(SC_OK);
    }

    @Test
    @DisplayName("Корректный логин")
    @Description("Ожидается статус 200 и json с данными пользователя и токенами")
    public void correctLoginTest() {
        Response response = Steps.userLogin(randomEmail, password);
        response.then().statusCode(SC_OK);
        response.then().assertThat().body("success", equalTo(true));
        response.then().assertThat().body("user.email", equalTo(randomEmail.toLowerCase()));
        response.then().assertThat().body("user.name", equalTo(name));
        response.then().assertThat().body("accessToken", notNullValue());
        response.then().assertThat().body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Некорректный логин")
    @Description("Ожидается статус 401 и соответствующее сообщение")
    public void incorrectLoginTest() {
        // так как поля имеют одинаковый функционал, тесты объеденены в один последовательный
        // с большой вероятностью ошибка в одном из полей проявится и в двух других
        // поэтому не обязательно проверять все поля по отдельности
        Response response = Steps.userLogin("", password);
        response.then().statusCode(SC_UNAUTHORIZED);
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo("email or password are incorrect"));

        response = Steps.userLogin(randomEmail, "");
        response.then().statusCode(SC_UNAUTHORIZED);
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo("email or password are incorrect"));
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
