import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Navbar.css";
import ProfileButton from "./Profilebutton";
import HomeButton from "./HomeButton";
import { useLocation } from 'react-router-dom';

const Navbar = () => {
  const [type, setType] = useState(null);
  const [showServiceDropdown, setShowServiceDropdown] = useState(false);
  const navigate = useNavigate();
  const storedUserType = localStorage.getItem("type");
  const location = useLocation();

  useEffect(() => {
    setType(storedUserType);
  }, []);


  if (location.pathname === '/' || location.pathname === '/signup') {
    return null;
  }

  return (
    <div className="navbar">
    <li>
            <button
            className="buttons"
            onClick={() => {
                navigate("/trending");
            }}
            >
            Trending
            </button>
        </li>
      <div className="dashbord">
        <Link to="/dashboard">Smart Homes</Link>
      </div>
      <div className="rightPanel">
        <ProfileButton />
        <HomeButton />
        {type === "storeManager" && (
            <button className="buttons"
            onClick={() => {
                navigate("/productlist");
            }}
            >
            Manage Items
            </button>
        )}
        {type === "storeManager" && (
            <button className="buttons"
            onClick={() => {
                navigate("/inventory");
            }}
            >
            Inventory
            </button>
        )}
        {type === "storeManager" && (
            <button className="buttons"
            onClick={() => {
                navigate("/report");
            }}
            >
            Sales Report
            </button>
        )}
        {type === "salesman" && (
            <button className="buttons">
            <Link to="/customers">Customers</Link>
            </button>
        )}
        {type === "customer" && (
            <button className="buttons">
            <Link to="/orders">Orders</Link>
            </button>
        )}
        <li className="dropdown">
        <button
          className="buttons dropdown-toggle"
          onClick={() => setShowServiceDropdown(!showServiceDropdown)}
        >
          Customer Service
        </button>
        {showServiceDropdown && (
          <ul className="dropdown-menu">
            <li>
              <button onClick={() => navigate("/service")}>
                Open a Ticket
              </button>
            </li>
            <li>
              <button onClick={() => navigate("/ticketstatus")}>
                Status of Ticket
              </button>
            </li>
          </ul>
        )}
      </li>
      <li>
            <button
            className="buttons"
            onClick={() => {
                navigate("/cartpage");
            }}
            >
            Cart
            </button>
        </li>
        <li>
            <button
            className="buttons"
            onClick={() => {
                navigate("/productsearch");
            }}
            >
            Product Search
            </button>
        </li>
        <li>
            <button
            className="buttons"
            onClick={() => {
                navigate("/semanticsearch");
            }}
            >
            Review Search
            </button>
        </li>
        <li>
            <button
            className="buttons"
            onClick={() => {
                localStorage.clear();
                navigate("/");
            }}
            >
            Logout
            </button>
        </li>

      </div>
    </div>
  );
};

export default Navbar;
