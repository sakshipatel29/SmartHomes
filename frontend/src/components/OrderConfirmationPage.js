import React from 'react';
import './OrderConfirmationPage.css';

const OrderConfirmationPage = ({ isVisible, onClose, confirmationNumber, pickupOrDeliveryDate }) => {
    if (!isVisible) return null;

    return (
        <div className="order-confirmation-popup">
            <div className="popup-content">
                <h1>Order Placed Successfully</h1>
                <p>Your order has been placed successfully.</p>
                <p>You can check your order details in the "Orders" section.</p>
                <p>Note: You can cancel your order up to 5 business days before the delivery date.</p>
                <button onClick={onClose}>Close</button>
            </div>
        </div>
    );
};

export default OrderConfirmationPage;
