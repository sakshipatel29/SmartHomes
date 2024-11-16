package servlets;

public class ReviewResult {
    private String id;
    private String productModelName;
    private String reviewText;
    private int reviewRating;
    private double score;

    // Constructor
    public ReviewResult(String id, String productModelName, String reviewText, int reviewRating, double score) {
        this.id = id;
        this.productModelName = productModelName;
        this.reviewText = reviewText;
        this.reviewRating = reviewRating;
        this.score = score;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductModelName() { return productModelName; }
    public void setProductModelName(String productModelName) { this.productModelName = productModelName; }

    public String getReviewText() { return reviewText; }
    public void setReviewText(String reviewText) { this.reviewText = reviewText; }

    public int getReviewRating() { return reviewRating; }
    public void setReviewRating(int reviewRating) { this.reviewRating = reviewRating; }

    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
}
