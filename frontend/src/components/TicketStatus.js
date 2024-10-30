import React, { useState } from 'react';
import axios from 'axios';

const TicketStatus = () => {
    const [ticketNumber, setTicketNumber] = useState('');
    const [ticketDetails, setTicketDetails] = useState(null);
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setTicketDetails(null);

        try {
            const response = await axios.get(`http://localhost:8080/myservlet/ticketstatus?ticketNumber=${ticketNumber}`);
            setTicketDetails(response.data);
        } catch (error) {
            setError(error.response?.data?.error || 'An error occurred while fetching the ticket status.');
        }
    };

    return (
        <div className="container">
            <h1>Ticket Status</h1>
            <form onSubmit={handleSubmit}>
                <div className="form-group">
                    <label htmlFor="ticketNumber">Ticket Number:</label>
                    <input
                        type="text"
                        id="ticketNumber"
                        value={ticketNumber}
                        onChange={(e) => setTicketNumber(e.target.value)}
                        required
                        className="form-control"
                    />
                </div>
                <button type="submit" className="btn btn-primary">Check Status</button>
            </form>

            {error && <div className="alert alert-danger mt-3">{error}</div>}

            {ticketDetails && (
                <div className="mt-4">
                    <h2>Ticket Details</h2>
                    <table className="table">
                        <tbody>
                            <tr>
                                <th>Ticket Number</th>
                                <td>{ticketDetails.ticketNumber}</td>
                            </tr>
                            <tr>
                                <th>Order ID</th>
                                <td>{ticketDetails.orderId}</td>
                            </tr>
                            <tr>
                                <th>Description</th>
                                <td>{ticketDetails.description}</td>
                            </tr>
                            <tr>
                                <th>Response</th>
                                <td>{ticketDetails.response}</td>
                            </tr>
                            <tr>
                                <th>Date</th>
                                <td>{ticketDetails.date}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default TicketStatus;