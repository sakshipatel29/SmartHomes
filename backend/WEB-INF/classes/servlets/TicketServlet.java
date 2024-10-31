package servlets;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Base64;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;
import org.json.JSONArray;

@WebServlet("/ticket")
@MultipartConfig
public class TicketServlet extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/smarthomes";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Sakshi@2906";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String OPENAI_API_KEY = "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"; // Replace with your actual API key

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String orderId = request.getParameter("orderId");
        String description = request.getParameter("description");
        Part imagePart = request.getPart("image");
        String imageBase64 = encodeImageToBase64(imagePart);

        if (orderId == null || orderId.isEmpty()) {
            response.getWriter().write("Please provide a valid order ID.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkOrderQuery = "SELECT order_status FROM orders WHERE order_id = ?";
            try (PreparedStatement checkOrderStmt = conn.prepareStatement(checkOrderQuery)) {
                checkOrderStmt.setString(1, orderId);
                ResultSet rs = checkOrderStmt.executeQuery();

                if (!rs.next()) {
                    response.getWriter().write("Please provide a valid order ID.");
                    return;
                } else if (!"delivered".equalsIgnoreCase(rs.getString("order_status"))) {
                    response.getWriter().write("Order is not yet delivered.");
                    return;
                }
            }

            // Call OpenAI API to get response
            String openAiResponse = getOpenAiResponse(description, imageBase64);
            String ticketNumber = generateTicketNumber();
            String insertTicketQuery = "INSERT INTO tickets (ticketNumber, orderId, description, image, response, date) VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement insertStmt = conn.prepareStatement(insertTicketQuery)) {
                insertStmt.setString(1, ticketNumber);
                insertStmt.setString(2, orderId);
                insertStmt.setString(3, description);
                insertStmt.setString(4, imageBase64);
                insertStmt.setString(5, openAiResponse);
                insertStmt.setDate(6, new Date(System.currentTimeMillis()));
                insertStmt.executeUpdate();
            }

            response.getWriter().write("Ticket created successfully with Ticket Number: " + ticketNumber);
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().write("Database error: " + e.getMessage());
        }
    }

    private String generateTicketNumber() {
        return "TICKET-" + System.currentTimeMillis();
    }

    private String encodeImageToBase64(Part imagePart) throws IOException {
        try (InputStream imageStream = imagePart.getInputStream()) {
            byte[] imageBytes = imageStream.readAllBytes();
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    private String getOpenAiResponse(String description, String imageBase64) throws IOException {
        final String instructionPrompt = "You are a customer service assistant for a delivery service, equipped to analyze images of packages. If a package appears damaged in the image, automatically process a refund according to policy. If the package looks wet, initiate a replacement. If the package appears normal and not damaged, escalate to agent. For any other issues or unclear images, escalate to agent. You must always use tools!";

        JSONObject jsonBody = new JSONObject();
        jsonBody.put("model", "gpt-4o-mini");
        jsonBody.put("max_tokens", 300);

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "system").put("content", instructionPrompt));
        
        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        
        JSONArray userContent = new JSONArray();
        userContent.put(new JSONObject().put("type", "text").put("text", description));
        userContent.put(new JSONObject()
            .put("type", "image_url")
            .put("image_url", new JSONObject()
                .put("url", "data:image/jpeg;base64," + imageBase64)));
        
        userMessage.put("content", userContent);
        messages.put(userMessage);

        jsonBody.put("messages", messages);

        System.out.println("Sending request to OpenAI API:");
        System.out.println("Request Payload: " + jsonBody.toString());

        URL url = new URL(OPENAI_API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        System.out.println("Response Code: " + responseCode);
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                String apiResponse = response.toString();
                System.out.println("OpenAI API Response: " + apiResponse);
                return parseOpenAiResponse(apiResponse);
            }
        } else {
            StringBuilder errorResponse = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    errorResponse.append(responseLine.trim());
                }
            }
            throw new IOException("Error: " + connection.getResponseMessage() + " - " + errorResponse.toString());
        }
    }

    private String parseOpenAiResponse(String responseBody) {
    JSONObject jsonResponse = new JSONObject(responseBody);
    JSONArray choices = jsonResponse.getJSONArray("choices");
    if (!choices.isEmpty()) {
        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject message = firstChoice.getJSONObject("message");
        return message.getString("content").trim();
    }
    return "Unable to analyze the image. Please contact customer support.";
}
}
