// import React, { useState } from 'react';

// const ProductSearch = () => {
//     const [query, setQuery] = useState('');
//     const [products, setProducts] = useState([]);
//     const [loading, setLoading] = useState(false);
//     const [error, setError] = useState('');

//     const handleSearch = async () => {
//         if (!query) {
//             alert("Please enter a search query.");
//             return;
//         }

//         setLoading(true);
//         setError('');
        
//         try {
//             const response = await fetch(`http://localhost:8080/myservlet/recommend?query=${encodeURIComponent(query)}`);
//             if (!response.ok) {
//                 throw new Error('Network response was not ok');
//             }
//             const data = await response.json();
//             setProducts(data.hits.hits); // Adjust based on the actual structure of your response
//         } catch (err) {
//             setError('Failed to fetch products. Please try again later.');
//             console.error(err);
//         } finally {
//             setLoading(false);
//         }
//     };

//     return (
//         <div>
//             <h1>Product Search</h1>
//             <input
//                 type="text"
//                 value={query}
//                 onChange={(e) => setQuery(e.target.value)}
//                 placeholder="Enter your product interest"
//             />
//             <button onClick={handleSearch} disabled={loading}>
//                 {loading ? 'Searching...' : 'Search'}
//             </button>

//             {error && <p style={{ color: 'red' }}>{error}</p>}

//             <div>
//                 <h2>Recommended Products</h2>
//                 {products.length > 0 ? (
//                     <ul>
//                         {products.map((product, index) => (
//                             <li key={index}>
//                                 <h3>{product._source.name}</h3>
//                                 <p>{product._source.description}</p>
//                                 <p>Price: ${product._source.price.toFixed(2)}</p>
//                                 <p>Category: {product._source.category}</p>
//                                 <p>Similarity Score: {product._score.toFixed(4)}</p>
//                             </li>
//                         ))}
//                     </ul>
//                 ) : (
//                     <p>No products found based on your query.</p>
//                 )}
//             </div>
//         </div>
//     );
// };

// export default ProductSearch;

import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // Import useNavigate for navigation

const ProductSearch = () => {
    const [query, setQuery] = useState('');
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');
    const navigate = useNavigate(); // Initialize navigate for navigation

    const handleSearch = async () => {
        if (!query) {
            alert("Please enter a search query.");
            return;
        }

        setLoading(true);
        setError('');
        
        try {
            const response = await fetch(`http://localhost:8080/myservlet/recommend?query=${encodeURIComponent(query)}`);
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setProducts(data.hits.hits); // Adjust based on the actual structure of your response
        } catch (err) {
            setError('Failed to fetch products. Please try again later.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleProductClick = (category) => {
        // Redirect to the category page
        navigate(`/category/${encodeURIComponent(category)}`); // Use encodeURIComponent to safely encode the category
    };

    return (
        <div>
            <h1>Product Search</h1>
            <input
                type="text"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Enter your product interest"
            />
            <button onClick={handleSearch} disabled={loading}>
                {loading ? 'Searching...' : 'Search'}
            </button>

            {error && <p style={{ color: 'red' }}>{error}</p>}

            <div>
                <h2>Recommended Products</h2>
                {products.length > 0 ? (
                    <ul>
                        {products.map((product, index) => (
                            <li key={index} onClick={() => handleProductClick(product._source.category)} style={{ cursor: 'pointer' }}>
                                <h3>{product._source.name}</h3>
                                <p>{product._source.description}</p>
                                <p>Price: ${product._source.price.toFixed(2)}</p>
                                <p>Category: {product._source.category}</p>
                                <p>Similarity Score: {product._score.toFixed(4)}</p>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p>No products found based on your query.</p>
                )}
            </div>
        </div>
    );
};

export default ProductSearch;