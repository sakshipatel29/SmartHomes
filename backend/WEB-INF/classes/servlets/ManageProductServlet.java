package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import utilities.ProductManager;

@WebServlet("/manageProduct")
public class ManageProductServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        JSONObject product = new JSONObject(request.getParameter("product"));

        switch (action) {
            case "add":
                ProductManager.addProduct(product);
                break;
            case "update":
                ProductManager.updateProduct(product);
                break;
            case "delete":
                ProductManager.deleteProduct(product.getInt("id"));
                break;
        }

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        out.print("{\"success\": true}");
        out.flush();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        switch (action) {
            case "getAll":
                out.print(ProductManager.getAllProducts().toString());
                break;
            case "getOne":
                int productId = Integer.parseInt(request.getParameter("id"));
                out.print(ProductManager.getProduct(productId).toString());
                break;
        }
        out.flush();
    }
}