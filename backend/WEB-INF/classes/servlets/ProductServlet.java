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

@WebServlet("/products")
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();
            String category = req.getParameter("category");
            JSONArray products;

            if (category != null) {
                products = dbUtil.getProductsByCategory(category);
            } else {
                products = dbUtil.getAllProducts();
            }

            out.write(products.toString());

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(new JSONObject().put("error", "Error retrieving products: " + e.getMessage()).toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            JSONObject requestBodyJson = getRequestBodyAsJson(req);
            MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();

            String action = requestBodyJson.optString("action", "");

            if ("like".equals(action)) {
                handleLikeRequest(req, resp, requestBodyJson, dbUtil);
            } else {
                boolean success = dbUtil.addProduct(requestBodyJson);

                if (success) {
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    out.write(new JSONObject().put("message", "Product added successfully").toString());
                } else {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write(new JSONObject().put("error", "Failed to add product").toString());
                }
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(new JSONObject().put("error", "Error processing request: " + e.getMessage()).toString());
        }
    }

    private void handleLikeRequest(HttpServletRequest req, HttpServletResponse resp, JSONObject requestBodyJson, MySQLDataStoreUtilities dbUtil) throws IOException {
        PrintWriter out = resp.getWriter();

        try {
            int productId = requestBodyJson.getInt("productId");
            String userEmail = requestBodyJson.getString("userEmail");
            
            boolean success = dbUtil.incrementLikeCount(productId, userEmail);

            if (success) {
                int newLikeCount = dbUtil.getLikeCount(productId);
                resp.setStatus(HttpServletResponse.SC_OK);
                out.write(new JSONObject()
                    .put("message", "Like count updated successfully")
                    .put("likeCount", newLikeCount)
                    .toString());
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(new JSONObject().put("error", "User has already liked this product").toString());
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(new JSONObject().put("error", "Error updating like count: " + e.getMessage()).toString());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            JSONObject requestBodyJson = getRequestBodyAsJson(req);
            int productId = Integer.parseInt(req.getParameter("id"));
            MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();

            boolean success = dbUtil.updateProduct(productId, requestBodyJson);

            if (success) {
                resp.setStatus(HttpServletResponse.SC_OK);
                out.write(new JSONObject().put("message", "Product updated successfully").toString());
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(new JSONObject().put("error", "Product not found").toString());
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(new JSONObject().put("error", "Error updating product: " + e.getMessage()).toString());
        }
    }
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            int productId = Integer.parseInt(req.getParameter("id"));
            MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();

            boolean success = dbUtil.deleteProduct(productId);

            if (success) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write(new JSONObject().put("error", "Product not found").toString());
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(new JSONObject().put("error", "Error deleting product: " + e.getMessage()).toString());
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
}