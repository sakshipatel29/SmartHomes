package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.MySQLDataStoreUtilities;

@WebServlet("/inventory")
public class InventoryServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        try {
            MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();
            if (action != null) {
                switch (action) {
                    case "allProducts":
                        out.println(dbUtil.getAllProducts().toString());
                        break;
                    case "onSale":
                        out.println(dbUtil.fetchProducts("SELECT name, price, available_quantity FROM products WHERE special_discount > 0"));
                        break;
                    case "rebates":
                        out.println(dbUtil.fetchProducts("SELECT name, price, available_quantity FROM products WHERE manufacturer_rebate > 0"));
                        break;
                    default:
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.println("{\"error\": \"Invalid action\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.println("{\"error\": \"Action parameter is required\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}