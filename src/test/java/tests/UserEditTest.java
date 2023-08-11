package tests;

import api.Steps;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;

public class UserEditTest {
    private String randomEmail = String.format("%s@yandex.ru", RandomStringUtils.random(15, true, true));
    private String name = "Anonymous";
    private String password = "password";
    private String bearerToken;
    private Response response;

    @Before
    public void init() {
        response = Steps.userCreate(randomEmail, password, name);
        response.then().statusCode(SC_OK);
        bearerToken = response.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Редактирование имени пользователя")
    @Description("Ожидается статус 200 и вывод нового имени в ответ")
    public void editNameWithAuthorizationTest() {
        String newName = String.format("%s_%s", name, RandomStringUtils.random(5, true, true));
        response = Steps.userEditName(bearerToken, newName);
        response.then().statusCode(SC_OK);
        response.then().assertThat().body("success", equalTo(true));
        response.then().assertThat().body("user.name", equalTo(newName));
    }

    @Test
    @DisplayName("Редактирование почты пользователя")
    @Description("Ожидается статус 200 и вывод новой почты в ответ")
    public void editEmailWithAuthorizationTest() {
        String newEmail = String.format("%s@yandex.ru", RandomStringUtils.random(20, true, true));
        response = Steps.userEditEmail(bearerToken, newEmail);
        response.then().statusCode(SC_OK);
        response.then().assertThat().body("success", equalTo(true));
        response.then().assertThat().body("user.email", equalTo(newEmail.toLowerCase()));
        // обновление параметра randomEmail для использования в аннотации @After
        randomEmail = newEmail;
    }

    @Test
    @DisplayName("Редактирование пароля пользователя")
    @Description("Ожидается статус 200")
    public void editPasswordWithAuthorizationTest() {
        String newPassword = String.format("%s_%s", password, RandomStringUtils.random(5, true, true));
        response = Steps.userEditPassword(bearerToken, newPassword);
        response.then().statusCode(SC_OK);
        response.then().assertThat().body("success", equalTo(true));
        // обновление параметра password для использования в аннотации @After
        password = newPassword;
    }

    @Test
    @DisplayName("Редактирование пользователя без авторизации")
    @Description("Ожидается статус 401")
    public void editUserWithoutAuthorizationTest() {
        String newName = String.format("%s_%s", name, RandomStringUtils.random(5, true, true));
        response = Steps.userEditName("", newName);
        response.then().statusCode(SC_UNAUTHORIZED);
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo("You should be authorised"));

        String newEmail = String.format("%s@yandex.ru", RandomStringUtils.random(20, true, true));
        response = Steps.userEditEmail("", newEmail);
        response.then().statusCode(SC_UNAUTHORIZED);
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo("You should be authorised"));

        String newPassword = String.format("%s_%s", password, RandomStringUtils.random(5, true, true));
        response = Steps.userEditPassword("", newPassword);
        response.then().statusCode(SC_UNAUTHORIZED);
        response.then().assertThat().body("success", equalTo(false));
        response.then().assertThat().body("message", equalTo("You should be authorised"));
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
