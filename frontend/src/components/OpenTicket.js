
import React, { useState } from 'react';
import axios from 'axios';

const OpenTicket = () => {
  const [orderId, setOrderId] = useState('');
  const [description, setDescription] = useState('');
  const [selectedFile, setSelectedFile] = useState(null);
  const [ticketNumber, setTicketNumber] = useState('');
  const [view, setView] = useState('menu');

  const handleOpenTicket = async () => {
    try {
      const formData = new FormData();
      formData.append('orderId', orderId);
      formData.append('description', description);
      if (selectedFile) {
        formData.append('image', selectedFile); // Append the file
      }

      const response = await axios.post('http://localhost:8080/myservlet/ticket', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      console.log(response.data); // Log the entire response for debugging

      if (response.data.status === 'success') {
        setTicketNumber(response.data.message); // This should be your ticket number
        setView('ticketOpened');
      } else {
        alert(response.data.message || "An error occurred."); // Check if message is defined
      }
    } catch (error) {
      console.error('Error opening ticket:', error);
      alert(error.response?.data?.message || "An error occurred while opening the ticket. Please try again.");
    }
  };

  return (
    <div>
      <h2>Customer Service</h2>
      {view === 'menu' && (
        <div>
          <input 
            type="text" 
            placeholder="Order ID" 
            value={orderId} 
            onChange={(e) => setOrderId(e.target.value)} 
          />
          <textarea 
            placeholder="Description" 
            value={description} 
            onChange={(e) => setDescription(e.target.value)} 
          />
          <input 
            type="file" 
            accept="image/*" 
            onChange={(e) => setSelectedFile(e.target.files[0])} 
          />
          <button onClick={handleOpenTicket}>Submit Ticket</button>
        </div>
      )}
      {view === 'ticketOpened' && (
        <div>
          <p>Your ticket number is: {ticketNumber}</p>
          <button onClick={() => setView('menu')}>Back to Menu</button>
        </div>
      )}
    </div>
  );
};

export default OpenTicket;
