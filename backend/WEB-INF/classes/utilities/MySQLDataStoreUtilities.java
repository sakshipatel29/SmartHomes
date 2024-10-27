package utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.sql.Statement;
import java.util.Map;
import org.json.JSONObject;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MySQLDataStoreUtilities {

    private Connection conn;

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/smarthomes";
    private static final String USER = "root";
    private static final String PASS = "Sakshi@2906";

    public MySQLDataStoreUtilities() {
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public JSONObject validateUser(String email, String password, String type) {
        JSONObject result = new JSONObject();
        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND type = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.setString(3, type);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    result.put("success", true);
                } else {
                    result.put("success", false);
                    result.put("error", "Invalid email, password, or user type");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("error", "Database error: " + e.getMessage());
        }

        return result;
    }
    public JSONObject registerUser(String email, String password, String type, String name, String street, String city, String state, String zipcode) {
    JSONObject result = new JSONObject();
    String sql = "INSERT INTO users (email, password, type, name, street, city, state, zipcode) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, email);
        pstmt.setString(2, password);
        pstmt.setString(3, type);
        pstmt.setString(4, name);
        pstmt.setString(5, street);
        pstmt.setString(6, city);
        pstmt.setString(7, state);
        pstmt.setString(8, zipcode);

        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            result.put("success", true);
        } else {
            result.put("success", false);
            result.put("error", "Failed to register user");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        result.put("success", false);
        if (e.getErrorCode() == 1062) {
            result.put("error", "Email already exists");
        } else {
            result.put("error", "Database error: " + e.getMessage());
        }
    }

    return result;
}

public JSONArray getAllProducts() {
        JSONArray products = new JSONArray();
        String sql = "SELECT *, (SELECT COUNT(*) FROM product_likes WHERE product_id = products.id) as like_count FROM products";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                JSONObject product = new JSONObject();
                product.put("id", rs.getInt("id"));
                product.put("name", rs.getString("name"));
                product.put("price", rs.getDouble("price"));
                product.put("description", rs.getString("description"));
                product.put("image", rs.getString("image"));
                product.put("specialDiscount", rs.getDouble("special_discount"));
                product.put("manufacturerRebate", rs.getDouble("manufacturer_rebate"));
                product.put("category", rs.getString("category"));
                product.put("likeCount", rs.getInt("like_count"));
                product.put("available_quantity", rs.getInt("available_quantity"));
                products.put(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public JSONArray getProductsByCategory(String category) {
    JSONArray products = new JSONArray();
    String sql = "SELECT *, (SELECT COUNT(*) FROM product_likes WHERE product_id = products.id) as like_count FROM products WHERE category = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, category);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                JSONObject product = new JSONObject();
                product.put("id", rs.getInt("id"));
                product.put("name", rs.getString("name"));
                product.put("price", rs.getDouble("price"));
                product.put("description", rs.getString("description"));
                product.put("image", rs.getString("image"));
                product.put("specialDiscount", rs.getDouble("special_discount"));
                product.put("manufacturerRebate", rs.getDouble("manufacturer_rebate"));
                product.put("category", rs.getString("category"));
                product.put("likeCount", rs.getInt("like_count"));
                product.put("accessories", getAccessoriesForProduct(rs.getInt("id")));
                products.put(product);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
}

public JSONArray getAccessoriesForProduct(int productId) {
    JSONArray accessories = new JSONArray();
    String sql = "SELECT * FROM accessories WHERE product_id = ?";

    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, productId);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                JSONObject accessory = new JSONObject();
                accessory.put("id", rs.getInt("id"));
                accessory.put("name", rs.getString("name"));
                accessory.put("price", rs.getDouble("price"));
                accessories.put(accessory);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return accessories;
}


    public boolean addProduct(JSONObject product) {
        String sql = "INSERT INTO products (name, price, description, image, special_discount, manufacturer_rebate, category) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getString("name"));
            pstmt.setDouble(2, product.getDouble("price"));
            pstmt.setString(3, product.getString("description"));
            pstmt.setString(4, product.getString("image"));
            pstmt.setDouble(5, product.getDouble("specialDiscount"));
            pstmt.setDouble(6, product.getDouble("manufacturerRebate"));
            pstmt.setString(7, product.getString("category"));

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(int productId, JSONObject product) {
        String sql = "UPDATE products SET name = ?, price = ?, description = ?, image = ?, special_discount = ?, manufacturer_rebate = ?, category = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getString("name"));
            pstmt.setDouble(2, product.getDouble("price"));
            pstmt.setString(3, product.getString("description"));
            pstmt.setString(4, product.getString("image"));
            pstmt.setDouble(5, product.getDouble("specialDiscount"));
            pstmt.setDouble(6, product.getDouble("manufacturerRebate"));
            pstmt.setString(7, product.getString("category"));
            pstmt.setInt(8, productId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public JSONArray getUserCart(String email) {
    JSONArray cartItems = new JSONArray();
    String sql = "SELECT c.*, p.category, a.id as accessory_id, a.name as accessory_name, a.price as accessory_price " +
                 "FROM cart c " +
                 "JOIN products p ON c.product_id = p.id " +
                 "LEFT JOIN accessories a ON (c.product_id = a.id AND c.is_accessory = true) " +
                 "WHERE c.user_email = ?";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, email);
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                JSONObject item = new JSONObject();
                item.put("id", rs.getInt("id"));
                item.put("productId", rs.getInt("product_id"));
                item.put("productName", rs.getString("product_name"));
                item.put("productPrice", rs.getDouble("product_price"));
                item.put("quantity", rs.getInt("quantity"));
                item.put("isAccessory", rs.getBoolean("is_accessory"));
                item.put("category", rs.getString("category"));

                if (rs.getBoolean("is_accessory")) {
                    item.put("accessoryId", rs.getInt("accessory_id"));
                    item.put("accessoryName", rs.getString("accessory_name"));
                    item.put("accessoryPrice", rs.getDouble("accessory_price"));
                }

                cartItems.put(item);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return cartItems;
}

    public boolean addToCart(String email, JSONObject cartItem) {
        String sql = "INSERT INTO cart (user_email, product_id, product_name, product_price, quantity, is_accessory) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, cartItem.getInt("productId"));
            pstmt.setString(3, cartItem.getString("productName"));
            pstmt.setDouble(4, cartItem.getDouble("productPrice"));
            pstmt.setInt(5, cartItem.getInt("quantity"));
            pstmt.setBoolean(6, cartItem.getBoolean("isAccessory"));
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeFromCart(String email, int productId, boolean isAccessory) {
        String sql = "DELETE FROM cart WHERE user_email = ? AND product_id = ? AND is_accessory = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setInt(2, productId);
            pstmt.setBoolean(3, isAccessory);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCartItemQuantity(String email, int productId, int quantity) {
    String sql = "UPDATE cart SET quantity = ? WHERE user_email = ? AND product_id = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, quantity);
        pstmt.setString(2, email);
        pstmt.setInt(3, productId);
        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public void addOrderItem(String orderId, String userId, String customerName, String customerAddress,
                         String creditCardNumber, String purchaseDate, String shipDate, int productId,
                         String category, int quantity, double price, double shippingCost,
                         double discount, double totalSales, int storeId, String orderStatus) throws SQLException { // Added orderStatus parameter
    Connection conn = null;
    try {
        conn = getConnection();
        conn.setAutoCommit(false);

        String insertOrderSql = "INSERT INTO orders (order_id, user_id, customer_name, customer_address, credit_card_number, " +
                "purchase_date, ship_date, product_id, category, quantity, price, shipping_cost, " +
                "discount, total_sales, store_id, order_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pstmt = conn.prepareStatement(insertOrderSql)) {
            pstmt.setString(1, orderId);
            pstmt.setString(2, userId);
            pstmt.setString(3, customerName);
            pstmt.setString(4, customerAddress);
            pstmt.setString(5, creditCardNumber);
            pstmt.setString(6, purchaseDate);
            pstmt.setString(7, shipDate);
            pstmt.setInt(8, productId);
            pstmt.setString(9, category);
            pstmt.setInt(10, quantity);
            pstmt.setDouble(11, price);
            pstmt.setDouble(12, shippingCost);
            pstmt.setDouble(13, discount);
            pstmt.setDouble(14, totalSales);
            pstmt.setInt(15, storeId);
            pstmt.setString(16, orderStatus); // Setting order status
            pstmt.executeUpdate();
        }

        String updateQuantitySql = "UPDATE products SET available_quantity = available_quantity - ? WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateQuantitySql)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, productId);
            pstmt.executeUpdate();
        }

        conn.commit();
    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        throw e;
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

    public JSONObject getStoreAddress(int storeId) throws SQLException {
        String sql = "SELECT street, city, state, zip_code FROM store_locations WHERE StoreID = ?";
        JSONObject storeAddress = new JSONObject();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, storeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    storeAddress.put("street", rs.getString("street"));
                    storeAddress.put("city", rs.getString("city"));
                    storeAddress.put("state", rs.getString("state"));
                    storeAddress.put("zipCode", rs.getString("zip_code"));
                }
            }
        }
        return storeAddress;
    }
    public JSONArray getAllOrders() {
    JSONArray orders = new JSONArray();
    String sql = "SELECT * FROM orders";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
            JSONObject order = new JSONObject();
            order.put("orderId", rs.getString("order_id"));
            order.put("userId", rs.getString("user_id"));
            order.put("customerName", rs.getString("customer_name"));
            order.put("customerAddress", rs.getString("customer_address"));
            order.put("creditCardNumber", rs.getString("credit_card_number"));
            order.put("purchaseDate", rs.getString("purchase_date"));
            order.put("shipDate", rs.getString("ship_date"));
            order.put("productId", rs.getInt("product_id"));
            order.put("category", rs.getString("category"));
            order.put("quantity", rs.getInt("quantity"));
            order.put("price", rs.getDouble("price"));
            order.put("shippingCost", rs.getDouble("shipping_cost"));
            order.put("discount", rs.getDouble("discount"));
            order.put("totalSales", rs.getDouble("total_sales"));
            order.put("storeId", rs.getInt("store_id"));
            order.put("orderStatus", rs.getString("order_status")); // Adding order status
            orders.put(order);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return orders;
}


public boolean deleteOrder(String orderId) {
    String sql = "DELETE FROM orders WHERE order_id = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, orderId);
        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean updateOrder(String orderId, JSONObject newOrderData) {
    String sql = "UPDATE orders SET user_id = ?, customer_name = ?, customer_address = ?, " +
                 "credit_card_number = ?, purchase_date = ?, ship_date = ?, product_id = ?, " +
                 "category = ?, quantity = ?, price = ?, shipping_cost = ?, discount = ?, " +
                 "total_sales = ?, store_id = ?, order_status = ? WHERE order_id = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, newOrderData.getString("userId"));
        pstmt.setString(2, newOrderData.getString("customerName"));
        pstmt.setString(3, newOrderData.getString("customerAddress"));
        pstmt.setString(4, newOrderData.getString("creditCardNumber"));
        pstmt.setString(5, newOrderData.getString("purchaseDate"));
        pstmt.setString(6, newOrderData.getString("shipDate"));
        pstmt.setInt(7, newOrderData.getInt("productId"));
        pstmt.setString(8, newOrderData.getString("category"));
        pstmt.setInt(9, newOrderData.getInt("quantity"));
        pstmt.setDouble(10, newOrderData.getDouble("price"));
        pstmt.setDouble(11, newOrderData.getDouble("shippingCost"));
        pstmt.setDouble(12, newOrderData.getDouble("discount"));
        pstmt.setDouble(13, newOrderData.getDouble("totalSales"));
        pstmt.setInt(14, newOrderData.getInt("storeId"));
        pstmt.setString(15, newOrderData.getString("orderStatus")); // Adding order status
        pstmt.setString(16, orderId);

        int affectedRows = pstmt.executeUpdate();
        return affectedRows > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean incrementLikeCount(int productId, String userEmail) throws SQLException {
        if (hasUserLikedProduct(productId, userEmail)) {
            return false;
        }

        String sql1 = "INSERT INTO product_likes (product_id, user_email) VALUES (?, ?)";
        String sql2 = "UPDATE products SET like_count = like_count + 1 WHERE id = ?";
        
        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt1 = conn.prepareStatement(sql1);
                 PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                
                pstmt1.setInt(1, productId);
                pstmt1.setString(2, userEmail);
                pstmt1.executeUpdate();

                pstmt2.setInt(1, productId);
                pstmt2.executeUpdate();

                conn.commit();
                return true;
            }
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw e;
        } finally {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
public boolean hasUserLikedProduct(int productId, String userEmail) throws SQLException {
        String sql = "SELECT COUNT(*) FROM product_likes WHERE product_id = ? AND user_email = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.setString(2, userEmail);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }


    public int getLikeCount(int productId) throws SQLException {
        String sql = "SELECT like_count FROM products WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("like_count");
                }
            }
        }
        return 0;
    }
    public boolean productExists(int productId) {
    String sql = "SELECT COUNT(*) FROM products WHERE id = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setInt(1, productId);
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

public JSONArray getTopFiveZipCodes() {
    JSONArray zipCodes = new JSONArray();
    String sql = "SELECT customer_address, COUNT(*) as order_count FROM orders " +
                 "GROUP BY customer_address ORDER BY order_count DESC LIMIT 5";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
            JSONObject zipCode = new JSONObject();
            zipCode.put("zipCode", rs.getString("customer_address").split(",")[1].trim());
            zipCode.put("orderCount", rs.getInt("order_count"));
            zipCodes.put(zipCode);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return zipCodes;
}

public JSONArray getTopFiveSoldProducts() {
    JSONArray products = new JSONArray();
    String sql = "SELECT p.id, p.name, SUM(o.quantity) as total_sold FROM products p " +
                 "JOIN orders o ON p.id = o.product_id " +
                 "GROUP BY p.id ORDER BY total_sold DESC LIMIT 5";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        while (rs.next()) {
            JSONObject product = new JSONObject();
            product.put("id", rs.getInt("id"));
            product.put("name", rs.getString("name"));
            product.put("totalSold", rs.getInt("total_sold"));
            products.put(product);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
}

public String fetchProducts(String query) {
        JSONArray jsonArray = new JSONArray();
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                JSONObject product = new JSONObject();
                product.put("name", rs.getString("name"));
                product.put("price", rs.getDouble("price"));
                product.put("available_quantity", rs.getInt("available_quantity"));
                jsonArray.put(product);
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }
    
    public JSONArray getProductSalesReport() {
    JSONArray salesReport = new JSONArray();
    String sql = "SELECT p.name, p.price, SUM(o.quantity) as total_sold, SUM(o.quantity * o.price) as total_sales " +
                 "FROM products p " +
                 "JOIN orders o ON p.id = o.product_id " +
                 "GROUP BY p.id, p.name, p.price " +
                 "ORDER BY total_sales DESC";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            JSONObject product = new JSONObject();
            product.put("name", rs.getString("name"));
            product.put("price", rs.getDouble("price"));
            product.put("totalSold", rs.getInt("total_sold"));
            product.put("totalSales", rs.getDouble("total_sales"));
            salesReport.put(product);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return salesReport;
}

public JSONArray getDailySalesReport() {
    JSONArray dailySales = new JSONArray();
    String sql = "SELECT DATE(purchase_date) as sale_date, SUM(total_sales) as daily_total " +
                 "FROM orders " +
                 "GROUP BY DATE(purchase_date) " +
                 "ORDER BY sale_date DESC";
    
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            JSONObject day = new JSONObject();
            day.put("date", rs.getString("sale_date"));
            day.put("totalSales", rs.getDouble("daily_total"));
            dailySales.put(day);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return dailySales;
}
public void saveAllProducts(Map<Integer, JSONObject> productMap) {
        String sql = "INSERT INTO products (id, name, price, description, image, special_discount, manufacturer_rebate, category, available_quantity) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE name=VALUES(name), price=VALUES(price), description=VALUES(description), " +
                     "image=VALUES(image), special_discount=VALUES(special_discount), manufacturer_rebate=VALUES(manufacturer_rebate), " +
                     "category=VALUES(category), available_quantity=VALUES(available_quantity)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            for (JSONObject product : productMap.values()) {
                pstmt.setInt(1, product.getInt("id"));
                pstmt.setString(2, product.getString("name"));
                pstmt.setDouble(3, product.getDouble("price"));
                pstmt.setString(4, product.getString("description"));
                pstmt.setString(5, product.getString("image"));
                pstmt.setDouble(6, product.optDouble("specialDiscount", 0.0));
                pstmt.setDouble(7, product.optDouble("manufacturerRebate", 0.0));
                pstmt.setString(8, product.getString("category"));
                pstmt.setInt(9, product.optInt("availableQuantity", 0));
                pstmt.addBatch();
            }
            
            pstmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public JSONArray searchProducts(String searchTerm) {
    JSONArray products = new JSONArray();
    String sql = "SELECT * FROM products WHERE name LIKE ? OR description LIKE ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, "%" + searchTerm + "%");
        pstmt.setString(2, "%" + searchTerm + "%");
        try (ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                JSONObject product = new JSONObject();
                product.put("id", rs.getInt("id"));
                product.put("name", rs.getString("name"));
                product.put("price", rs.getDouble("price"));
                product.put("description", rs.getString("description"));
                product.put("image", rs.getString("image"));
                product.put("category", rs.getString("category"));
                products.put(product);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return products;
    }

    public boolean isOrderDelivered(String orderId) {
    boolean isDelivered = false;
    try (Connection conn = getConnection()) {  // Use try-with-resources for connection
        String query = "SELECT status FROM orders WHERE order_id = ?";
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, orderId);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            isDelivered = "delivered".equalsIgnoreCase(rs.getString("status"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return isDelivered;
}

public void insertTicket(String ticketNumber, String orderId, String decision, String description) {
    try (Connection conn = getConnection()) {  // Use try-with-resources for connection
        String query = "INSERT INTO tickets (ticket_number, order_id, decision, description) VALUES (?, ?, ?, ?)";
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, ticketNumber);
        pst.setString(2, orderId);
        pst.setString(3, decision);
        pst.setString(4, description);
        pst.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public String getTicketDecision(String ticketNumber) {
    String decision = null;
    try (Connection conn = getConnection()) {  // Use try-with-resources for connection
        String query = "SELECT decision FROM tickets WHERE ticket_number = ?";
        PreparedStatement pst = conn.prepareStatement(query);
        pst.setString(1, ticketNumber);
        ResultSet rs = pst.executeQuery();
        if (rs.next()) {
            decision = rs.getString("decision");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return decision;
}
}





