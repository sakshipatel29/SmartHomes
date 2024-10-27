package utilities;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONArray;

public class ProductManager {
    private static Map<Integer, JSONObject> productMap = new HashMap<>();
    private static MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();

    public static void loadProducts() {
        JSONArray products = dbUtil.getAllProducts();
        for (int i = 0; i < products.length(); i++) {
            JSONObject product = products.getJSONObject(i);
            productMap.put(product.getInt("id"), product);
        }
    }

    public static void addProduct(JSONObject product) {
        int productId = product.getInt("id");
        productMap.put(productId, product);
        dbUtil.addProduct(product);
    }

    public static void updateProduct(JSONObject product) {
        int productId = product.getInt("id");
        productMap.put(productId, product);
        dbUtil.updateProduct(productId, product);
    }

    public static void deleteProduct(int productId) {
        productMap.remove(productId);
        dbUtil.deleteProduct(productId);
    }

    public static JSONObject getProduct(int productId) {
        return productMap.get(productId);
    }

    public static JSONArray getAllProducts() {
        JSONArray products = new JSONArray();
        for (JSONObject product : productMap.values()) {
            products.put(product);
        }
        return products;
    }
}