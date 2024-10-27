package servlets;

import utilities.MySQLDataStoreUtilities;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            String email = request.getParameter("email");
            MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();
            JSONArray userCart = dbUtil.getUserCart(email);
            out.write(userCart.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(new JSONObject().put("error", "Could not load cart: " + e.getMessage()).toString());
        }
    }

    
    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json");
    PrintWriter out = response.getWriter();

    try {
        JSONObject requestBodyJson = getRequestBodyAsJson(request);
        System.out.println("Received payload: " + requestBodyJson.toString());

        String email = requestBodyJson.getString("email");
        String action = requestBodyJson.getString("action");
        MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();

        JSONArray updatedCart = null;
        String errorMessage = null;

        if ("add".equalsIgnoreCase(action)) {
            JSONObject newCartItem = createCartItemFromRequest(requestBodyJson);
            System.out.println("Adding new item to cart: " + newCartItem.toString());
            boolean success = dbUtil.addToCart(email, newCartItem);
            if (success) {
                updatedCart = dbUtil.getUserCart(email);
            } else {
                errorMessage = "Failed to add item to cart";
            }
        } else if ("remove".equalsIgnoreCase(action)) {
            int productId = requestBodyJson.getInt("productId");
            boolean isAccessory = requestBodyJson.optBoolean("isAccessory", false);
            System.out.println("Removing item from cart: productId=" + productId + ", isAccessory=" + isAccessory);
            boolean success = dbUtil.removeFromCart(email, productId, isAccessory);
            if (success) {
                updatedCart = dbUtil.getUserCart(email);
            } else {
                errorMessage = "Failed to remove item from cart";
            }
        } else if ("update".equalsIgnoreCase(action)) {
            int productId = requestBodyJson.getInt("productId");
            int quantity = requestBodyJson.getInt("quantity");
            System.out.println("Updating cart item quantity: productId=" + productId + ", quantity=" + quantity);
            boolean success = dbUtil.updateCartItemQuantity(email, productId, quantity);
            if (success) {
                updatedCart = dbUtil.getUserCart(email);
            } else {
                errorMessage = "Failed to update cart item quantity";
            }
        } else {
            errorMessage = "Invalid action";
        }

        if (updatedCart != null) {
            out.write(updatedCart.toString());
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(new JSONObject().put("error", errorMessage).toString());
        }
    } catch (Exception e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        System.out.println("Error processing cart operation: " + e.getMessage());
        e.printStackTrace();
        out.write(new JSONObject().put("error", "Failed to process request: " + e.getMessage()).toString());
    }
}

    private JSONObject getRequestBodyAsJson(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        return new JSONObject(requestBody.toString());
    }

    private JSONObject createCartItemFromRequest(JSONObject requestBodyJson) throws Exception {
        JSONObject cartItem = new JSONObject();

        cartItem.put("productId", requestBodyJson.getInt("productId"));
        cartItem.put("productName", requestBodyJson.getString("productName"));
        cartItem.put("productPrice", requestBodyJson.getDouble("productPrice"));
        cartItem.put("quantity", requestBodyJson.getInt("quantity"));
        cartItem.put("isAccessory", requestBodyJson.optBoolean("isAccessory", false));

        if (cartItem.getBoolean("isAccessory")) {
            if (requestBodyJson.has("parentProductId")) {
                cartItem.put("parentProductId", requestBodyJson.getInt("parentProductId"));
                System.out.println("Accessory item: parentProductId=" + cartItem.getInt("parentProductId")); 
            } else {
                throw new Exception("Missing parentProductId for accessory");
            }
        } else {
            cartItem.put("warrantyPrice", requestBodyJson.optDouble("warrantyPrice", 0.0));
            System.out.println("Non-accessory item with warranty: " + cartItem.toString());
        }

        return cartItem;
    }
}






