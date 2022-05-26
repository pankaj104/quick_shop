package com.example.zeroq;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Expense_graph extends AppCompatActivity {
    BarChart barChart;
    FirebaseDatabase rootnode;
    TextView exp_nav_name,exp_nav_Email,nav_home;
    DrawerLayout drawerLayout;
    DatabaseReference databaseReference1;
    View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_graph);
        barChart=findViewById(R.id.bar_chart);
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        rootnode = FirebaseDatabase.getInstance();
        databaseReference1 = rootnode.getReference("Users").child(uid).child("purchase");
       exp_nav_name=findViewById(R.id.expense_Nav_Name);
        exp_nav_Email=findViewById(R.id.expense_Nav_Email);
        nav_home= findViewById(R.id.expense_nav_home);
drawerLayout= findViewById(R.id.drawer_layout);

        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                double i=1;
                ArrayList<BarEntry> Graph_list= new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren() ){
                    String prices= snap.getValue().toString();
                    Graph_list.add( new BarEntry((float) i, Float.parseFloat(prices)));
                    i++;

                }
                BarDataSet barDataSet= new BarDataSet(Graph_list,"Times");
                BarData barData= new BarData();
                barData.addDataSet(barDataSet);
                barChart.animateY(0);
                barChart.getDescription().setText("Times");
                barChart.getDescription().setTextColor(Color.CYAN);


                barChart.setData(barData);
                barChart.invalidate();
                barChart.setFitBars(true);
                barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Intent intent= getIntent();
        String expesnesName= intent.getStringExtra("name");
        String expesnesEmail= intent.getStringExtra("email");
        exp_nav_name.setText(expesnesName);
        exp_nav_Email.setText(expesnesEmail);


        nav_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Expense_graph.this,scan_screen.class);
                startActivity(i);
            }
        });




    }
    public void ClickMenu(View view){
        openDrawer(drawerLayout);


    }

    private static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }
    public void clicklogo(View view){
        closeDrawer(drawerLayout);
    }

    private static void closeDrawer(DrawerLayout drawerLayout) {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }
    @Override
    protected  void onPause(){
        super.onPause();
        closeDrawer(drawerLayout);
    }

    @Override
    public void onBackPressed() {

        Intent i= new Intent(Expense_graph.this,scan_screen.class);
        startActivity(i);
        super.onBackPressed();


    }
}