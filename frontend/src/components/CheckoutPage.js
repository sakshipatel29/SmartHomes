
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './CheckoutPage.css';

const CheckoutPage = () => {
  const [email, setEmail] = useState('');
  const [name, setName] = useState('');
  const [address, setAddress] = useState({
    street: '',
    city: '',
    state: '',
    zipCode: ''
  });
  const [creditCard, setCreditCard] = useState('');
  const [deliveryMethod, setDeliveryMethod] = useState('homeDelivery');
  const [storeLocation, setStoreLocation] = useState('');
  const [cartItems, setCartItems] = useState([]);
  const [message, setMessage] = useState('');
  const [isPopupVisible, setPopupVisible] = useState(false);
  const navigate = useNavigate();

  const storeLocations = [
    { id: 1, name: 'Store A', zipCode: '10001' },
    { id: 2, name: 'Store B', zipCode: '10002' },
    { id: 3, name: 'Store C', zipCode: '10003' },
    { id: 4, name: 'Store D', zipCode: '10004' },
    { id: 5, name: 'Store E', zipCode: '10005' },
    { id: 6, name: 'Store F', zipCode: '10006' },
    { id: 7, name: 'Store G', zipCode: '10007' },
    { id: 8, name: 'Store H', zipCode: '10008' },
    { id: 9, name: 'Store I', zipCode: '10009' },
    { id: 10, name: 'Store J', zipCode: '10010' }
  ];

  useEffect(() => {
    const userEmail = localStorage.getItem('email');
    axios.get(`http://localhost:8080/myservlet/cart?email=${encodeURIComponent(userEmail)}`)
      .then((response) => {
        setCartItems(response.data || []);
      })
      .catch((error) => {
        console.error('Error fetching cart data', error);
        setCartItems([]);
      });
  }, []);

  const handleAddressChange = (e, field) => {
    setAddress({ ...address, [field]: e.target.value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const orderItems = cartItems.map(item => ({
      productId: item.productId,
      category: item.category,
      quantity: item.quantity,
      price: item.productPrice,
      discount: 0 
    }));

    const data = {
      user_id: email, 
      customerName: name,
      customerAddress: `${address.street}, ${address.city}, ${address.state} ${address.zipCode}`,
      creditCardNumber: creditCard,
      items: orderItems,
      shippingCost: 0, 
      storeId: deliveryMethod === 'instorePickup' ? storeLocations.find(store => store.name === storeLocation)?.id : 0
    };

    axios.post('http://localhost:8080/myservlet/order', data)
      .then((response) => {
        console.log(response.data); 
        setPopupVisible(true);
      })
      .catch((error) => {
        console.error('There was an error processing the order!', error);
        setMessage('Failed to place the order. Please try again.');
      });
  };

  const handleClosePopup = () => {
    setPopupVisible(false);
    navigate('/orders');
  };

  return (
    <div className="checkout-container">
      <h2>Place Your Order</h2>
      <form onSubmit={handleSubmit}>
        <h3>Customer Information</h3>
        <div className="form-group">
          <label>Name:</label>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            required
          />
        </div>
        <div className="form-group">
          <label>Email:</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
          />
        </div>

        {deliveryMethod === 'homeDelivery' && (
          <div>
            <h3>Delivery Address</h3>
            <div className="form-group">
              <label>Street:</label>
              <input
                type="text"
                value={address.street}
                onChange={(e) => handleAddressChange(e, 'street')}
                required
              />
            </div>
            <div className="form-group">
              <label>City:</label>
              <input
                type="text"
                value={address.city}
                onChange={(e) => handleAddressChange(e, 'city')}
                required
              />
            </div>
            <div className="form-group">
              <label>State:</label>
              <input
                type="text"
                value={address.state}
                onChange={(e) => handleAddressChange(e, 'state')}
                required
              />
            </div>
            <div className="form-group">
              <label>Zip Code:</label>
              <input
                type="text"
                value={address.zipCode}
                onChange={(e) => handleAddressChange(e, 'zipCode')}
                required
              />
            </div>
          </div>
        )}

        <h3>Payment Information</h3>
        <div className="form-group">
          <label>Credit Card:</label>
          <input
            type="text"
            value={creditCard}
            onChange={(e) => setCreditCard(e.target.value)}
            required
          />
        </div>

        <h3>Delivery Method</h3>
        <div>
          <label>
            <input
              type="radio"
              value="homeDelivery"
              checked={deliveryMethod === 'homeDelivery'}
              onChange={(e) => setDeliveryMethod(e.target.value)}
            />
            Home Delivery
          </label>
          <label>
            <input
              type="radio"
              value="instorePickup"
              checked={deliveryMethod === 'instorePickup'}
              onChange={(e) => setDeliveryMethod(e.target.value)}
            />
            In-store Pickup
          </label>
        </div>

        {deliveryMethod === 'instorePickup' && (
          <div className="form-group">
            <label>Select Store Location:</label>
            <select value={storeLocation} onChange={(e) => setStoreLocation(e.target.value)} required>
              <option value="">Select a store</option>
              {storeLocations.map((store) => (
                <option key={store.id} value={store.name}>
                  {store.name} ({store.zipCode})
                </option>
              ))}
            </select>
          </div>
        )}

        <h3>Your Cart</h3>
        <div className="cart-items-container">
          {cartItems.length === 0 ? (
            <p>Your cart is empty.</p>
          ) : (
            cartItems.map((item, index) => (
              <div className="cart-item" key={index}>
                <p>
                  {item.productName} = ${item.productPrice}
                </p>
              </div>
            ))
          )}
        </div>

        <button type="submit" className="submit-btn" disabled={cartItems.length === 0}>
          Submit Order
        </button>
      </form>

      {isPopupVisible && (
        <div className="popup-container">
          <div className="popup-content">
            <h2>Order Confirmation</h2>
            <p>Your Order is placed! Thank you for your purchase.</p>
            <button onClick={handleClosePopup}>Close</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default CheckoutPage;
