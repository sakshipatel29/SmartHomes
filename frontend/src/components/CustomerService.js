// import React, { useState } from 'react';
// import axios from 'axios';

// const CustomerService = () => {
//   const [orderId, setOrderId] = useState('');
//   const [description, setDescription] = useState('');
//   const [selectedFile, setSelectedFile] = useState(null);
//   const [ticketNumber, setTicketNumber] = useState('');
//   const [view, setView] = useState('menu');

//   const convertImageToBase64 = (file) => {
//     return new Promise((resolve, reject) => {
//       const reader = new FileReader();
//       reader.readAsDataURL(file);
//       reader.onloadend = () => resolve(reader.result);
//       reader.onerror = (error) => reject(error);
//     });
//   };

//   const handleOpenTicket = async () => {
//     try {
//       // Check if orderId and description are provided
//       if (!orderId || !description || !selectedFile) {
//         alert("Please fill in all fields and select an image.");
//         return;
//       }
      
//       // Convert the selected file to Base64
//       const base64Image = await convertImageToBase64(selectedFile);
  
//       // Prepare the payload according to servlet expectations
//       const payload = {
//         orderId,
//         description,
//         image: base64Image.split(',')[1], // Extract base64 string from data URL
//       };
  
//       // Make the POST request to your servlet
//       const response = await axios.post('http://localhost:8080/myservlet/ticket', payload, {
//         headers: {
//           'Content-Type': 'application/json',
//         },
//       });
  
//       console.log(response.data); // Log the entire response for debugging
  
//       // Check if the response is successful
//       if (response.data.includes("Ticket created successfully")) {
//         // Extract ticket number from the response
//         const ticketNum = response.data.split("Ticket Number: ")[1];
//         setTicketNumber(ticketNum);
//         setView('ticketOpened');
//       } else {
//         alert(response.data); // Show the response message if there's an error
//       }
//     } catch (error) {
//       console.error('Error opening ticket:', error);
//       alert(error.response?.data || "An error occurred while opening the ticket. Please try again.");
//     }
//   };

//   return (
//     <div>
//       <h2>Customer Service</h2>
//       {view === 'menu' && (
//         <div>
//           <input 
//             type="text" 
//             placeholder="Order ID" 
//             value={orderId} 
//             onChange={(e) => setOrderId(e.target.value)} 
//           />
//           <textarea 
//             placeholder="Description" 
//             value={description} 
//             onChange={(e) => setDescription(e.target.value)} 
//           />
//           <input 
//             type="file" 
//             accept="image/*" 
//             onChange={(e) => setSelectedFile(e.target.files[0])} 
//           />
//           <button onClick={handleOpenTicket}>Submit Ticket</button>
//         </div>
//       )}
//       {view === 'ticketOpened' && (
//         <div>
//           <p>Your ticket number is: {ticketNumber}</p>
//           <button onClick={() => setView('menu')}>Back to Menu</button>
//         </div>
//       )}
//     </div>
//   );
// };

// export default CustomerService;




import React, { useState } from 'react';
import axios from 'axios';

const CustomerService = () => {
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

export default CustomerService;
