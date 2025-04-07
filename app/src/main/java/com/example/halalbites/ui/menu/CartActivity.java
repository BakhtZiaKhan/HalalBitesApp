package com.example.halalbites.ui.Delivery;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.halalbites.R;
import com.example.halalbites.ui.menu.CartItem;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerViewCart;
    private TextView tvCartTotal;
    private CartAdapter cartAdapter;
    private List<CartItem> cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);

        cart = CartManager.getInstance().getCart(); // Global cart holder

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        tvCartTotal = findViewById(R.id.tvCartTotal);

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(cart);
        recyclerViewCart.setAdapter(cartAdapter);

        double total = CartManager.getInstance().getTotal();
        tvCartTotal.setText(String.format("Total: €%.2f", total));
    }
}
