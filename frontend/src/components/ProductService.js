const API_URL = 'http://localhost:8080/myservlet/products';

export const getProducts = async (category = '') => {
  const response = await fetch(`${API_URL}?category=${encodeURIComponent(category)}`);
  return await response.json();
};

export const addProduct = async (category, product) => {
  const response = await fetch(`${API_URL}?category=${encodeURIComponent(category)}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(product),
  });
  return await response.json();
};

export const updateProduct = async (category, productId, updatedProduct) => {
  const response = await fetch(`${API_URL}?category=${encodeURIComponent(category)}&id=${productId}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(updatedProduct),
  });
  return await response.json();
};

export const deleteProduct = async (category, productId) => {
  const response = await fetch(`${API_URL}?category=${category}&id=${productId}`, {
    method: 'DELETE',
  });
  return response.status === 204;
};
