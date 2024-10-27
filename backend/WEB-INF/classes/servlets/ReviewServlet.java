package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import utilities.MongoDBDataStoreUtilities;

@WebServlet("/review")
public class ReviewServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        BufferedReader reader = request.getReader();
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        String requestBody = sb.toString();

        try {
            JSONObject jsonObject = new JSONObject(requestBody);
            Map<String, Object> review = new HashMap<>();

            review.put("productModelName", jsonObject.getString("productModelName"));
            review.put("productCategory", jsonObject.getString("productCategory"));
            review.put("productPrice", jsonObject.getDouble("productPrice"));
            review.put("storeId", jsonObject.getString("storeId"));
            review.put("storeZip", jsonObject.getString("storeZip"));
            review.put("storeCity", jsonObject.getString("storeCity"));
            review.put("storeState", jsonObject.getString("storeState"));
            review.put("productOnSale", jsonObject.getString("productOnSale"));
            review.put("manufacturerName", jsonObject.getString("manufacturerName"));
            review.put("manufacturerRebate", jsonObject.getString("manufacturerRebate"));
            review.put("userId", jsonObject.getString("userId"));
            review.put("userAge", jsonObject.getInt("userAge"));
            review.put("userGender", jsonObject.getString("userGender"));
            review.put("userOccupation", jsonObject.getString("userOccupation"));
            review.put("reviewRating", jsonObject.getInt("reviewRating"));
            review.put("reviewDate", jsonObject.getString("reviewDate"));
            review.put("reviewText", jsonObject.getString("reviewText"));

            MongoDBDataStoreUtilities.insertReview(review);

            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"message\": \"Review submitted successfully\"}");
            out.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("{\"error\": \"Invalid JSON format\"}");
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        MongoDBDataStoreUtilities.closeConnection();
    }
}