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
            String newOrderData = request.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
            JSONObject newOrderJson = new JSONObject(newOrderData);

            String orderId = newOrderJson.getString("orderId");

            MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();
            boolean orderUpdated = dbUtil.updateOrder(orderId, newOrderJson);

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
