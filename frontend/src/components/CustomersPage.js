// import React, { useState, useEffect } from 'react';
// import './CustomersPage.css';

// const CustomersPage = () => {
//     const [orders, setOrders] = useState([]);
//     const [editOrder, setEditOrder] = useState(null);
//     const [showSignUp, setShowSignUp] = useState(false);
//     const [signUpData, setSignUpData] = useState({
//         email: '',
//         password: '',
//         confirmPassword: '',
//         type: 'customer'
//     });                    

//     useEffect(() => {
//         fetch('http://localhost:8080/myservlet/get', {
//             method: 'GET',
//             headers: {
//                 'Content-Type': 'application/json',
//             }
//         })
//         .then(response => response.json())
//         .then(data => setOrders(data))
//         .catch(error => console.error('Error fetching orders:', error));
//     }, []);

//     const handleUpdateOrder = (orderId) => {
//         fetch('http://localhost:8080/myservlet/update', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json',
//             },
//             body: JSON.stringify(editOrder)
//         })
//         .then(response => response.json())
//         .then(data => {
//             if (data.status === 'Order updated successfully') {
//                 setOrders(orders.map(order => order.orderId === orderId ? editOrder : order));
//                 setEditOrder(null);
//             } else {
//                 alert('Failed to update order');
//             }
//         })
//         .catch(error => console.error('Error updating order:', error));
//     };

//     const handleDeleteOrder = (orderId) => {
//         fetch(`http://localhost:8080/myservlet/delete?orderId=${encodeURIComponent(orderId)}`, {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json',
//             },
//             body: JSON.stringify({ orderId: orderId })
//         })
//         .then(response => response.json())
//         .then(data => {
//             if (data.status === 'Order deleted successfully') {
//                 alert('Order deleted successfully');
//                 setOrders(orders.filter(order => order.orderId !== orderId));
//             } else {
//                 alert('Failed to delete order');
//             }
//         })
//         .catch(error => console.error('Error deleting order:', error));
//     };

//     const handleSignUp = () => {
//         if (signUpData.password !== signUpData.confirmPassword) {
//             alert('Passwords do not match');
//             return;
//         }

//         fetch('http://localhost:8080/myservlet/signup', {
//             method: 'POST',
//             headers: {
//                 'Content-Type': 'application/json',
//             },
//             body: JSON.stringify({
//                 email: signUpData.email,
//                 password: signUpData.password,
//                 type: signUpData.type
//             })
//         })
//         .then(response => response.json())
//         .then(data => {
//             if (data.success) {
//                 alert('Account created successfully');
//                 setShowSignUp(false);
//             } else {
//                 alert(data.error || 'Account created successfully');
//             }
//         })
//         .catch(error => console.error('Error creating account:', error));
//     };

//     return (
//         <div className="customers-page">
//             <h1 className="page-title">Customers Orders</h1>
            
//             <button className="create-account-button" onClick={() => setShowSignUp(true)}>
//                 Create Account
//             </button>
            
//             <h2 className="section-title">All Orders</h2>
//             {orders.length > 0 ? (
//                 <ul className="orders-list">
//                     {orders.map(order => (
//                         <li key={order.orderId} className="order-item">
//                             <h3 className="order-id">Order ID: {order.orderId}</h3>
//                             <p><strong>Customer Name:</strong> {order.customerName}</p>
//                             <p><strong>Product ID:</strong> {order.productId}</p>
//                             <p><strong>Category:</strong> {order.category}</p>
//                             <p><strong>Quantity:</strong> {order.quantity}</p>
//                             <p><strong>Price:</strong> ${order.price}</p>
//                             <p><strong>Total Sales:</strong> ${order.totalSales}</p>
//                             <button className="edit-button" onClick={() => setEditOrder(order)}>Edit Order</button>
//                             <button className="delete-button" onClick={() => handleDeleteOrder(order.orderId)}>Delete Order</button>
//                         </li>
//                     ))}
//                 </ul>
//             ) : (
//                 <p className="no-orders">No orders found.</p>
//             )}

//             {editOrder && (
//                 <div className="edit-order-popup">
//                     <h2>Edit Order</h2>
//                     <input 
//                         type="text" 
//                         value={editOrder.customerName} 
//                         onChange={(e) => setEditOrder({ ...editOrder, customerName: e.target.value })} 
//                         className="edit-input"
//                     />
//                     <input 
//                         type="number" 
//                         value={editOrder.quantity} 
//                         onChange={(e) => setEditOrder({ ...editOrder, quantity: parseInt(e.target.value) })} 
//                         className="edit-input"
//                     />
//                     <input 
//                         type="number" 
//                         value={editOrder.price} 
//                         onChange={(e) => setEditOrder({ ...editOrder, price: parseFloat(e.target.value) })} 
//                         className="edit-input"
//                     />
//                     <button className="update-button" onClick={() => handleUpdateOrder(editOrder.orderId)}>Update Order</button>
//                     <button className="cancel-button" onClick={() => setEditOrder(null)}>Cancel</button>
//                 </div>
//             )}

//             {showSignUp && (
//                 <div className="signup-popup">
//                     <div className="popup-content">
//                         <h2>Create Account</h2>
//                         <input
//                             type="email"
//                             placeholder="Email"
//                             value={signUpData.email}
//                             onChange={(e) => setSignUpData({ ...signUpData, email: e.target.value })}
//                             className="signup-input"
//                         />
//                         <input
//                             type="password"
//                             placeholder="Password"
//                             value={signUpData.password}
//                             onChange={(e) => setSignUpData({ ...signUpData, password: e.target.value })}
//                             className="signup-input"
//                         />
//                         <input
//                             type="password"
//                             placeholder="Confirm Password"
//                             value={signUpData.confirmPassword}
//                             onChange={(e) => setSignUpData({ ...signUpData, confirmPassword: e.target.value })}
//                             className="signup-input"
//                         />
//                         <select
//                             value={signUpData.type}
//                             onChange={(e) => setSignUpData({ ...signUpData, type: e.target.value })}
//                             className="signup-select"
//                         >
//                             <option value="customer">Customer</option>
//                             <option value="salesman">Salesman</option>
//                         </select>
//                         <button className="signup-button" onClick={handleSignUp}>Add Customer</button>
//                         <button className="cancel-button" onClick={() => setShowSignUp(false)}>Cancel</button>
//                     </div>
//                 </div>
//             )}
//         </div>
//     );
// };

// export default CustomersPage;

import React, { useState, useEffect } from 'react';
import './CustomersPage.css';

const CustomersPage = () => {
    const [orders, setOrders] = useState([]);
    const [editOrder, setEditOrder] = useState(null);
    const [showSignUp, setShowSignUp] = useState(false);
    const [signUpData, setSignUpData] = useState({
        email: '',
        password: '',
        confirmPassword: '',
        type: 'customer'
    });                    

    useEffect(() => {
        fetch('http://localhost:8080/myservlet/get', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(response => response.json())
        .then(data => setOrders(data))
        .catch(error => console.error('Error fetching orders:', error));
    }, []);

    const handleUpdateOrder = (orderId) => {
        fetch('http://localhost:8080/myservlet/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(editOrder)
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'Order updated successfully') {
                alert('Order updated successfully');
                setOrders(orders.map(order => order.orderId === orderId ? editOrder : order));
                setEditOrder(null);
            } else {
                alert('Failed to update order');
            }
        })
        .catch(error => console.error('Error updating order:', error));
    };

    const handleDeleteOrder = (orderId) => {
        fetch(`http://localhost:8080/myservlet/delete?orderId=${encodeURIComponent(orderId)}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ orderId: orderId })
        })
        .then(response => response.json())
        .then(data => {
            if (data.status === 'Order deleted successfully') {
                alert('Order deleted successfully');
                setOrders(orders.filter(order => order.orderId !== orderId));
            } else {
                alert('Failed to delete order');
            }
        })
        .catch(error => console.error('Error deleting order:', error));
    };

    const handleSignUp = () => {
        if (signUpData.password !== signUpData.confirmPassword) {
            alert('Passwords do not match');
            return;
        }

        fetch('http://localhost:8080/myservlet/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: signUpData.email,
                password: signUpData.password,
                type: signUpData.type
            })
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Account created successfully');
                setShowSignUp(false);
            } else {
                alert(data.error || 'Failed to create account');
            }
        })
        .catch(error => console.error('Error creating account:', error));
    };

    return (
        <div className="customers-page">
            <h1 className="page-title">Customers Orders</h1>
            
            <button className="create-account-button" onClick={() => setShowSignUp(true)}>
                Create Account
            </button>
            
            <h2 className="section-title">All Orders</h2>
            {orders.length > 0 ? (
                <ul className="orders-list">
                    {orders.map(order => (
                        <li key={order.orderId} className="order-item">
                            <h3 className="order-id">Order ID: {order.orderId}</h3>
                            <p><strong>Customer Name:</strong> {order.customerName}</p>
                            <p><strong>Product ID:</strong> {order.productId}</p>
                            <p><strong>Category:</strong> {order.category}</p>
                            <p><strong>Quantity:</strong> {order.quantity}</p>
                            <p><strong>Price:</strong> ${order.price}</p>
                            <p><strong>Total Sales:</strong> ${order.totalSales}</p>
                            <p><strong>Order Status:</strong> {order.orderStatus}</p>
                            <button className="edit-button" onClick={() => setEditOrder(order)}>Edit Order</button>
                            <button className="delete-button" onClick={() => handleDeleteOrder(order.orderId)}>Delete Order</button>
                        </li>
                    ))}
                </ul>
            ) : (
                <p className="no-orders">No orders found.</p>
            )}

            {editOrder && (
                <div className="edit-order-popup">
                    <h2>Edit Order</h2>
                    <input 
                        type="text" 
                        value={editOrder.customerName} 
                        onChange={(e) => setEditOrder({ ...editOrder, customerName: e.target.value })} 
                        className="edit-input"
                    />
                    <input 
                        type="number" 
                        value={editOrder.quantity} 
                        onChange={(e) => setEditOrder({ ...editOrder, quantity: parseInt(e.target.value) })} 
                        className="edit-input"
                    />
                    <input 
                        type="number" 
                        value={editOrder.price} 
                        onChange={(e) => setEditOrder({ ...editOrder, price: parseFloat(e.target.value) })} 
                        className="edit-input"
                    />
                    <button className="update-button" onClick={() => handleUpdateOrder(editOrder.orderId)}>Update Order</button>
                    <button className="cancel-button" onClick={() => setEditOrder(null)}>Cancel</button>
                </div>
            )}

            {showSignUp && (
                <div className="signup-popup">
                    <div className="popup-content">
                        <h2>Create Account</h2>
                        <input
                            type="email"
                            placeholder="Email"
                            value={signUpData.email}
                            onChange={(e) => setSignUpData({ ...signUpData, email: e.target.value })}
                            className="signup-input"
                        />
                        <input
                            type="password"
                            placeholder="Password"
                            value={signUpData.password}
                            onChange={(e) => setSignUpData({ ...signUpData, password: e.target.value })}
                            className="signup-input"
                        />
                        <input
                            type="password"
                            placeholder="Confirm Password"
                            value={signUpData.confirmPassword}
                            onChange={(e) => setSignUpData({ ...signUpData, confirmPassword: e.target.value })}
                            className="signup-input"
                        />
                        <select
                            value={signUpData.type}
                            onChange={(e) => setSignUpData({ ...signUpData, type: e.target.value })}
                            className="signup-select"
                        >
                            <option value="customer">Customer</option>
                            <option value="salesman">Salesman</option>
                        </select>
                        <button className="signup-button" onClick={handleSignUp}>Add Customer</button>
                        <button className="cancel-button" onClick={() => setShowSignUp(false)}>Cancel</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default CustomersPage;
