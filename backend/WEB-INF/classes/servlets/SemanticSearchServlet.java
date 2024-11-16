// package servlets;

// import javax.servlet.ServletException;
// import javax.servlet.annotation.WebServlet;
// import javax.servlet.http.HttpServlet;
// import javax.servlet.http.HttpServletRequest;
// import javax.servlet.http.HttpServletResponse;
// import java.io.IOException;
// import java.io.PrintWriter;
// import java.net.HttpURLConnection;
// import java.net.URL;
// import java.util.Scanner;

// @WebServlet("/semantic-search")
// public class SemanticSearchServlet extends HttpServlet {

//     private static final String OPENAI_API_KEY = "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";
//     private static final String ELASTICSEARCH_URL = "http://localhost:9200/products/_search";

//     @Override
//     protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//         String userInput = request.getParameter("query");
//         if (userInput == null || userInput.isEmpty()) {
//             response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Query parameter is missing");
//             return;
//         }

//         try {
//             // Debugging: Log the input query
//             System.out.println("Received Query: " + userInput);

//             // Get embedding for the query text
//             double[] queryVector = getEmbedding(userInput);

//             // Debugging: Log the query embedding
//             System.out.println("Query Embedding: " + java.util.Arrays.toString(queryVector));

//             // Search for similar reviews
//             String jsonResults = searchSimilarReviews(queryVector);

//             // Debugging: Log Elasticsearch results
//             System.out.println("Elasticsearch Results: " + jsonResults);

//             // Send response
//             response.setContentType("application/json");
//             PrintWriter out = response.getWriter();
//             out.print(jsonResults);
//             out.flush();
//         } catch (Exception e) {
//             e.printStackTrace(); // Log full stack trace to server logs
//             response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while processing the request: " + e.getMessage());
//         }
//     }

//     private double[] getEmbedding(String text) throws IOException {
//         URL url = new URL("https://api.openai.com/v1/embeddings");
//         HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//         conn.setRequestMethod("POST");
//         conn.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
//         conn.setRequestProperty("Content-Type", "application/json");
//         conn.setDoOutput(true);

//         String jsonInputString = "{\"input\": \"" + text.replace("\"", "\\\"") + "\", \"model\": \"text-embedding-3-small\"}";

//         try (PrintWriter writer = new PrintWriter(conn.getOutputStream())) {
//             writer.print(jsonInputString);
//             writer.flush();
//         }

//         // Capture the OpenAI API response
//         StringBuilder jsonResponse = new StringBuilder();
//         try (Scanner scanner = new Scanner(conn.getInputStream())) {
//             while (scanner.hasNextLine()) {
//                 jsonResponse.append(scanner.nextLine());
//             }
//         }

//         // Debugging: Log the raw OpenAI response
//         System.out.println("OpenAI Response: " + jsonResponse);

//         // Parse the JSON response to extract the embedding
//         org.json.JSONObject responseJson = new org.json.JSONObject(jsonResponse.toString());
//         org.json.JSONArray embeddingArray = responseJson.getJSONArray("data").getJSONObject(0).getJSONArray("embedding");

//         double[] embedding = new double[embeddingArray.length()];
//         for (int i = 0; i < embeddingArray.length(); i++) {
//             embedding[i] = embeddingArray.getDouble(i);
//         }
//         return embedding;
//     }

//     private String searchSimilarReviews(double[] queryVector) throws IOException {
//     StringBuilder queryBuilder = new StringBuilder();

//     queryBuilder.append("{\"size\": 5, \"query\": {\"script_score\": {\"query\": {\"match_all\": {}}, \"script\": {\"source\": \"cosineSimilarity(params.query_vector, doc['embedding_vector']) + 1.0\", \"params\": {\"query_vector\": [");

//     for (int i = 0; i < queryVector.length; i++) {
//         queryBuilder.append(queryVector[i]);
//         if (i < queryVector.length - 1) {
//             queryBuilder.append(", ");
//         }
//     }

//     queryBuilder.append("]}}}}}");

//     URL url = new URL(ELASTICSEARCH_URL);
//     HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//     conn.setRequestMethod("POST");
//     conn.setRequestProperty("Content-Type", "application/json");
//     conn.setDoOutput(true);

//     try (PrintWriter writer = new PrintWriter(conn.getOutputStream())) {
//         writer.print(queryBuilder.toString());
//         writer.flush();
//     }

//     int responseCode = conn.getResponseCode();
//     if (responseCode >= 400) {
//         StringBuilder errorResponse = new StringBuilder();
//         try (Scanner scanner = new Scanner(conn.getErrorStream())) {
//             while (scanner.hasNextLine()) {
//                 errorResponse.append(scanner.nextLine());
//             }
//         }
//         System.err.println("Elasticsearch Error Response: " + errorResponse);
//         throw new IOException("Elasticsearch query failed with status code: " + responseCode + " and message: " + errorResponse);
//     }

//     StringBuilder jsonResponse = new StringBuilder();
//     try (Scanner scanner = new Scanner(conn.getInputStream())) {
//         while (scanner.hasNextLine()) {
//             jsonResponse.append(scanner.nextLine());
//         }
//     }

//     return jsonResponse.toString();
// }

// }

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

@WebServlet("/semantic")
public class SemanticSearchServlet extends HttpServlet {

    private static final String ELASTICSEARCH_URL = "http://localhost:9200/products/_search";
    private static final String OPENAI_API_KEY = "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";
    private static final String EMBEDDING_MODEL = "text-embedding-3-small";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String question = req.getParameter("question");
        if (question == null || question.isEmpty()) {
            resp.getWriter().write("Please provide a 'question' parameter.");
            return;
        }

        // Get embedding for the question
        String embedding = getEmbedding(question);

        // Perform KNN search in Elasticsearch with filters for reviews
        String searchResults = performKnnSearch(embedding);

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

    private String performKnnSearch(String embedding) throws IOException {
        String parsedEmbedding = parseEmbeddingToArray(embedding);

        URL url = new URL(ELASTICSEARCH_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        String jsonInputString = "{"
                + "\"query\": {"
                + "\"bool\": {"
                + "\"must\": ["
                + "{ \"range\": { \"reviewRating\": { \"gt\": 4 } } },"
                + "{ \"range\": { \"reviewDate\": { \"gte\": \"2024-09-26\" } } }"
                + "]"
                + "}"
                + "},"
                + "\"knn\": {"
                + "\"field\": \"embedding_vector\","
                + "\"query_vector\": " + parsedEmbedding + ","
                + "\"k\": 5,"
                + "\"num_candidates\": 100"
                + "},"
                + "\"_source\": [\"manufacturerName\", \"productModelName\", \"reviewText\", "
                + "\"reviewRating\", \"productPrice\", \"userAge\", \"userGender\", "
                + "\"productCategory\", \"reviewDate\"]"
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