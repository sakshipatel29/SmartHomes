import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

@WebServlet("/recommend")
public class ProductRecommendationServlet extends HttpServlet {

    private static final String ELASTICSEARCH_URL = "http://localhost:9200/products/_search";
    private static final String OPENAI_API_KEY = "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";
    private static final String EMBEDDING_MODEL = "text-embedding-3-small";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userQuery = req.getParameter("query");
        if (userQuery == null || userQuery.isEmpty()) {
            resp.getWriter().write("Please provide a 'query' parameter.");
            return;
        }

        // Get embedding for the user query
        String embedding = getEmbedding(userQuery);

        // Perform product search in Elasticsearch
        String searchResults = performProductSearch(embedding);

        // Return the results
        resp.setContentType("application/json");
        resp.getWriter().write(searchResults);
    }

    private String getEmbedding(String text) throws IOException {
        URL url = new URL("https://api.openai.com/v1/embeddings");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        // Create JSON request body
        String jsonInputString = "{\"model\": \"" + EMBEDDING_MODEL + "\", \"input\": [\"" + text.replace("\n", " ") + "\"]}";

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        // Read response
        try (Scanner scanner = new Scanner(connection.getInputStream())) {
            return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
        }
    }

    private String performProductSearch(String embedding) throws IOException {
        String parsedEmbedding = parseEmbeddingToArray(embedding);

        URL url = new URL(ELASTICSEARCH_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = "{"
                + "\"size\": 5,"
                + "\"query\": {"
                + "\"script_score\": {"
                + "\"query\": { \"match_all\": {} },"
                + "\"script\": {"
                + "\"source\": \"cosineSimilarity(params.query_vector, 'embedding_vector') + 1.0\","
                + "\"params\": {\"query_vector\": " + parsedEmbedding + "}"
                + "}"
                + "}"
                + "},"
                + "\"_source\": [\"name\", \"description\", \"price\", \"category\"]"
                + "}";

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonInputString.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (Scanner scanner = new Scanner(connection.getInputStream())) {
                return scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
            }
        } else {
            try (InputStream errorStream = connection.getErrorStream();
                 Scanner scanner = new Scanner(errorStream)) {
                String errorResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                throw new IOException("Elasticsearch Error: " + errorResponse);
            }
        }
    }

    private String parseEmbeddingToArray(String embeddingJson) {
        JSONObject jsonObject = new JSONObject(embeddingJson);
        JSONArray dataArray = jsonObject.getJSONArray("data");
        return dataArray.getJSONObject(0).getJSONArray("embedding").toString();
    }
}