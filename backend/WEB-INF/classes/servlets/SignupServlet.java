package servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import org.json.JSONObject;
import utilities.MySQLDataStoreUtilities;

@WebServlet("/signup")
public class SignupServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            JSONObject requestBodyJson = getRequestBodyAsJson(request);

            String email = requestBodyJson.getString("email");
            String password = requestBodyJson.getString("password");
            String type = requestBodyJson.getString("type");
            String name = requestBodyJson.optString("name", null);
            String street = requestBodyJson.optString("street", null);
            String city = requestBodyJson.optString("city", null);
            String state = requestBodyJson.optString("state", null);
            String zipcode = requestBodyJson.optString("zipcode", null);

            MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();
            JSONObject result = dbUtil.registerUser(email, password, type, name, street, city, state, zipcode);

            if (result.getBoolean("success")) {
                out.write(new JSONObject().put("message", "Signup successful").toString());
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(result.toString());
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(new JSONObject().put("error", "Invalid request: " + e.getMessage()).toString());
        }
    }

    private JSONObject getRequestBodyAsJson(HttpServletRequest request) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        return new JSONObject(requestBody.toString());
    }
}