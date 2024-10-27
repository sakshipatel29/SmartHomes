import React from 'react';
import './PaymentSuccessModal.css';

const PaymentSuccessModal = ({ isOpen, onClose }) => {
    if (!isOpen) return null;

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <h2>Payment Successful!</h2>
                <p>Thank you for your purchase. Your order has been confirmed.</p>
                <button className="close-modal" onClick={onClose}>
                    Close
                </button>
            </div>
        </div>
    );
};

export default PaymentSuccessModal;
