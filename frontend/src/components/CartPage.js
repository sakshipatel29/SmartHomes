import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './CartPage.css';

const CartPage = () => {
    const [cart, setCart] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const userEmail = localStorage.getItem('email');
    const navigate = useNavigate();

    useEffect(() => {
        if (!userEmail) {
            setError('User email is not set.');
            setLoading(false);
            return;
        }
        fetchCart();
    }, [userEmail]);

    const fetchCart = async () => {
        try {
            const response = await fetch(`http://localhost:8080/myservlet/cart?email=${encodeURIComponent(userEmail)}`);
            if (!response.ok) {
                throw new Error('Failed to fetch cart data');
            }
            const data = await response.json();
            setCart(data);
            setLoading(false);
        } catch (err) {
            setError(err.message);
            setLoading(false);
        }
    };

    const updateQuantity = async (productId, quantity, isAccessory = false) => {
        try {
            const response = await fetch('http://localhost:8080/myservlet/cart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    action: quantity === 0 ? 'remove' : 'update',
                    email: userEmail,
                    productId: productId,
                    quantity: quantity,
                    isAccessory: isAccessory,
                }),
            });

            if (!response.ok) {
                throw new Error('Failed to update cart');
            }

            const updatedCart = await response.json();
            setCart(updatedCart);
        } catch (err) {
            setError(err.message);
        }
    };

    const removeFromCart = async (productId, isAccessory = false) => {
        try {
            const response = await fetch('http://localhost:8080/myservlet/cart', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    action: 'remove',
                    email: userEmail,
                    productId: productId,
                    isAccessory: isAccessory,
                }),
            });

            if (!response.ok) {
                throw new Error('Failed to remove item from cart');
            }

            const updatedCart = await response.json();
            setCart(updatedCart);
        } catch (err) {
            setError(err.message);
        }
    };

    const calculateTotal = () => {
        return cart.reduce((total, item) => {
            const itemTotal = item.productPrice * item.quantity;
            const accessoriesTotal = (item.accessories || []).reduce((acc, accessory) => 
                acc + (accessory.accessoryPrice * accessory.quantity), 0);
            return total + itemTotal + accessoriesTotal;
        }, 0);
    };

    const handleCheckout = () => {
        navigate('/checkout');
    };

    if (loading) return <p>Loading...</p>;
    if (error) return <p>Error: {error}</p>;

    return (
        <div className="cart-container">
            <h1>Your Cart</h1>
            {cart.length === 0 ? (
                <p>Your cart is empty.</p>
            ) : (
                <>
                    <ul className="cart-items">
                        {cart.map(item => (
                            <li key={`${item.productId}_${item.isAccessory}`} className="cart-item">
                                <h2 className="product-name">{item.productName}</h2>
                                <p className="product-price">Price: ${item.productPrice.toFixed(2)}</p>
                                <p className="product-category">Category: {item.category}</p>
                                <div className="quantity-controls">
                                    <button onClick={() => updateQuantity(item.productId, item.quantity - 1, item.isAccessory)}>-</button>
                                    <span>{item.quantity}</span>
                                    <button onClick={() => updateQuantity(item.productId, item.quantity + 1, item.isAccessory)}>+</button>
                                </div>
                                <button className="remove-button" onClick={() => removeFromCart(item.productId, item.isAccessory)}>Remove</button>
                                
                                {item.accessories && item.accessories.length > 0 && (
                                    <div className="accessories">
                                        <h3>Accessories:</h3>
                                        <ul>
                                            {item.accessories.map((accessory, index) => (
                                                <li key={index}>
                                                    {accessory.accessoryName} - ${accessory.accessoryPrice.toFixed(2)}
                                                    <div className="quantity-controls">
                                                        <button onClick={() => updateQuantity(accessory.id, accessory.quantity - 1, true)}>-</button>
                                                        <span>{accessory.quantity}</span>
                                                        <button onClick={() => updateQuantity(accessory.id, accessory.quantity + 1, true)}>+</button>
                                                    </div>
                                                    <button className="remove-button" onClick={() => removeFromCart(accessory.id, true)}>Remove</button>
                                                </li>
                                            ))}
                                        </ul>
                                    </div>
                                )}
                            </li>
                        ))}
                    </ul>
                    <h2>Total: ${calculateTotal().toFixed(2)}</h2>
                    <button className="checkout-button" onClick={handleCheckout}>
                        Continue to Checkout
                    </button>
                </>
            )}
        </div>
    );
};

export default CartPage;