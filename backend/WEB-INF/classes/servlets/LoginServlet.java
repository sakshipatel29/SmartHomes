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
import java.sql.SQLException;
import utilities.*;
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            JSONObject requestBodyJson = getRequestBodyAsJson(request);

            String email = requestBodyJson.getString("email");
            String password = requestBodyJson.getString("password");
            String type = requestBodyJson.getString("type");

            MySQLDataStoreUtilities dbUtil = new MySQLDataStoreUtilities();
            JSONObject result = dbUtil.validateUser(email, password, type);

            if (result.getBoolean("success")) {
                out.write(new JSONObject().put("message", "Login successful").toString());
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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