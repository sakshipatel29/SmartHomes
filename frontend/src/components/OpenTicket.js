
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
        formData.append('image', selectedFile);
      }
  
      const response = await axios.post('http://localhost:8080/myservlet/ticket', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
  
      console.log('Server response:', response.data);

      const responseText = response.data;
      const ticketNumberMatch = responseText.match(/Ticket Number: (TICKET-\d+)/);
  
      if (ticketNumberMatch) {
        const extractedTicketNumber = ticketNumberMatch[1];
        setTicketNumber(extractedTicketNumber);
        setView('ticketOpened');
        alert(`Ticket created successfully. Ticket Number: ${extractedTicketNumber}`);
      } else {
        alert('Ticket created, but no ticket number found in the response.');
      }
    } catch (error) {
      console.error('Error opening ticket:', error);
      alert(error.response?.data || "An error occurred while opening the ticket. Please try again.");
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



