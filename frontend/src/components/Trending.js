import React, { useState, useEffect } from 'react';
import './Trending.css';

const Trending = () => {
  const [trendingData, setTrendingData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchTrendingData = async () => {
      try {
        const response = await fetch('http://localhost:8080/myservlet/trending');
        if (!response.ok) {
          throw new Error('Failed to fetch trending data');
        }
        const data = await response.json();
        setTrendingData(data);
        setLoading(false);
      } catch (error) {
        setError(error.message);
        setLoading(false);
      }
    };
    fetchTrendingData();
  }, []);

  if (loading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;

  return (
    <div className="trending">
      <h2>Trending</h2>
      <div className="trending-section">
        <h3>Top Five Most Liked Products</h3>
        <ul>
          {trendingData.topRatedProducts && trendingData.topRatedProducts.map((product, index) => (
            <li key={index}>
              {product.productName} - {product.manufacturerName} - 
              Category: {product.productCategory} - 
              Price: ${product.productPrice} - 
              Average Rating: {product.averageRating.toFixed(2)}
            </li>
          ))}
        </ul>
      </div>
      <div className="trending-section">
        <h3>Top Five Zip Codes</h3>
        <ul>
          {trendingData.topZipCodes && trendingData.topZipCodes.map((zipCode, index) => (
            <li key={index}>{zipCode.zipCode} - {zipCode.orderCount} orders</li>
          ))}
        </ul>
      </div>
      <div className="trending-section">
        <h3>Top Five Most Sold Products</h3>
        <ul>
          {trendingData.topSoldProducts && trendingData.topSoldProducts.map((product, index) => (
            <li key={index}>{product.name} - {product.totalSold} sold</li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Trending;

