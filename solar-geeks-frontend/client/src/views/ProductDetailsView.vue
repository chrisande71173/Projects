<template>
  <div v-if="product" class="product-details">
    <h1>{{ product.name }}</h1>
    <img src="@/assets/350x250.jpg" :alt="product.name" />
    <p>{{ product.description }}</p>
    <p>{{ product.productSku }}</p>
    <p><strong>Price:</strong> {{ '$' + product.price }}</p>
    <div class="button-group">
            <button v-if="isLoggedIn" @click="addToCart(product)">Add to Cart</button>
          </div>
      
    <router-link to="/products"><button>Back to Products</button></router-link>
  </div>
  <div v-else class="loading">
    <p>Loading product details...</p>
  </div>
</template>

<script>
import productService from "@/services/productService";
import cartService from "../services/CartService";

export default {
  data() {
    return {
      product: null,
    };
  },
  async created() {
    const productId = this.$route.params.id; // Get the product ID from the route
    try {
      const response = await productService.getProductById(productId);
      this.product = response.data; // Assuming API response contains product data
    } catch (error) {
      console.error("Failed to fetch product details:", error);
    }
  },
  computed: {
    isLoggedIn() {
      return this.$store.state.token.length > 0;
    },
    filteredProducts() {
      const query = this.searchQuery.toLowerCase();
      return this.products.filter((product) =>
        product.name.toLowerCase().includes(query)
      );
    },
  },
  methods: {
    async fetchProducts() {
      try {
        this.isLoading = true;
        const response = await productService.getAllProducts();
        this.products = response.data.map((product) => ({
          ...product,
          quantity: 1, // default quantity
        }));
      } catch (error) {
        console.error("Failed to fetch products:", error);
      } finally {
        this.isLoading = false;
      }
    },
    async addToCart(product) {
      try {
        const cartResponse = await cartService.getCart();
        const cartItems = cartResponse.data.cartItems;

        const existingItem = cartItems.find(
          (item) => item.productId === product.productId
        );

        const payload = {
          productId: product.productId,
          quantity: existingItem ? existingItem.quantity + 1 : 1, // Default to 1 if not in cart
      };

    console.log("Payload being sent:", payload);
      await cartService.addItemToCart(payload);
      alert(`${product.name} has been added to your cart!`);
      } catch (error) {
         console.error("Failed to add item to cart:", error);
          alert("An error occurred while adding the product to the cart.");
      }
    },
  },
};
</script>

<style scoped>

.product-details {
  max-width: 600px;
  margin-top: 280px;
  margin-bottom: 40px;
  margin-inline: auto;
  padding: 20px;
  background-color: #f9f9f9;
  border-radius: 10px;
  border: 1px solid #ddd;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
  text-align: center;
}


.product-details h1 {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}


.product-details img {
  width: 100%;
  max-width: 350px;
  height: auto;
  border-radius: 5px;
  border: 1px solid #ddd;
  margin-bottom: 20px;
  margin-inline: auto;
  align-items: center;
}


.product-details p {
  font-size: 16px;
  color: #555;
  margin: 10px 0;
  line-height: 1.6;
}


.product-details p strong {
  font-size: 18px;
  color: #000;
}


.product-details button {
  padding: 10px 20px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
  margin-top: 20px;
}

.product-details button:hover {
  background-color: #0056b3;
}


.loading {
  max-width: 400px;
  margin: 50px auto;
  text-align: center;
  font-size: 18px;
  color: #555;
}

.loading p {
  margin: 0;
}
</style>