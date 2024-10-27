import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './PaymentPage.css';

const PaymentPage = () => {
    const [cardNumber, setCardNumber] = useState('');
    const [showSuccessPopup, setShowSuccessPopup] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = (e) => {
        e.preventDefault();
        setShowSuccessPopup(true);
        setTimeout(() => {
            navigate('/order-success');
        }, 2000);
    };

    return (
        <div className="payment-page">
            <h2>Payment</h2>
            <form onSubmit={handleSubmit}>
                <label>Credit Card Number:
                    <input
                        type="text"
                        value={cardNumber}
                        onChange={(e) => setCardNumber(e.target.value)}
                        required
                    />
                </label>
                <button type="submit">Pay Now</button>
            </form>

            {showSuccessPopup && (
                <div className="success-popup">
                    <p>Payment Successful! Redirecting to order confirmation...</p>
                </div>
            )}
        </div>
    );
};

export default PaymentPage;
