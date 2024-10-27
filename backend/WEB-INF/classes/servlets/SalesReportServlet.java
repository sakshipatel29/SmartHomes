package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import utilities.MySQLDataStoreUtilities;

@WebServlet("/salesReport")
public class SalesReportServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();
        
        try {
            if (action != null) {
                switch (action) {
                    case "productSales":
                        out.println(dbUtil.getProductSalesReport().toString());
                        break;
                    case "dailySales":
                        out.println(dbUtil.getDailySalesReport().toString());
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