package api;

import groovyjarjarantlr4.runtime.tree.RewriteCardinalityException;
import io.qameta.allure.Step;
import io.restassured.response.Response;

public class Steps {
    static User user = new User();

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
}
