import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import './Dashboard.css';

const Dashboard = () => {
    const [searchTerm, setSearchTerm] = useState('');
    const [suggestions, setSuggestions] = useState([]);
    const navigate = useNavigate();

    const categories = [
        { name: 'Smart Doorbells', image: '/images/doorbell.jpg' },
        { name: 'Smart Doorlocks', image: '/images/doorlock.jpg' },
        { name: 'Smart Speakers', image: '/images/speaker-image.png' },
        { name: 'Smart Lightings', image: '/images/lighting.jpg' },
        { name: 'Smart Thermostats', image: '/images/thermostats.jpg' }
    ];

    useEffect(() => {
        if (searchTerm.length > 1) {
            fetchSuggestions();
        } else {
            setSuggestions([]);
        }
    }, [searchTerm]);

    const fetchSuggestions = async () => {
        try {
            const response = await fetch(`http://localhost:8080/myservlet/autocomplete?term=${encodeURIComponent(searchTerm)}`);
            if (!response.ok) {
                throw new Error('Failed to fetch suggestions');
            }
            const data = await response.json();
            setSuggestions(data);
        } catch (error) {
            console.error('Error fetching suggestions:', error);
        }
    };

    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value);
    };

    const handleSearchSubmit = (e) => {
        e.preventDefault();
        if (searchTerm.trim()) {
            navigate(`/search?term=${encodeURIComponent(searchTerm)}`);
        }
    };

    const handleSuggestionClick = (suggestion) => {
        setSearchTerm(suggestion);
        navigate(`/search?term=${encodeURIComponent(suggestion)}`);
    };

    return (
        <div className="dashboard-container">
            <h1>Product Categories</h1>
            <form onSubmit={handleSearchSubmit} className="search-form">
                <input
                    type="text"
                    value={searchTerm}
                    onChange={handleSearchChange}
                    placeholder="Search products..."
                    className="search-input"
                />
                <button type="submit" className="search-button">Search</button>
            </form>
            {suggestions.length > 0 && (
                <ul className="suggestions-list">
                    {suggestions.map((suggestion, index) => (
                        <li key={index} onClick={() => handleSuggestionClick(suggestion)}>
                            {suggestion}
                        </li>
                    ))}
                </ul>
            )}
            <div className="product-categories">
                {categories.map((category, index) => (
                    <Link 
                        to={`/category/${category.name.toLowerCase().replace(/ /g, '-')}`}
                        key={index}
                    >
                        <div 
                            className="category-box" 
                            style={{ backgroundImage: `url(${category.image})` }}
                        >
                            <span className="category-name">{category.name}</span>
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
};

export default Dashboard;