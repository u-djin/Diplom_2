import api.Steps;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.After;
import org.junit.Test;

public class UserCreateTest {
    private String randomEmail = String.format("%s@yandex.ru", RandomStringUtils.random(15, true, true));
    private String name = "Anonymous";
    private String password = "password";

    @Test
    @DisplayName("Корректное создание пользователя")
    @Description("Ожидается статус 200 и json с данными пользователя и токенами")
    public void createCorrectUserTest() {
        Response response = Steps.userCreate(randomEmail, password, name);
        response.then().statusCode(SC_OK);
        response.then().assertThat().body("success", equalTo(true));
        response.then().assertThat().body("user.email", equalTo(randomEmail.toLowerCase()));
        response.then().assertThat().body("user.name", equalTo(name));
        response.then().assertThat().body("accessToken", notNullValue());
        response.then().assertThat().body("refreshToken", notNullValue());
    }

    @Test
    @DisplayName("Создание уже зарегестрированного пользователя")
    @Description("Ожидается статус 403 и соответствующее сообщение")
    public void createDuplicateUserTest() {
        Response response = Steps.userCreate(randomEmail, password, name);
        response.then().statusCode(SC_OK);
        response = Steps.userCreate(randomEmail, password, name);
        response.then().statusCode(SC_FORBIDDEN);
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без полей")
    @Description("Ожидается статус 403 и соответствующее сообщение")
    public void createEmptyFieldsUserTest() {
        // так как поля имеют одинаковый функционал, тесты объеденены в один последовательный
        // с большой вероятностью ошибка в одном из полей проявится и в двух других
        // поэтому не обязательно проверять все поля по отдельности
        Response response = Steps.userCreate("", password, name);
        response.then().statusCode(SC_FORBIDDEN);
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"));

        response = Steps.userCreate(randomEmail, "", name);
        response.then().statusCode(SC_FORBIDDEN);
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"));

        response = Steps.userCreate(randomEmail, password, "");
        response.then().statusCode(SC_FORBIDDEN);
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void deleteAfter() {
        Response response = Steps.userLogin(randomEmail, password);
        boolean success = response.then().extract().path("success");
        if (success) {
            String bearerToken = response.then().extract().path("accessToken");
            response = Steps.userDelete(bearerToken);
            response.then().statusCode(SC_ACCEPTED);
        }
    }
}
