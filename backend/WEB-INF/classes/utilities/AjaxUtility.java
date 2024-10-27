package utilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AjaxUtility {
    private MySQLDataStoreUtilities dbUtil;
    private Map<String, List<String>> autocompleteCache;

    public AjaxUtility() {
        this.dbUtil = new MySQLDataStoreUtilities();
        this.autocompleteCache = new HashMap<>();
    }    

    public List<String> getAutoCompleteResults(String searchTerm) {
        searchTerm = searchTerm.toLowerCase();

        if (autocompleteCache.containsKey(searchTerm)) {
            return autocompleteCache.get(searchTerm);
        }

        List<String> results = new ArrayList<>();
        String sql = "SELECT name FROM products WHERE lower(name) LIKE ? LIMIT 5";
        
        try (Connection conn = dbUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchTerm + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(rs.getString("name"));
                }
            }

            autocompleteCache.put(searchTerm, results);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return results;
    }

    public void clearCache() {
        autocompleteCache.clear();
    }
}