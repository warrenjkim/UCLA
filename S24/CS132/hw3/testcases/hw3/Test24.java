class InventoryTest {
  public static void main(String[] args) {
    Inventory inventory;
    int result;

    inventory = new Inventory();
    result = inventory.init(10); // Initialize inventory for 10 products
    System.out.println(result);

    // Set initial quantities for products
    result = inventory.setQuantity(0, 50);
    System.out.println(result);
    result = inventory.setQuantity(1, 30);
    System.out.println(result);

    // Update product quantities
    result = inventory.updateQuantity(0, 5); // Increase quantity of product 0 by 5
    System.out.println(result);
    result = inventory.getQuantity(0); // Get updated quantity of product 0
    System.out.println(result);

    result = inventory.updateQuantity(1, (0 - 10)); // Decrease quantity of product 1 by 10
    System.out.println(result);
    result = inventory.getQuantity(1); // Get updated quantity of product 1
    System.out.println(result);
  }
}

class Inventory {
  int[] quantities;

  public int init(int size) {
    quantities = new int[size];
    return 1; // Success, inventory initialized
  }

  public int setQuantity(int index, int quantity) {
    quantities[index] = quantity;
    return 1; // Success, quantity set
  }

  public int updateQuantity(int index, int amount) {
    quantities[index] = (quantities[index]) + amount;
    return 1; // Success, quantity updated
  }

  public int getQuantity(int index) {
    return quantities[index]; // Return the quantity at the index
  }
}
