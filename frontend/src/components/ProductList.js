import React, { useEffect, useState } from 'react';
import { getProducts, addProduct, updateProduct, deleteProduct } from './ProductService';
import './ProductList.css';

const ProductList = () => {
  const [products, setProducts] = useState([]);
  const [category, setCategory] = useState('Smart-Doorbells');
  const [newProduct, setNewProduct] = useState({ id: '', name: '', price: '', description: '' });
  const [editProduct, setEditProduct] = useState(null);

  useEffect(() => {
    loadProducts();
  }, [category]);

  const loadProducts = async () => {
    const data = await getProducts(category);
    setProducts(data);
  };

  const handleAddProduct = async () => {
    await addProduct(category, newProduct);
    setNewProduct({ id: '', name: '', price: '', description: '' });
    loadProducts();
  };

  const handleUpdateProduct = async () => {
    if (editProduct) {
      await updateProduct(category, editProduct.id, editProduct);
      setEditProduct(null);
      loadProducts();
    }
  };

  const handleDeleteProduct = async (productId) => {
    await deleteProduct(category, productId);
    loadProducts();
  };

  return (
    <div className="product-list-container">
      <h1 className="product-list-title">Products in {category}</h1>
      <select className="category-select" value={category} onChange={(e) => setCategory(e.target.value)}>
        <option value="Smart-Doorbells">Smart Doorbells</option>
        <option value="Smart-Doorlocks">Smart Doorlocks</option>
        <option value="Smart-Speakers">Smart Speakers</option>
        <option value="Smart-Lightings">Smart Lightings</option>
        <option value="Smart-Thermostats">Smart Thermostats</option>
      </select>

      <div className="product-form">
        <h2 className="form-title">Add New Product</h2>
        <input
          className="form-input"
          type="text"
          placeholder="ID"
          value={newProduct.id}
          onChange={(e) => setNewProduct({ ...newProduct, id: e.target.value })}
        />
        <input
          className="form-input"
          type="text"
          placeholder="Name"
          value={newProduct.name}
          onChange={(e) => setNewProduct({ ...newProduct, name: e.target.value })}
        />
        <input
          className="form-input"
          type="text"
          placeholder="Price"
          value={newProduct.price}
          onChange={(e) => setNewProduct({ ...newProduct, price: e.target.value })}
        />
        <input
          className="form-input"
          type="text"
          placeholder="Description"
          value={newProduct.description}
          onChange={(e) => setNewProduct({ ...newProduct, description: e.target.value })}
        />
        <button className="form-button" onClick={handleAddProduct}>Add Product</button>
      </div>

      <div className="product-form">
        <h2 className="form-title">Edit Product</h2>
        {editProduct && (
          <>
            <input
              className="form-input"
              type="text"
              placeholder="Name"
              value={editProduct.name}
              onChange={(e) => setEditProduct({ ...editProduct, name: e.target.value })}
            />
            <input
              className="form-input"
              type="text"
              placeholder="Price"
              value={editProduct.price}
              onChange={(e) => setEditProduct({ ...editProduct, price: e.target.value })}
            />
            <input
              className="form-input"
              type="text"
              placeholder="Description"
              value={editProduct.description}
              onChange={(e) => setEditProduct({ ...editProduct, description: e.target.value })}
            />
            <button className="form-button" onClick={handleUpdateProduct}>Update Product</button>
          </>
        )}
      </div>

      <h2 className="product-list-title">Product List</h2>
      <ul className="product-list">
        {products.map((product) => (
          <li className="product-item" key={product.id}>
            {product.name} - ${product.price}
            <button className="product-button" onClick={() => setEditProduct(product)}>Edit</button>
            <button className="product-button" onClick={() => handleDeleteProduct(product.id)}>Delete</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default ProductList;

