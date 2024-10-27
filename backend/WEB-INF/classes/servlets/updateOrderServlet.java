package servlets;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import org.json.JSONObject;
import utilities.MySQLDataStoreUtilities;

@WebServlet("/update")
public class updateOrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            // Reading and parsing JSON data from the request body
            String newOrderData = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            JSONObject newOrderJson = new JSONObject(newOrderData);

            // Retrieving orderId and checking for orderStatus
            String orderId = newOrderJson.getString("orderId");

            // Updating the order in the database
            MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();
            boolean orderUpdated = dbUtil.updateOrder(orderId, newOrderJson);

            // Responding to client based on the result of the update operation
            if (orderUpdated) {
                response.setStatus(HttpServletResponse.SC_OK);
                out.print(new JSONObject().put("status", "Order updated successfully").toString());
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(new JSONObject().put("error", "Order not found").toString());
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(new JSONObject().put("error", "Error updating order: " + e.getMessage()).toString());
        }
    }
}
