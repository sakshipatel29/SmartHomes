import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './Login.css';

export const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [type, setType] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        console.log('Submitting:', { email, password, type });

        const user = {
            email,
            password,
            type
        };

        try {
            const response = await fetch('http://localhost:8080/myservlet/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(user)
            });
            if (response.ok) {
                const data = await response.json();
                setMessage(`Login successful: ${data.message}`);
                localStorage.setItem('user', JSON.stringify({ email, type }));
                localStorage.setItem("email", email);
                localStorage.setItem("type", user.type);

                navigate('/dashboard');
            } else {
                const errorData = await response.json();
                setMessage(`Login failed: ${errorData.error}`);
            }
        } catch (error) {
            setMessage(`An error occurred: ${error.message}`);
        }
    };

    const handleSignUpClick = () => {
        navigate('/signup');
    };

    return (
        <div className="login-container">
            <h2 className="login-title">Login</h2>
            <form onSubmit={handleSubmit} className="login-form">
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
                <div className='login'>
                    <button type="submit" className="submit-button">Login</button>
                </div>
            </form>
            {message && <p className="message">{message}</p>}
            <div className="signup-container">
                <p>Don't have an account?</p>
                <button onClick={handleSignUpClick} className="signup-button">
                    Sign Up
                </button>
            </div>
        </div>
    );
};

export default Login;
