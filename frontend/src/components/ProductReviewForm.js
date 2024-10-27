import React, { useState } from 'react';

const ProductReviewForm = ({ product, onClose, userEmail }) => {
  const [formData, setFormData] = useState({
    productModelName: product.name,
    productCategory: product.category,
    productPrice: product.price,
    storeId: 'SmartPortables of Chicago',
    storeZip: '60616',
    storeCity: 'Chicago',
    storeState: 'IL',
    productOnSale: product.specialDiscount > 0 ? 'Yes' : 'No',
    manufacturerName: product.manufacturer,
    manufacturerRebate: product.manufacturerRebate > 0 ? 'Yes' : 'No',
    userId: userEmail,
    userAge: '',
    userGender: '',
    userOccupation: '',
    reviewRating: '',
    reviewText: ''
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('http://localhost:8080/myservlet/review', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          ...formData,
          reviewDate: new Date().toLocaleDateString()
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to submit review');
      }

      alert('Review submitted successfully!');
      onClose();
    } catch (error) {
      console.error('Error submitting review:', error);
      alert('Failed to submit review. Please try again.');
    }
  };

  return (
    <div className="review-form-overlay">
      <div className="review-form-container">
        <h2>Write a Review</h2>
        <form onSubmit={handleSubmit}>
          Product: <input type="text" name="productModelName" value={formData.productModelName} readOnly />
          Category:<input type="text" name="productCategory" value={formData.productCategory} readOnly />
          Price:<input type="text" name="productPrice" value={formData.productPrice} readOnly />
          Store ID:<input type="text" name="storeId" value={formData.storeId} readOnly />
          Store Zipcode:<input type="text" name="storeZip" value={formData.storeZip} readOnly />
          Store City: <input type="text" name="storeCity" value={formData.storeCity} readOnly />
          Store State:<input type="text" name="storeState" value={formData.storeState} readOnly />
          Product on Sale:<input type="text" name="productOnSale" value={formData.productOnSale} readOnly />
          Manufacturer Name:<input type="text" name="manufacturerName" value={formData.manufacturerName} readOnly />
          Manufacturer Rebate:<input type="text" name="manufacturerRebate" value={formData.manufacturerRebate} readOnly />
          User ID:<input type="text" name="userId" value={formData.userId} readOnly />
          Your Age:<input type="number" name="userAge" value={formData.userAge} onChange={handleChange} placeholder="Age" required />
          Gender:<select name="userGender" value={formData.userGender} onChange={handleChange} required>
            <option value="">Select Gender</option>
            <option value="Male">Male</option>
            <option value="Female">Female</option>
            <option value="Other">Other</option>
          </select>
          Your Occupation:<input type="text" name="userOccupation" value={formData.userOccupation} onChange={handleChange} placeholder="Occupation" required />
          Ratings:<select name="reviewRating" value={formData.reviewRating} onChange={handleChange} required>
            <option value="">Select Rating</option>
            <option value="1">1</option>
            <option value="2">2</option>
            <option value="3">3</option>
            <option value="4">4</option>
            <option value="5">5</option>
          </select>
          Write Your Reviews:<textarea name="reviewText" value={formData.reviewText} onChange={handleChange} placeholder="Write your review here" required></textarea>
          <button type="submit">Submit Review</button>
          <button type="button" onClick={onClose}>Cancel</button>
        </form>
      </div>
    </div>
  );
};

export default ProductReviewForm;