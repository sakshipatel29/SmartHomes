// import React, { useState } from 'react';
// import axios from 'axios';

// function SemanticSearch() {
//     const [query, setQuery] = useState('');
//     const [results, setResults] = useState([]);

//     const handleSearch = async () => {
//         try {
//             const response = await axios.post('http://localhost:8080/myservlet/semantic-search', new URLSearchParams({ query }));
//             setResults(response.data);
//         } catch (error) {
//             console.error('Error performing semantic search:', error);
//         }
//     };

//     return (
//         <div>
//             <input
//                 type="text"
//                 value={query}
//                 onChange={(e) => setQuery(e.target.value)}
//                 placeholder="Enter your search query"
//             />
//             <button onClick={handleSearch}>Search Reviews</button>
//             <ul>
//                 {results.map((review, index) => (
//                     <li key={index}>{review.text}</li>
//                 ))}
//             </ul>
//         </div>
//     );
// }

// export default SemanticSearch;


import React, { useState } from 'react';
import axios from 'axios';

function SemanticSearch() {
    const [query, setQuery] = useState('');
    const [results, setResults] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    const handleSearch = async () => {
        setIsLoading(true);
        setError(null);
        try {
            // Update the endpoint to match your servlet's URL
            const response = await axios.get('http://localhost:8080/myservlet/semantic', {
                params: { question: query } // Send the query as a parameter
            });
            setResults(response.data.hits.hits); // Adjust according to Elasticsearch response structure
        } catch (error) {
            console.error('Error performing semantic search:', error);
            setError('An error occurred while searching. Please try again.');
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div>
            <input
                type="text"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                placeholder="Enter your search query"
            />
            <button onClick={handleSearch} disabled={isLoading}>
                {isLoading ? 'Searching...' : 'Search Reviews'}
            </button>
            
            {error && <p style={{color: 'red'}}>{error}</p>}
            
            {results.length > 0 ? (
                <ul>
                    {results.map((review, index) => (
                        <li key={index}>
                            {/* Accessing fields based on expected response structure */}
                            <h3>{review._source.productModelName}</h3>
                            <p>{review._source.reviewText}</p>
                            <p>Score: {review._source.reviewRating}</p>
                            <p>Price: ${review._source.productPrice}</p>
                        </li>
                    ))}
                </ul>
            ) : (
                results.length === 0 && <p>No results found.</p>
            )}
        </div>
    );
}

export default SemanticSearch;

// import React, { useState } from 'react';
// import axios from 'axios';

// function SemanticSearch() {
//     const [query, setQuery] = useState('');
//     const [results, setResults] = useState(null);
//     const [isLoading, setIsLoading] = useState(false);
//     const [error, setError] = useState(null);

//     const handleSearch = async () => {
//         setIsLoading(true);
//         setError(null);
//         try {
//             const response = await axios.post('http://localhost:8080/myservlet/semantic-search', new URLSearchParams({ query }));
//             setResults(response.data);
//         } catch (error) {
//             console.error('Error performing semantic search:', error);
//             setError('An error occurred while searching. Please try again.');
//         } finally {
//             setIsLoading(false);
//         }
//     };

//     return (
//         <div>
//             <input
//                 type="text"
//                 value={query}
//                 onChange={(e) => setQuery(e.target.value)}
//                 placeholder="Enter your search query"
//             />
//             <button onClick={handleSearch} disabled={isLoading}>
//                 {isLoading ? 'Searching...' : 'Search Reviews'}
//             </button>
            
//             {error && <p style={{color: 'red'}}>{error}</p>}
            
//             {results && results.length > 0 ? (
//                 <ul>
//                     {results.map((review, index) => (
//                         <li key={index}>
//                             <h3>{review.name}</h3>
//                             <p>{review.description}</p>
//                             <p>Score: {review.score}</p>
//                         </li>
//                     ))}
//                 </ul>
//             ) : (
//                 results && <p>No results found.</p>
//             )}
//         </div>
//     );
// }

// export default SemanticSearch;