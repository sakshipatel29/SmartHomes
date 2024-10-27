import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Bar } from 'react-chartjs-2';

function SalesReport() {
    const [productSales, setProductSales] = useState([]);
    const [dailySales, setDailySales] = useState([]);

    useEffect(() => {
        fetchProductSales();
        fetchDailySales();
    }, []);

    const fetchProductSales = async () => {
        try {
            const response = await axios.get('http://localhost:8080/myservlet/salesReport?action=productSales');
            setProductSales(response.data);
        } catch (error) {
            console.error('Error fetching product sales', error);
        }
    };

    const fetchDailySales = async () => {
        try {
            const response = await axios.get('http://localhost:8080/myservlet/salesReport?action=dailySales');
            setDailySales(response.data);
        } catch (error) {
            console.error('Error fetching daily sales', error);
        }
    };

    const chartData = {
        labels: productSales.map(product => product.name),
        datasets: [{
            label: 'Total Sales',
            data: productSales.map(product => product.totalSales),
            backgroundColor: 'rgba(75, 192, 192, 0.6)' 
        }]
    };

    const chartOptions = {
        indexAxis: 'y',
        scales: {
            x: {
                title: {
                    display: true,
                    text: 'Total Sales'
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
        <div>
            <h2>Product Sales Report</h2>
            <table>
                <thead>
                    <tr>
                        <th>Product Name</th>
                        <th>Price</th>
                        <th>Items Sold</th>
                        <th>Total Sales</th>
                    </tr>
                </thead>
                <tbody>
                    {productSales.map(product => (
                        <tr key={product.name}>
                            <td>{product.name}</td>
                            <td>${product.price.toFixed(2)}</td>
                            <td>{product.totalSold}</td>
                            <td>${product.totalSales.toFixed(2)}</td>
                        </tr>
                    ))}
                </tbody>
            </table>

            <h2>Product Sales Chart</h2>
            <Bar data={chartData} options={chartOptions} /> 

            <h2>Daily Sales Report</h2>
            <table>
                <thead>
                    <tr>
                        <th>Date</th>
                        <th>Total Sales</th>
                    </tr>
                </thead>
                <tbody>
                    {dailySales.map(day => (
                        <tr key={day.date}>
                            <td>{day.date}</td>
                            <td>${day.totalSales.toFixed(2)}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

export default SalesReport;
