

import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './OrderPage.css'; 

const OrderPage = () => {
    const [orders, setOrders] = useState([]);
    const [userEmail, setUserEmail] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const email = localStorage.getItem('email');
        setUserEmail(email);

        fetch(`http://localhost:8080/myservlet/get`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        })
            .then(response => response.json())
            .then(data => setOrders(data))
            .catch(error => console.error('Error fetching orders:', error));
    }, []);

    const handleCancelOrder = (orderId) => {
        fetch(`http://localhost:8080/myservlet/delete?orderId=${orderId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            }
            throw new Error('Network response was not ok.');
        })
        .then(data => {
            if (data.status === "success") {
                alert('Order cancelled successfully');
                setOrders(orders.filter(order => order.orderId !== orderId));
            } else {
                alert('Failed to cancel order: ' + data.message);
            }
        })
        .catch(error => console.error('Error cancelling order:', error));
    };

    return (
        <div className="order-container">
            <h1 className="order-header">Your Orders</h1>
            {orders.length > 0 ? (
                <ul className="order-list">
                    {orders.map(order => (
                        <li key={order.orderId} className="order-item">
                            <h3>Order ID: {order.orderId}</h3>
                            <p><strong>Customer Name:</strong> {order.customerName}</p>
                            <p><strong>Address:</strong> {order.customerAddress}</p>
                            <p><strong>Purchase Date:</strong> {order.purchaseDate}</p>
                            <p><strong>Ship Date:</strong> {order.shipDate}</p>
                            <p><strong>Product ID:</strong> {order.productId}</p>
                            <p><strong>Category:</strong> {order.category}</p>
                            <p><strong>Quantity:</strong> {order.quantity}</p>
                            <p><strong>Price:</strong> ${order.price}</p>
                            <p><strong>Shipping Cost:</strong> ${order.shippingCost}</p>
                            <p><strong>Discount:</strong> ${order.discount}</p>
                            <p><strong>Total Sales:</strong> ${order.totalSales}</p>
                            <p><strong>Order Status:</strong> {order.orderStatus}</p>
                            {order.storeId && (
                                <p><strong>Store ID:</strong> {order.storeId}</p>
                            )}
                            <button className="cancel-button" onClick={() => handleCancelOrder(order.orderId)}>
                                Cancel Order
                            </button>
                        </li>
                    ))}
                </ul>
            ) : (
                <p className="no-orders">No orders found.</p>
            )}
        </div>
    );
};

export default OrderPage;
