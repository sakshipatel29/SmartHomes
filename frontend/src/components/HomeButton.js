import React from 'react';
import { useNavigate } from 'react-router-dom';

const HomeButton = () => {
    const navigate = useNavigate();

    const goToDashboard = () => {
        navigate('/dashboard');
    };

    return (
        <button className="buttons"  onClick={goToDashboard}>
            Home
        </button>
    );
};

export default HomeButton;
