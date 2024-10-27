
package servlets;

import utilities.MySQLDataStoreUtilities;
import utilities.MongoDBDataStoreUtilities;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.bson.Document;
import java.util.List;

@WebServlet("/trending")
public class TrendingServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            MySQLDataStoreUtilities mysqlDbUtil = new MySQLDataStoreUtilities();
            
            JSONObject trendingData = new JSONObject();

            List<Document> topRatedProducts = MongoDBDataStoreUtilities.getTopFiveRatedProducts();
            JSONArray topRatedProductsJson = new JSONArray();
            for (Document doc : topRatedProducts) {
                JSONObject product = new JSONObject();
                product.put("productName", doc.getString("_id"));
                product.put("manufacturerName", doc.getString("manufacturerName"));
                product.put("productCategory", doc.getString("productCategory"));
                product.put("productPrice", doc.getDouble("productPrice"));
                product.put("averageRating", doc.getDouble("averageRating"));
                topRatedProductsJson.put(product);
            }
            trendingData.put("topRatedProducts", topRatedProductsJson);

            trendingData.put("topZipCodes", mysqlDbUtil.getTopFiveZipCodes());
            trendingData.put("topSoldProducts", mysqlDbUtil.getTopFiveSoldProducts());

            out.write(trendingData.toString());

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write(new JSONObject().put("error", "Error retrieving trending data: " + e.getMessage()).toString());
        }
    }
}