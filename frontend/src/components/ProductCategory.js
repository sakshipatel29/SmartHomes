import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import './ProductCategory.css';

const ProductCategory = () => {
  const { category } = useParams();
  const [products, setProducts] = useState([]);
  const [cart, setCart] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedProduct, setSelectedProduct] = useState(null);
  const [reviewForm, setReviewForm] = useState({
    productModelName: '',
    productCategory: '',
    productPrice: '',
    storeId: '',
    storeZip: '',
    storeCity: '',
    storeState: '',
    productOnSale: '',
    manufacturerName: '',
    manufacturerRebate: '',
    userId: '',
    userAge: '',
    userGender: '',
    userOccupation: '',
    reviewRating: '',
    reviewDate: '',
    reviewText: ''
  });
  const userEmail = localStorage.getItem('email');

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        const response = await fetch(`http://localhost:8080/myservlet/products?category=${encodeURIComponent(category)}`);
        if (!response.ok) {
          throw new Error('Failed to fetch products');
        }
        const data = await response.json();
        setProducts(data);
        setLoading(false);
      } catch (error) {
        setError(error.message);
        setLoading(false);
      }
    };
    fetchProducts();
  }, [category]);

  useEffect(() => {
    const fetchCart = async () => {
      try {
        const response = await fetch(`http://localhost:8080/myservlet/cart?email=${encodeURIComponent(userEmail)}`);
        if (!response.ok) {
          throw new Error('Failed to fetch cart');
        }
        const cartData = await response.json();
        setCart(cartData);
      } catch (error) {
        console.error('Error fetching cart:', error);
      }
    };
    fetchCart();
  }, [userEmail]);

  const getQuantity = (id, isAccessory = false) => {
    const itemInCart = cart.find(item => item.productId === id && item.isAccessory === isAccessory);
    return itemInCart ? itemInCart.quantity : 0;
  };

  const addToCart = async (item, isAccessory = false, parentProductId = null) => {
    try {
      const response = await fetch('http://localhost:8080/myservlet/cart', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          action: 'add',
          email: userEmail,
          productId: item.id,
          productName: item.name,
          productPrice: item.price,
          isAccessory: isAccessory,
          quantity: 1,
          parentProductId: parentProductId
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to add item to cart');
      }

      const updatedCart = await response.json();
      setCart(updatedCart);
    } catch (error) {
      console.error('Error adding to cart:', error);
    }
  };

  const handleQuantityChange = async (item, increment, isAccessory = false) => {
    const currentQuantity = getQuantity(item.id, isAccessory);
    const newQuantity = increment ? currentQuantity + 1 : Math.max(0, currentQuantity - 1);

    try {
      const response = await fetch('http://localhost:8080/myservlet/cart', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          action: newQuantity === 0 ? 'remove' : 'update',
          email: userEmail,
          productId: item.id,
          productName: item.name,
          productPrice: item.price,
          isAccessory: isAccessory,
          quantity: newQuantity
        }),
      });

      if (!response.ok) {
        throw new Error('Failed to update cart');
      }

      const updatedCart = await response.json();
      setCart(updatedCart);
    } catch (error) {
      console.error('Error updating cart:', error);
    }
  };

  const handleReviewSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('http://localhost:8080/myservlet/review', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(reviewForm),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to submit review');
      }

      const data = await response.json();
      alert('Review submitted successfully!');
      setReviewForm({
        productModelName: '',
        productCategory: '',
        productPrice: '',
        storeId: '',
        storeZip: '',
        storeCity: '',
        storeState: '',
        productOnSale: '',
        manufacturerName: '',
        manufacturerRebate: '',
        userId: '',
        userAge: '',
        userGender: '',
        userOccupation: '',
        reviewRating: '',
        reviewDate: '',
        reviewText: ''
      });
      setSelectedProduct(null);
    } catch (error) {
      console.error('Error submitting review:', error);
      alert(`Failed to submit review: ${error.message}`);
    }
  };

  const handleReviewChange = (e) => {
    setReviewForm({
      ...reviewForm,
      [e.target.name]: e.target.value
    });
  };

  const openReviewForm = (product) => {
    setSelectedProduct(product);
    setReviewForm({
      ...reviewForm,
      productModelName: product.name,
      productCategory: category,
      productPrice: product.price.toFixed(2),
      manufacturerName: product.manufacturer,
      manufacturerRebate: product.manufacturerRebate > 0 ? 'Yes' : 'No',
      productOnSale: product.specialDiscount > 0 ? 'Yes' : 'No',
      reviewDate: new Date().toLocaleDateString()
    });
  };

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="product-category">
      <h2>{category.replace('-', ' ').toUpperCase()}</h2>
      <div className="product-list">
        {products.map(product => (
          <div key={product.id} className="product-item">
            <img src={product.image} alt={product.name} />
            <h3>{product.name}</h3>
            <p>Price: ${product.price.toFixed(2)}</p>
            <p>Special Discount: ${product.specialDiscount.toFixed(2)}</p>
            <p>Manufacturer Rebate: ${product.manufacturerRebate.toFixed(2)}</p>
            <p>{product.description}</p>
            <button onClick={() => addToCart(product)}>Add to Cart</button>
            <button onClick={() => openReviewForm(product)}>Write Review</button>
            <div className="quantity-control">
              <button onClick={() => handleQuantityChange(product, false)}>-</button>
              <span>{getQuantity(product.id)}</span>
              <button onClick={() => handleQuantityChange(product, true)}>+</button>
            </div>
            {product.accessories && product.accessories.length > 0 && (
              <div className="accessories">
                <h4>Accessories</h4>
                {product.accessories.map(accessory => (
                  <div key={accessory.id} className="accessory-item">
                    <p>{accessory.name} - ${accessory.price.toFixed(2)}</p>
                    <button onClick={() => addToCart(accessory, true, product.id)}>Add Accessory</button>
                    <div className="quantity-control">
                      <button onClick={() => handleQuantityChange(accessory, false, true)}>-</button>
                      <span>{getQuantity(accessory.id, true)}</span>
                      <button onClick={() => handleQuantityChange(accessory, true, true)}>+</button>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>

      {selectedProduct && (
        <div className="review-form">
          <h3>Write a Review for {selectedProduct.name}</h3>
          <form onSubmit={handleReviewSubmit}>
            Product name:<input type="text" name="productModelName" value={reviewForm.productModelName} onChange={handleReviewChange} placeholder="Product Model Name" required />
            Product Category:<input type="text" name="productCategory" value={reviewForm.productCategory} onChange={handleReviewChange} placeholder="Product Category" required />
            Product Price:<input type="text" name="productPrice" value={reviewForm.productPrice} onChange={handleReviewChange} placeholder="Product Price" required />
            Store ID:<input type="text" name="storeId" value={reviewForm.storeId} onChange={handleReviewChange} placeholder="Store ID" required />
            Store Zipcode: <input type="text" name="storeZip" value={reviewForm.storeZip} onChange={handleReviewChange} placeholder="Store Zip" required />
            Store city:<input type="text" name="storeCity" value={reviewForm.storeCity} onChange={handleReviewChange} placeholder="Store City" required />
            Store state:<input type="text" name="storeState" value={reviewForm.storeState} onChange={handleReviewChange} placeholder="Store State" required />
            Product On Sale:<input type="text" name="productOnSale" value={reviewForm.productOnSale} onChange={handleReviewChange} placeholder="Product On Sale" required />
            Manufacturer name:<input type="text" name="manufacturerName" value={reviewForm.manufacturerName} onChange={handleReviewChange} placeholder="Manufacturer Name" required />
            manufacturer Rebate<input type="text" name="manufacturerRebate" value={reviewForm.manufacturerRebate} onChange={handleReviewChange} placeholder="Manufacturer Rebate" required />
            User ID:<input type="text" name="userId" value={reviewForm.userId} onChange={handleReviewChange} placeholder="User ID" required />
            User Age:<input type="number" name="userAge" value={reviewForm.userAge} onChange={handleReviewChange} placeholder="User Age" required />
            User Gender:<input type="text" name="userGender" value={reviewForm.userGender} onChange={handleReviewChange} placeholder="User Gender" required />
            User Occupation:<input type="text" name="userOccupation" value={reviewForm.userOccupation} onChange={handleReviewChange} placeholder="User Occupation" required />
            Review Rating<input type="number" name="reviewRating" value={reviewForm.reviewRating} onChange={handleReviewChange} placeholder="Review Rating" min="1" max="5" required />
            Review Date:<input type="text" name="reviewDate" value={reviewForm.reviewDate} onChange={handleReviewChange} placeholder="Review Date" required />
            Write your review:<textarea name="reviewText" value={reviewForm.reviewText} onChange={handleReviewChange} placeholder="Review Text" required></textarea>
            <button type="submit">Submit Review</button>
          </form>
        </div>
      )}
    </div>
  );
};

export default ProductCategory;