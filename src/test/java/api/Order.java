package api;

import constants.APIConstants;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

import pojo.DataPOJO;
import pojo.GetIngredientsPOJO;
import pojo.OrderPOJO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Order {
    public Response getIngredients() {
        Response response = given()
                .header("Content-type", "application/json")
                .get(APIConstants.BASE_URL + APIConstants.INGREDIENTS_GET);
        return response;
    }

    public Response orderCreateAuthorized(String bearerToken, List<String> ingredients) {
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", bearerToken)
                .body(new OrderPOJO(ingredients))
                .post(APIConstants.BASE_URL + APIConstants.ORDER_CREATE);
        return response;
    }

    public Response orderCreateNonAuthorized(List<String> ingredients) {
        Response response = given()
                .header("Content-type", "application/json")
                .body(new OrderPOJO(ingredients))
                .post(APIConstants.BASE_URL + APIConstants.ORDER_CREATE);
        return response;
    }

    public Response ordersGetAuthorized(String bearerToken) {
        Response response = given()
                .header("Content-type", "application/json")
                .header("Authorization", bearerToken)
                .get(APIConstants.BASE_URL + APIConstants.ORDERS_GET);
        return response;
    }

    public Response ordersGetNonAuthorized() {
        Response response = given()
                .header("Content-type", "application/json")
                .get(APIConstants.BASE_URL + APIConstants.ORDERS_GET);
        return response;
    }
}
