import React from 'react';
import { useParams } from 'react-router-dom';
import './CategoryPage.css';

const CategoryPage = () => {
    const { categoryName } = useParams();
    console.log('Category Name:', categoryName);

    if (!categoryName) {
        return <h2>Category not found</h2>;
    }

    return (
        <div>
            <h1>{categoryName.replace(/-/g, ' ').toUpperCase()}</h1>
            <p>Products will be displayed here for the category: {categoryName}</p>
        </div>
    );
};

export default CategoryPage;
