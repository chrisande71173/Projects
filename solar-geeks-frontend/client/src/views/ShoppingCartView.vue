<template>
  <div class="shopping-cart">
    <h1>Your Shopping Cart</h1>
    <div v-if="loading">Loading...</div>
    <div v-else>
      <div v-if="cartItems.length">
        <ul>
          <li v-for="item in cartItems" :key="item.cartItemId">
            <img src="@/assets/350x250.jpg" alt="Product image" class="product-image" />
            <div class="item-info">
              <router-link :to="{ name: 'product-details', params: { id: item.productId } }">
              <h2 class="product-link">{{ item.productName }}</h2>
              </router-link>
              <p>Price: ${{ item.price }}</p>
              <p>Quantity: {{ item.quantity }}</p>
              <p>Total Price: ${{ item.totalPrice }}</p>
            </div>
            <button @click="removeItem(item.cartItemId)">Remove</button>
          </li>
        </ul>
        <div class="cart-summary">
          <p><strong>Subtotal:</strong> {{ subtotal }}</p>
          <p><strong>Tax Rate:</strong> {{ taxRate }}</p>
          <p><strong>Tax Amount:</strong> {{ taxAmount }}</p>
          <p><strong>Total:</strong> {{ total }}</p>
        </div>
        <div class="cart-actions">
          <button @click="clearCart">Clear Cart</button>
        </div>
      </div>
      <div v-else>
        <p>Your cart is empty.</p>
      </div>
    </div>
  </div>
</template>

<script>
import cartService from "@/services/cartService";

export default {
  data() {
    return {
      cartItems: [],
      subtotal: "$0.00",
      taxRate: "0.00%",
      taxAmount: "$0.00",
      total: "$0.00",
      loading: true,
    };
  },
  methods: {
    async fetchCart() {
      try {
        this.loading = true;
        const response = await cartService.getCart();
        this.cartItems = response.data.cartItems;
        this.subtotal = response.data.subtotal;
        this.taxRate = response.data.taxRate;
        this.taxAmount = response.data.taxAmount;
        this.total = response.data.total;
      } catch (error) {
        console.error("Error fetching cart:", error);
      } finally {
        this.loading = false;
      }
    },
    async removeItem(cartItemId) {
      try {
        await cartService.removeItemFromCart(cartItemId);
        this.cartItems = this.cartItems.filter(item => item.cartItemId !== cartItemId);
        this.fetchCart();
      } catch (error) {
        console.error("Error removing item:", error);
      }
    },
    async clearCart() {
      try {
        await cartService.clearCart();
        this.cartItems = [];
        this.subtotal = "$0.00";
        this.taxRate = "0.00%";
        this.taxAmount = "$0.00";
        this.total = "$0.00";
      } catch (error) {
        console.error("Error clearing cart:", error);
      }
    },
  },
  created() {
    this.fetchCart();
  },
};
</script>

<style scoped>

.shopping-cart {
  max-width: 800px;
  margin-inline: auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  border: solid 1px #ddd;
  margin-top: 280px;
  margin-bottom: 60px;
}
a {
  text-decoration: none;
}


.shopping-cart h1 {
  font-size: 28px;
  font-weight: bold;
  margin-bottom: 20px;
  text-align: center;
  color: #333;
}


.shopping-cart ul {
  list-style: none;
  padding: 0;
  margin: 0;
}


.shopping-cart li {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  padding: 15px;
  border: solid 1px #ddd;
  border-radius: 8px;
  background-color: #fff;
  box-shadow: 0px 2px 4px rgba(0, 0, 0, 0.05);
}


.product-image {
  width: 100px;
  height: 100px;
  object-fit: cover;
  border-radius: 5px;
  border: solid 1px #ddd;
}


.item-info {
  flex: 1;
}

.item-info h2 {
  font-size: 18px;
  margin: 0 0 5px;
  color: #007bff;
}

.item-info h2:hover {
  text-decoration: underline;
  color: #0056b3;
}

.item-info p {
  margin: 5px 0;
  color: #555;
  font-size: 14px;
}

/* Remove button */
.shopping-cart button {
  padding: 8px 12px;
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.shopping-cart button:hover {
  background-color: #660610;
}


.cart-summary {
  margin-top: 20px;
  padding: 15px;
  border: solid 1px #ddd;
  border-radius: 8px;
  background-color: #f8f9fa;
}

.cart-summary p {
  margin: 10px 0;
  font-size: 16px;
  color: #333;
}


.cart-actions {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.cart-actions button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.cart-actions button:hover {
  background-color: #0056b3;
}


.shopping-cart p {
  text-align: center;
  color: #555;
  font-size: 16px;
}


.shopping-cart div[loading] {
  text-align: center;
  font-size: 18px;
  color: #555;
  margin-top: 20px;
}
</style>