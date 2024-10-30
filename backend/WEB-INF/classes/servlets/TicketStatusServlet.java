import java.io.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.json.JSONObject;

@WebServlet("/ticketstatus")
public class TicketStatusServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smarthomes";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Sakshi@2906";

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String ticketNumber = request.getParameter("ticketNumber");

        if (ticketNumber == null || ticketNumber.isEmpty()) {
            sendErrorResponse(response, "Please provide a valid ticket number.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM tickets WHERE ticketNumber = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, ticketNumber);
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    JSONObject ticketDetails = new JSONObject();
                    ticketDetails.put("ticketNumber", rs.getString("ticketNumber"));
                    ticketDetails.put("orderId", rs.getString("orderId"));
                    ticketDetails.put("description", rs.getString("description"));
                    ticketDetails.put("response", rs.getString("response"));
                    ticketDetails.put("date", rs.getDate("date").toString());

                    response.getWriter().write(ticketDetails.toString());
                } else {
                    sendErrorResponse(response, "No ticket found with the provided number.");
                }
            }
        } catch (SQLException e) {
            sendErrorResponse(response, "Database error: " + e.getMessage());
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("error", message);
        response.getWriter().write(errorResponse.toString());
    }
}