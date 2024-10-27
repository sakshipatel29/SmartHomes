import React, { useState, useEffect } from "react";
import axios from "axios";
import { Bar } from "react-chartjs-2";
import { Chart as ChartJS, CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend } from 'chart.js';
import './InventoryManager.css';

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

function InventoryManager() {
    const [allProducts, setAllProducts] = useState([]);
    const [onSaleProducts, setOnSaleProducts] = useState([]);
    const [rebateProducts, setRebateProducts] = useState([]);
    const [barChartData, setBarChartData] = useState({});

    useEffect(() => {
        fetchAllProducts();
        fetchOnSaleProducts();
        fetchRebateProducts();
    }, []);

    const fetchAllProducts = async () => {
        try {
            const response = await axios.get("http://localhost:8080/myservlet/inventory?action=allProducts");
            setAllProducts(response.data);
            updateBarChartData(response.data);
        } catch (error) {
            console.error("Error fetching all products", error);
        }
    };

    const fetchOnSaleProducts = async () => {
        try {
            const response = await axios.get("http://localhost:8080/myservlet/inventory?action=onSale");
            setOnSaleProducts(response.data);
        } catch (error) {
            console.error("Error fetching on-sale products", error);
        }
    };

    const fetchRebateProducts = async () => {
        try {
            const response = await axios.get("http://localhost:8080/myservlet/inventory?action=rebates");
            setRebateProducts(response.data);
        } catch (error) {
            console.error("Error fetching rebate products", error);
        }
    };

    const updateBarChartData = (products) => {
        const chartData = {
            labels: products.map(product => product.name),
            datasets: [{
                label: "Available Quantity",
                data: products.map(product => product.available_quantity),
                backgroundColor: "rgba(75, 192, 192, 0.6)"
            }]
        };
        setBarChartData(chartData);
    };
    const chartOptions = {
        indexAxis: 'y',
        scales: {
            x: {
                beginAtZero: true,
                max: 10,
                ticks: {
                    stepSize: 1
                }
            },
            y: {
                title: {
                    display: true,
                    text: 'Products'
                }
            }
        }
    };

    return (
        <div className="inventory-manager">
            <h2>All Products</h2>
            {allProducts.length > 0 ? (
                <table>
                    <thead>
                        <tr>
                            <th>Product Name</th>
                            <th>Price</th>
                            <th>Available Quantity</th>
                        </tr>
                    </thead>
                    <tbody>
                        {allProducts.map(product => (
                            <tr key={product.id}>
                                <td>{product.name}</td>
                                <td>${product.price.toFixed(2)}</td>
                                <td>{product.available_quantity}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <p>No products available</p>
            )}
            <div className="chart-container">
            <h2>Inventory Chart</h2>
            {Object.keys(barChartData).length > 0 && <Bar data={barChartData} options={chartOptions} />}
            </div>
            <h2>Products on Sale</h2>
            {onSaleProducts.length > 0 ? (
                <table>
                    <thead>
                        <tr>
                            <th>Product Name</th>
                            <th>Price</th>
                            <th>Available Quantity</th>
                        </tr>
                    </thead>
                    <tbody>
                        {onSaleProducts.map(product => (
                            <tr key={product.id}>
                                <td>{product.name}</td>
                                <td>${product.price.toFixed(2)}</td>
                                <td>{product.available_quantity}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <p>No products on sale</p>
            )}

            <h2>Products with Rebates</h2>
            {rebateProducts.length > 0 ? (
                <table>
                    <thead>
                        <tr>
                            <th>Product Name</th>
                            <th>Price</th>
                            <th>Available Quantity</th>
                        </tr>
                    </thead>
                    <tbody>
                        {rebateProducts.map(product => (
                            <tr key={product.id}>
                                <td>{product.name}</td>
                                <td>${product.price.toFixed(2)}</td>
                                <td>{product.available_quantity}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            ) : (
                <p>No products with rebates</p>
            )}
        </div>
    );
}

export default InventoryManager;