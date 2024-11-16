import React from 'react';
import { BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import Login from './components/Login';
import Signup from './components/Signup';
import Dashboard from './components/Dashboard';
import Navbar from './components/Navbar';
import CategoryPage from './components/CategoryPage';
import ProductCategory from './components/ProductCategory';
import CartPage from './components/CartPage';
import CheckoutPage from './components/CheckoutPage';
import PaymentPage from './components/PaymentPage';
import OrderConfirmationPage from './components/OrderConfirmationPage';
import ProductList from './components/ProductList';
import CustomersPage from './components/CustomersPage';
import OrderPage from './components/OrderPage';
import Trending from './components/Trending';
import InventoryManager from './components/InventoryManager';
import SalesReport from './components/SalesReport';
import ProductDetails from './components/ProductDetails';
import SearchResults from './components/SearchResults';
import OpenTicket from './components/OpenTicket';
import TicketStatus from './components/TicketStatus';
import SemanticSearch from './components/SemanticSearch';
import ProductSearch from './components/ProductSearch';
import ProductDetail from './components/ProductDetail';
import './App.css';



const App = () => {
  return (
    <div className='app-background'>
    <Router>
       <Navbar />
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/category/:category" element={<ProductCategory />} />
        <Route path="/category/:category" element={<CategoryPage />} />
        <Route path="/cartpage" element={<CartPage />} />
        <Route path="/checkout" element={<CheckoutPage />} />
        <Route path="/payment" element={<PaymentPage />} />
        <Route path="/order-confirmation" element={<OrderConfirmationPage />} />
        <Route path="/productlist" element={<ProductList />} />
        <Route path="/customers" element={<CustomersPage />} />
        <Route path="/orders" element={<OrderPage />} />
        <Route path="/trending" element={<Trending />} />
        <Route path="/inventory" element={<InventoryManager />} />
        <Route path="/report" element={<SalesReport />} />
        <Route path="/product/:id" element={<ProductDetails />} />
        <Route path='/search' element={<SearchResults />}/>
        <Route path='/service' element={<OpenTicket />}/>
        <Route path='/ticketstatus' element={<TicketStatus />}/>
        <Route path='/semanticsearch' element={<SemanticSearch />}/>
        <Route path='/productsearch' element={<ProductSearch />}/>
        <Route path="/product/:productId" component={ProductDetail} />
      </Routes>
    </Router>
    </div>
  );
};

export default App;
