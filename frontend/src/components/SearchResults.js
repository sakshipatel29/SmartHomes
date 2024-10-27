
import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import './SearchResults.css';

const SearchResults = () => {
    const [results, setResults] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const location = useLocation();

    useEffect(() => {
        const searchParams = new URLSearchParams(location.search);
        const term = searchParams.get('term');

        const fetchResults = async () => {
            setLoading(true);
            try {
                const response = await fetch(`http://localhost:8080/myservlet/search?term=${encodeURIComponent(term)}`);
                if (!response.ok) {
                    throw new Error('Failed to fetch search results');
                }
                const data = await response.json();
                setResults(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        if (term) {
            fetchResults();
        }
    }, [location.search]);

    const addToCart = async (product) => {
        try {
            const response = await fetch('http://localhost:8080/myservlet/cart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    action: 'add',
                    email: localStorage.getItem('email'), // Assuming you store user email in localStorage
                    productId: product.id,
                    productName: product.name,
                    productPrice: product.price,
                    quantity: 1,
                    isAccessory: false
                }),
            });

            if (!response.ok) {
                throw new Error('Failed to add item to cart');
            }

            alert('Product added to cart successfully!');
        } catch (error) {
            console.error('Error adding to cart:', error);
            alert('Failed to add product to cart');
        }
    };

    if (loading) return <div>Loading...</div>;
    if (error) return <div>Error: {error}</div>;

    return (
        <div className="search-results">
            <h2>Search Results</h2>
            {results.length === 0 ? (
                <p>No results found.</p>
            ) : (
                <div className="product-list">
                    {results.map((product) => (
                        <div key={product.id} className="product-item">
                            <h3>{product.name}</h3>
                            <img src={product.image} alt={product.name} />
                            <p>Price: ${product.price.toFixed(2)}</p>
                            <p>Description: {product.description}</p>
                            <p>Special Discount: ${product.special_discount ? product.special_discount.toFixed(2) : '10.00'}</p>
                            <p>Manufacturer Rebate: ${product.manufacturer_rebate ? product.manufacturer_rebate.toFixed(2) : '15.00'}</p>
                            <button onClick={() => addToCart(product)}>Add to Cart</button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default SearchResults;