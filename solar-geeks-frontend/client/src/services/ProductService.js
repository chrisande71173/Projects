import axios from 'axios';

const productService = {
    getAllProducts() {
      return axios.get('/products');
    },
    getProductById(productId) {
      return axios.get(`/products/${productId}`);
    },
    findProductsBySKU(sku) {
      return axios.get('/products', { params: { sku } });
    },
    findProductsByName(name) {
      return axios.get('/products', { params: { name } });
    },
  };
  
  export default productService;