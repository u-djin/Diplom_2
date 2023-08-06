package api;

import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import constants.APIConstants;
import pojo.UserPOJO;

public class User {
    public Response userCreate(String email, String password, String name) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new UserPOJO(email, password, name))
                .post(APIConstants.BASE_URL + APIConstants.USER_CREATE);
        return response;
    }

    public Response userLogin(String email, String password) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new UserPOJO(email, password))
                .post(APIConstants.BASE_URL + APIConstants.USER_LOGIN);
        return response;
    }

    // нельзя создать конструкторы для редактирования только имени или пароля из-за одинаковой сигнатуры
    // поэтому в тело передаётся просто строка в обоих случаях
    public Response userEditEmail(String bearerToken, String newEmail) {
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", bearerToken)
                .body(String.format("{\"email\": \"%s\"}", newEmail))
                .patch(APIConstants.BASE_URL + APIConstants.USER_EDIT);
        return response;
    }
    public Response userEditName(String bearerToken, String newName) {
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", bearerToken)
                .body(String.format("{\"name\": \"%s\"}", newName))
                .patch(APIConstants.BASE_URL + APIConstants.USER_EDIT);
        return response;
    }

    public Response userEditPassword(String bearerToken, String newPassword) {
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", bearerToken)
                .body(String.format("{\"password\": \"%s\"}", newPassword))
                .patch(APIConstants.BASE_URL + APIConstants.USER_EDIT);
        return response;
    }

    public Response userDelete(String bearerToken) {
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", bearerToken)
                .delete(APIConstants.BASE_URL + APIConstants.USER_DELETE);
        return response;
    }
}
