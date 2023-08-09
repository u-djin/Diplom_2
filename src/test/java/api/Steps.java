package api;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;

public class Steps {
    static User user = new User();
    static Order order = new Order();

    @Step("Создание нового пользователя")
    public static Response userCreate(String email, String password, String name) {
        return user.userCreate(email, password, name);
    }

    @Step("Логин пользователя")
    public static Response userLogin(String email, String password) {
        return user.userLogin(email, password);
    }

    @Step("Удаление пользователя")
    public static Response userDelete(String bearerToken) {
        return user.userDelete(bearerToken);
    }

    @Step("Редактирование почты пользователя")
    public static Response userEditEmail(String bearerToken, String newEmail) {
        return user.userEditEmail(bearerToken, newEmail);
    }

    @Step("Редактирование почты пользователя")
    public static Response userEditName(String bearerToken, String newName) {
        return user.userEditName(bearerToken, newName);
    }

    @Step("Редактирование имени пользователя")
    public static Response userEditPassword(String bearerToken, String newPassword) {
        return user.userEditPassword(bearerToken, newPassword);
    }

    @Step("Получение данных об ингредиентах")
    public static Response getIngredients() {
        return order.getIngredients();
    }

    @Step("Создание заказа с авторизайией")
    public static Response orderCreateAuthorized(String bearerToken, List<String> ingredients) {
        return order.orderCreateAuthorized(bearerToken, ingredients);
    }

    @Step("Создание заказа без авторизации")
    public static Response orderCreateNonAuthorized(List<String> ingredients) {
        return order.orderCreateNonAuthorized(ingredients);
    }

    @Step("Получить заказы пользователя с авторизайией")
    public static Response ordersGetAuthorized(String bearerToken) {
        return order.ordersGetAuthorized(bearerToken);
    }

    @Step("Получить заказы пользователя без авторизации")
    public static Response ordersGetNonAuthorized() {
        return order.ordersGetNonAuthorized();
    }
}
