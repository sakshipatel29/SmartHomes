import React, { useState } from 'react';

const ProfileButton = () => {
    const [showPopup, setShowPopup] = useState(false);

    const email = localStorage.getItem('email');
    const type = localStorage.getItem('type');
    console.log(type);

    const togglePopup = () => {
        setShowPopup(!showPopup);
    };

    return (
        <div style={{ position: 'relative' }}>
            <button className="buttons" onClick={togglePopup}>
                Profile
            </button>

            {showPopup && (
                <div style={popupStyle}>
                    <p><strong>Email:</strong> {email}</p>
                    <p><strong>Type:</strong> {type}</p>
                </div>
            )}
        </div>
    );
};

const popupStyle = {
    position: 'absolute',
    top: '80px',
    height: 100,
    width: 150,
    left: '0',
    backgroundColor: 'white',
    border: '1px solid #ccc',
    padding: '10px',
    borderRadius: '5px',
    boxShadow: '0px 4px 8px rgba(0, 0, 0, 0.1)',
    zIndex: '1000',
};

export default ProfileButton;
