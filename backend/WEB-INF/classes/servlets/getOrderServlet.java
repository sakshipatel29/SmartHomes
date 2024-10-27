package servlets;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;
import utilities.MySQLDataStoreUtilities;

@WebServlet("/get")
public class getOrderServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();
            JSONArray orders = dbUtil.getAllOrders();  // This will now include 'order_status' for each order.
            out.print(orders.toString());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(new JSONObject().put("error", "Error retrieving orders: " + e.getMessage()).toString());
        }
    }
}
