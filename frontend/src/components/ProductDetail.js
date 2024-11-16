import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';

const ProductDetail = () => {
    const { productId } = useParams(); // Get productId from URL parameters
    const [product, setProduct] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchProductDetails = async () => {
            try {
                const response = await fetch(`http://localhost:8080/myservlet/products?id=${productId}`); // Fetch product details using your API
                if (!response.ok) {
                    throw new Error('Network response was not ok');
                }
                const data = await response.json();
                setProduct(data); // Set the fetched product data
            } catch (err) {
                setError('Failed to fetch product details. Please try again later.');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchProductDetails();
    }, [productId]);

    if (loading) return <p>Loading...</p>;
    if (error) return <p style={{ color: 'red' }}>{error}</p>;

    return (
        <div>
            {product && (
                <>
                    <h1>{product.name}</h1>
                    <p>{product.description}</p>
                    <p>Price: ${product.price.toFixed(2)}</p>
                    <p>Category: {product.category}</p>
                    {/* Add more details as needed */}
                </>
            )}
        </div>
    );
};

export default ProductDetail;