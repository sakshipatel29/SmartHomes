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
import utilities.AjaxUtility;

@WebServlet("/autocomplete")
public class AutoCompleteServlet extends HttpServlet {
    private AjaxUtility ajaxUtility;

    @Override
    public void init() throws ServletException {
        super.init();
        ajaxUtility = new AjaxUtility();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String searchTerm = request.getParameter("term");
        List<String> results = ajaxUtility.getAutoCompleteResults(searchTerm);
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        out.print(new JSONArray(results));
        out.flush();
    }
}