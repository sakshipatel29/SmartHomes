package servlets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class OpenAIClient {
    private String apiKey;

    public OpenAIClient(String apiKey) {
        this.apiKey = apiKey;
    }

    public List<Double> getEmbedding(String text) {
        try {
            // Define the API endpoint and connection
            String endpoint = "https://api.openai.com/v1/embeddings";
            URL url = new URL(endpoint);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Create the JSON request body
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "text-embedding-3-small");
            requestBody.put("input", text);

            // Send the request
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read the response
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            // Parse the response to extract the embedding
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray embeddingsArray = jsonResponse.getJSONArray("data");
            List<Double> embeddings = new ArrayList<>();
            if (embeddingsArray.length() > 0) {
                JSONArray embeddingValues = embeddingsArray.getJSONObject(0).getJSONArray("embedding");
                for (int i = 0; i < embeddingValues.length(); i++) {
                    embeddings.add(embeddingValues.getDouble(i));
                }
            }
            
            return embeddings;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // Return null if there was an error
    }
}