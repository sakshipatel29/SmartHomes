
package servlets;

import utilities.MySQLDataStoreUtilities;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Date;
import java.text.SimpleDateFormat;

@WebServlet("/order")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            JSONObject requestBodyJson = getRequestBodyAsJson(request);
            MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();

            String userId = requestBodyJson.getString("user_id");
            String customerName = requestBodyJson.getString("customerName");
            String customerAddress = requestBodyJson.getString("customerAddress");
            String creditCardNumber = requestBodyJson.getString("creditCardNumber");
            String orderId = generateOrderId();
            String purchaseDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String shipDate = calculateShipDate(purchaseDate);
            JSONArray orderItems = requestBodyJson.getJSONArray("items");
            double shippingCost = requestBodyJson.optDouble("shippingCost", 0.0);
            double totalDiscount = 0.0;
            double totalSales = 0.0;
            int storeId = requestBodyJson.optInt("storeId", 0);
            String orderStatus = "pending"; // Initial order status

            for (int i = 0; i < orderItems.length(); i++) {
                JSONObject item = orderItems.getJSONObject(i);
                int productId = item.getInt("productId");
                String category = item.getString("category");
                int quantity = item.getInt("quantity");
                double price = item.getDouble("price");
                double discount = item.optDouble("discount", 0.0);

                totalDiscount += discount * quantity;
                totalSales += (price - discount) * quantity;

                dbUtil.addOrderItem(orderId, userId, customerName, customerAddress, creditCardNumber,
                        purchaseDate, shipDate, productId, category, quantity, price, shippingCost,
                        discount, totalSales, storeId, orderStatus); // Added orderStatus to each order item
            }

            JSONObject storeAddress = null;
            if (storeId > 0) {
                storeAddress = dbUtil.getStoreAddress(storeId);
            }

            JSONObject orderResponse = new JSONObject();
            orderResponse.put("orderId", orderId);
            orderResponse.put("totalSales", totalSales);
            orderResponse.put("totalDiscount", totalDiscount);
            orderResponse.put("orderStatus", orderStatus); // Adding order status to the response
            if (storeAddress != null) {
                orderResponse.put("storeAddress", storeAddress);
            }

            out.write(orderResponse.toString());

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(new JSONObject().put("error", "Failed to process order: " + e.getMessage()).toString());
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

    private String generateOrderId() {
        return "ORD" + System.currentTimeMillis();
    }

    private String calculateShipDate(String purchaseDate) {
        return purchaseDate;
    }
}
