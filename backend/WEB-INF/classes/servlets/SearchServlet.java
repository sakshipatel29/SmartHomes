package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import utilities.MySQLDataStoreUtilities;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {
    private MySQLDataStoreUtilities dbUtil;

    @Override
    public void init() throws ServletException {
        super.init();
        dbUtil = new MySQLDataStoreUtilities();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchTerm = request.getParameter("term");
        JSONArray results = dbUtil.searchProducts(searchTerm);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        out.print(results.toString());
        out.flush();
    }
}

