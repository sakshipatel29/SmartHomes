
package servlets;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import org.json.JSONObject;
import org.json.JSONArray;
import utilities.MySQLDataStoreUtilities;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/service")
public class CustomerServiceServlet extends HttpServlet {
    
    private static final String OPENAI_API_KEY = "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"; // Replace with your actual OpenAI API key

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        MySQLDataStoreUtilities dbUtilities = new MySQLDataStoreUtilities(); 

        if ("openTicket".equals(action)) {
            openTicket(request, response, dbUtilities);
        } else if ("checkTicketStatus".equals(action)) {
            checkTicketStatus(request, response, dbUtilities);
        }
    }
    
    private void openTicket(HttpServletRequest request, HttpServletResponse response, MySQLDataStoreUtilities dbUtilities) throws ServletException, IOException {
        String orderId = request.getParameter("orderId");
        String description = request.getParameter("description");
        String imageBase64 = request.getParameter("image");

        if (!dbUtilities.isOrderDelivered(orderId)) { 
            sendJsonResponse(response, "error", "Please provide a valid Order ID or ensure the order is delivered.");
            return;
        }

        String decision = processImageWithOpenAI(imageBase64);
        

        String ticketNumber = generateTicketNumber();

        dbUtilities.insertTicket(ticketNumber, orderId, decision, description); 
        
        sendJsonResponse(response, "success", ticketNumber);
    }
    
    private void checkTicketStatus(HttpServletRequest request, HttpServletResponse response, MySQLDataStoreUtilities dbUtilities) throws ServletException, IOException {
        String ticketNumber = request.getParameter("ticketNumber");
        
        String decision = dbUtilities.getTicketDecision(ticketNumber); 
        
        if (decision != null) {
            sendJsonResponse(response, "success", decision);
        } else {
            sendJsonResponse(response, "error", "Ticket not found.");
        }
    }
    
    private String processImageWithOpenAI(String imageBase64) {
        try {
            URL url = new URL("https://api.openai.com/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
            conn.setDoOutput(true);

            JSONObject payload = new JSONObject();
            payload.put("model", "gpt-4o-mini"); 
            payload.put("messages", new JSONArray()
                .put(new JSONObject().put("role", "user").put("content", getInstructionPrompt()))
                .put(new JSONObject().put("role", "user").put("content", new JSONArray()
                    .put(new JSONObject()
                        .put("type", "image_url")
                        .put("image_url", new JSONObject().put("url", "data:image/jpeg;base64," + imageBase64))
                    )
                ))
            );

            JSONArray functions = new JSONArray();
            functions.put(new JSONObject()
                .put("name", "refund_order")
                .put("description", "Refund an order")
                .put("parameters", new JSONObject()
                    .put("type", "object")
                    .put("properties", new JSONObject()
                        .put("rationale", new JSONObject().put("type", "string"))
                        .put("image_description", new JSONObject().put("type", "string"))
                    )
                    .put("required", new JSONArray().put("rationale").put("image_description"))
                ));
            
            functions.put(new JSONObject()
                .put("name", "replace_order")
                .put("description", "Replace an order")
                .put("parameters", new JSONObject()
                    .put("type", "object")
                    .put("properties", new JSONObject()
                        .put("rationale", new JSONObject().put("type", "string"))
                        .put("image_description", new JSONObject().put("type", "string"))
                    )
                    .put("required", new JSONArray().put("rationale").put("image_description"))
                ));
            
            functions.put(new JSONObject()
                .put("name", "escalate_to_agent")
                .put("description", "Escalate to an agent")
                .put("parameters", new JSONObject()
                    .put("type", "object")
                    .put("properties", new JSONObject()
                        .put("rationale", new JSONObject().put("type", "string"))
                        .put("image_description", new JSONObject().put("type", "string"))
                        .put("message", new JSONObject().put("type", "string"))
                    )
                    .put("required", new JSONArray().put("rationale").put("image_description").put("message"))
                ));
            
            payload.put("functions", functions);
            payload.put("function_call", "auto");
            payload.put("temperature", 0.0);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = payload.toString().getBytes();
                os.write(input, 0, input.length);           
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            StringBuilder responseBuilder = new StringBuilder();
            String responseLine;
            
            while ((responseLine = br.readLine()) != null) {
                responseBuilder.append(responseLine.trim());
            }

            JSONObject responseJson = new JSONObject(responseBuilder.toString());
            return parseOpenAIResponse(responseJson);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getInstructionPrompt() {
        return "You are a customer service assistant for a delivery service, equipped to analyze images of packages. If a package appears damaged in the image, automatically process a refund according to policy. If the package looks wet, initiate a replacement. If the package appears normal and not damaged, escalate to agent. For any other issues or unclear images, escalate to agent.";
    }

    private String parseOpenAIResponse(JSONObject responseJson) {
        var functionCall = responseJson.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getJSONObject("function_call");
        String action = functionCall.getString("name");
        
        switch (action) {
            case "refund_order":
                return handleRefundOrder(functionCall.getJSONObject("arguments"));
                
            case "replace_order":
                return handleReplaceOrder(functionCall.getJSONObject("arguments"));
                
            case "escalate_to_agent":
                return handleEscalateToAgent(functionCall.getJSONObject("arguments"));
                
            default:
                throw new RuntimeException("Unknown action: " + action);
        }
    }

    private String handleRefundOrder(JSONObject arguments) {

        return "Refund Order processed.";
    }

    private String handleReplaceOrder(JSONObject arguments) {

        return "Replace Order processed.";
    }

    private String handleEscalateToAgent(JSONObject arguments) {

        return "Escalated to Human Agent.";
    }

    private String generateTicketNumber() {
        return "TKT" + System.currentTimeMillis();
    }
    
    private void sendJsonResponse(HttpServletResponse response, String status, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", status);
        jsonResponse.put("message", message);
        response.getWriter().write(jsonResponse.toString());
    }
}