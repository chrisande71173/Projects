<template>
  <div class="home">
    <!-- Search Bar -->
    <div class="search-container">
      <input
        type="text"
        v-model="searchQuery"
        placeholder="Search for products..."
        class="search-bar"
      />
    </div>

    <div id="heading-line">
      
    </div>
    <p id="login-message" v-if="!isLoggedIn">
      Welcome! You may browse anonymously as much as you wish,<br />
      but you must
      <router-link v-bind:to="{ name: 'login' }">Login</router-link> to add
      items to your shopping cart.
    </p>
      <h1>
        <loading-spinner id="spinner" v-bind:spin="isLoading" />
      </h1>
    <!-- Centered icons for view toggle -->
    <div class="view-icons">
      <font-awesome-icon
        v-bind:class="{ active: cardView }"
        v-on:click="cardView = true"
        icon="fa-solid fa-grip"
        title="View tiles"
      />
      <font-awesome-icon
        v-bind:class="{ active: !cardView }"
        v-on:click="cardView = false"
        icon="fa-solid fa-table"
        title="View table"
      />
    </div>

    <div v-if="filteredProducts.length === 0 && !isLoading">
      <p>No products available.</p>
    </div>

        <!-- Table View -->
    <table v-if="!cardView">
      <thead>
        <tr>
          <th>Name</th>
          <th>SKU</th>
          <th>Price</th>
          <th v-if="isLoggedIn" >Quantity</th>
          <th v-if="isLoggedIn" >Action</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="product in filteredProducts" :key="product.id">
          <td>
            <router-link :to="{ name: 'product-details', params: { id: product.productId } }" class="name-link">
              {{ product.name }}
            </router-link>
          </td>
          <td>{{ product.productSku }}</td>
          <td>{{ '$' + product.price }}</td>
          <td>
            <select v-if="isLoggedIn" v-model="product.quantity" :key="product.id">
              <option v-for="n in 10" :key="n" :value="n">{{ n }}</option>
            </select>
          </td>
          <td>
            <button v-if="isLoggedIn" @click="addToCart(product)">Add to Cart</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Tile View -->
    <div v-if="cardView" class="product-tiles">
      <div v-for="product in filteredProducts" :key="product.id" class="product-card">
        <img src="@/assets/350x250.jpg" :alt="product.name" class="product-image" />
        <div class="product-info">
          <h3 id="product-tile-link">
            <router-link :to="{ name: 'product-details', params: { id: product.productId } }">
              {{ product.name }}
            </router-link>
          </h3>
          <div> {{ product.productSku }}</div>
          <p>{{ '$' + product.price }}</p>
          <select v-if="isLoggedIn" v-model="product.quantity" :key="product.id">
            <option v-for="n in 10" :key="n" :value="n">{{ n }}</option>
          </select>
          <div class="button-group">
            <button v-if="isLoggedIn" @click="addToCart(product)">Add to Cart</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import LoadingSpinner from "../components/LoadingSpinner.vue";
import productService from "@/services/productService";
import cartService from "@/services/cartService";

export default {
  components: {
    LoadingSpinner,
  },
  data() {
    return {
      isLoading: true,
      cardView: true,
      products: [],
      searchQuery: "", 
    };
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
          quantity: 1, 
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
          quantity: existingItem
            ? existingItem.quantity + product.quantity
            : product.quantity,
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
  created() {
    this.fetchProducts();
  },
};
</script>

<style scoped>
.home {
  margin-top: 280px;
  margin-bottom: 40px;
  text-align: center; 
}

#spinner {
  color: #007bff;
}

.view-icons {
  display: flex;
  justify-content: center;
  gap: 5px;
}

.view-icons .active {
  background-color: #007bff;
  color: #fff;
  padding: 5px; 
}

.view-icons > * {
  font-size: 1.5rem;
  cursor: pointer;
  color: #333;
  padding: 5px;
  border-radius: 10%;
}

.view-icons > *:hover {
  background-color: rgba(0, 0, 255, 0.1);
  padding: 5px;
}

.product-tiles {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  justify-content: center;
  padding: 20px;
}

.product-card {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  width: 230px;
  text-align: center;
  padding: 15px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
  text-decoration: none;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.product-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
  margin-bottom: 10px;
}

.product-info {
  font-size: 1rem;
  font-weight: 600;
  margin: 0;
  color: #333;
}


.button-group {
  display: flex;
  justify-content: center;
  gap: 10px;
  margin-top: 10px;
}

.view-details {
  text-decoration: none;
  color: #fff;
  background-color: #007bff;
  padding: 8px 16px;
  border-radius: 5px;
  transition: background-color 0.3s ease;
}

button {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

button:hover {
  background-color: #0056b3;
}

table {
  width: 100%;
  border-collapse: collapse;
  margin: 20px 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

th, td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}


#login-message {
  font-size: 1.2rem; 
  color: #333; 
  text-align: center; 
  margin: 5px auto; 
  line-height: 1.5; 
  padding: 10px;
  background-color: #f9f9f9; 
  border: 1px solid #ddd; 
  border-radius: 8px; 
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); 
  width: 50%;
  margin-top: 20px;
}

#login-message a {
  color: #007bff; 
  text-decoration: none; 
  font-weight: bold; 
}

#login-message a:hover {
  text-decoration: underline; 
  color: #0056b3; 
}
h1 {
  margin: 0px;
}
.name-link {
  text-decoration: none;
}
h3 a{
  text-decoration: none;
  color: #007bff;
}

h3 a:hover {
  text-decoration: underline; 
  color: #0056b3; 
}

td a {
  text-decoration: none;
  color: #007bff;
}

td a:hover {
  text-decoration: underline; 
  color: #0056b3;
}
</style>