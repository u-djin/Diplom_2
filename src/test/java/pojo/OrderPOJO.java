package pojo;

import java.util.List;

public class OrderPOJO {
    private List<String> ingredients;

    public OrderPOJO(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public OrderPOJO() {}

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
