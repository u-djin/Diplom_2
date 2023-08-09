package pojo;

import java.util.List;

public class GetIngredientsPOJO {
    private boolean success;
    private List<DataPOJO> data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<DataPOJO> getData() {
        return data;
    }

    public void setData(List<DataPOJO> data) {
        this.data = data;
    }
}
