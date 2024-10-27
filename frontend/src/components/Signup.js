import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Signup.css';

const Signup = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [type, setType] = useState('');
    const [name, setName] = useState('');
    const [street, setStreet] = useState('');
    const [city, setCity] = useState('');
    const [state, setState] = useState('');
    const [zipcode, setZipcode] = useState('');
    const [message, setMessage] = useState('');

    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();

        if (password !== confirmPassword) {
            setMessage('Passwords do not match.');
            return;
        }

        const user = {
            email,
            password,
            type,
            name,
            street,
            city,
            state,
            zipcode
        };

        try {
            const response = await fetch('http://localhost:8080/myservlet/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(user)
            });

            if (response.ok) {
                const data = await response.json();
                setMessage(`Signup successful: ${data.message}`);
                setTimeout(() => {
                    navigate('/');
                }, 10);
            } else {
                const errorData = await response.json();
                setMessage(`Signup failed: ${errorData.error}`);
            }
        } catch (error) {
            setMessage(`An error occurred: ${error.message}`);
        }
    };

    return (
        <div className="signup-container">
            <h2 className="signup-title">Signup</h2>
            <form onSubmit={handleSubmit} className="signup-form">
                {/* {type === 'customer' && (
                    <> */}
                        <div className="form-group">
                            <label htmlFor="name" className="form-label">Name:</label>
                            <input
                                type="text"
                                id="name"
                                className="form-input"
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                required
                            />
                        </div>
                        <div className="form-group">
                    <label htmlFor="email" className="form-label">Email:</label>
                    <input
                        type="email"
                        id="email"
                        className="form-input"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password" className="form-label">Password:</label>
                    <input
                        type="password"
                        id="password"
                        className="form-input"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="confirmPassword" className="form-label">Confirm Password:</label>
                    <input
                        type="password"
                        id="confirmPassword"
                        className="form-input"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="type" className="form-label">Type:</label>
                    <select
                        id="type"
                        className="form-select"
                        value={type}
                        onChange={(e) => setType(e.target.value)}
                        required
                    >
                        <option value="" disabled>Select a type</option>
                        <option value="customer">Customer</option>
                        <option value="storeManager">Store Manager</option>
                        <option value="salesman">Salesman</option>
                    </select>
                </div>
                        <div className="form-group">
                            <label htmlFor="street" className="form-label">Street:</label>
                            <input
                                type="text"
                                id="street"
                                className="form-input"
                                value={street}
                                onChange={(e) => setStreet(e.target.value)}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="city" className="form-label">City:</label>
                            <input
                                type="text"
                                id="city"
                                className="form-input"
                                value={city}
                                onChange={(e) => setCity(e.target.value)}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="state" className="form-label">State:</label>
                            <input
                                type="text"
                                id="state"
                                className="form-input"
                                value={state}
                                onChange={(e) => setState(e.target.value)}
                                required
                            />
                        </div>
                        <div className="form-group">
                            <label htmlFor="zipcode" className="form-label">Zip Code:</label>
                            <input
                                type="text"
                                id="zipcode"
                                className="form-input"
                                value={zipcode}
                                onChange={(e) => setZipcode(e.target.value)}
                                required
                            />
                        </div>
                    {/* </>
                )} */}
                
                <div className='sig'>
                    <button type="submit" className="signup-button">Signup</button>
                </div>
            </form>
            {message && <p className="message">{message}</p>}
        </div>
    );
};

export default Signup;