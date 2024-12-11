import axios from 'axios';

const cartService = {
    getCart() {
      return axios.get('/cart');
    },
    addItemToCart(item) {
      return axios.post('/cart/items', item);
    },
    removeItemFromCart(itemId) {
      return axios.delete(`/cart/items/${itemId}`);
    },
    clearCart() {
      return axios.delete('/cart');
    },
  };
  
  export default cartService;