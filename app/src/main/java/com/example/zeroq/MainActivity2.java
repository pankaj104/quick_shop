package com.example.zeroq;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ktx.Firebase;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity implements  PaymentResultListener{

    ListView listView;
Button scan , pay;
TextView textView,total_amt;
    int total_Amount = 0;
    FirebaseDatabase rootnode;
    DatabaseReference databaseReference1;
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> price = new ArrayList<>();
    ArrayList<String> code = new ArrayList<>();
    dbhandler db = new dbhandler(MainActivity2.this);
    ArrayList<String> quantity = new ArrayList<>();
@Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main2);

  //  textView = findViewById(R.id.textView);




    listView = findViewById(R.id.listview);
    total_amt = findViewById(R.id.textView10);
    customadapter ad = new customadapter(this, name, price, code, quantity);
    listView.setAdapter(ad);
    scan = findViewById(R.id.button5);
    pay = findViewById(R.id.button3);
    scan.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(MainActivity2.this, scan_screen.class);
            startActivity(i);
        }
    });


    pay.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!name.isEmpty() && !price.isEmpty() && !code.isEmpty() && !quantity.isEmpty()) {
                customadapter ad = new customadapter(MainActivity2.this, name, price, code, quantity);
                name.clear();
                price.clear();
                code.clear();
                quantity.clear();
                ad.notifyDataSetChanged();




                Checkout checkout = new Checkout();
                checkout.setImage(R.drawable.person);
                try {
                    String Total=total_amt.getText().toString();
                    Integer Int_i= Integer.parseInt(Total);
                    int float_i=Math.round(Float.parseFloat(String.valueOf(Int_i))*100);
                    String Total_graph = total_amt.getText().toString();
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    rootnode = FirebaseDatabase.getInstance();
                    databaseReference1 = rootnode.getReference("Users").child(uid).child("purchase");
                    databaseReference1.push().setValue(Total_graph).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {


                            } else {

                            }
                        }
                    });




                    JSONObject options = new JSONObject();
                    options.put("name", R.string.app_name);
                    options.put("description", "Payment for Buy");
                    options.put("send_sms_hash", true);
                    options.put("allow_rotation", false);

                    options.put("currency", "INR");
                    options.put("amount", float_i   );

                    JSONObject preFill = new JSONObject();
                    preFill.put("email", "email");
                    preFill.put("contact", "phone");

                    options.put("prefill", preFill);

                    checkout.open(MainActivity2.this, options);

                } catch (Exception e) {
                    e.printStackTrace();


                }

            } else {
                Toast.makeText(MainActivity2.this, "Your Cart is Empty!", Toast.LENGTH_SHORT).show();
            }

        }
            });

    Cursor res = db.getdata();
    while (res.moveToNext()) {
        code.add(res.getString(0));
        name.add(res.getString(1));
        price.add(res.getString(2));
        quantity.add(res.getString(3));
        ad.notifyDataSetChanged();


    }

    //get total Amount of All products in cart

    for (int i = 0; i < price.size(); i++) {

        total_Amount = total_Amount + Integer.parseInt(price.get(i));
String str_Amount= String.valueOf(total_Amount);
        total_amt.setText(str_Amount);

    }

}

    @Override
    public void onPaymentSuccess(String s) {
        try {
            Intent intent = new Intent(MainActivity2.this,scan_screen.class);
            startActivity(intent);

            ArrayList<String> invoice_name = new ArrayList<>();

            ArrayList<String> invoice_price = new ArrayList<>();

            ArrayList<String> invoice_Quantity = new ArrayList<>();

            Cursor res = db.getdata();
            while (res.moveToNext()) {
                invoice_name.add(res.getString(1));
                invoice_price.add(res.getString(2));
                invoice_Quantity.add(res.getString(3));
            }
            for (int i = 0; i < price.size(); i++) {

                total_Amount = total_Amount + Integer.parseInt(price.get(i));
                String str_Amount = String.valueOf(total_Amount);
                total_amt.setText(str_Amount);
            }

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("Do you want to download the Invoice?");
                builder.setPositiveButton("download", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity2.this, "Downloading Invoice...", Toast.LENGTH_SHORT).show();
                        convertToPdf();
                        Intent intent = new Intent(MainActivity2.this, Invoice_view.class);
                        startActivity(intent);
                    }

                    private void convertToPdf() {
                        String path = getExternalFilesDir(null).getAbsolutePath().toString() + "/Invoice.pdf";
                        File file = new File(path);
                        if (file.exists()) {
                            try {
                                file.createNewFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        Document document = new Document(PageSize.A4);
                        try {
                            PdfWriter.getInstance(document, new FileOutputStream(file.getAbsoluteFile()));
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        document.open();

                        Font font = new Font(Font.FontFamily.COURIER, 24);
                        Font myfont = new Font(Font.FontFamily.COURIER, 34);
                        Paragraph paragraph = new Paragraph();
                        paragraph.add(new Paragraph("Invoice", myfont));
                        paragraph.add(new Paragraph("Name     Quantity     Price ", font));
                        for (int i = 0; i < invoice_name.size(); i++) {

                            String Name = invoice_name.get(i);
                            String Quantity= invoice_Quantity.get(i);
                            String price= invoice_price.get(i);

                            paragraph.add(new Paragraph(Name+"    "+Quantity+"       "+price, font));
                        }
                        String invoiceamount = String.valueOf(total_Amount);
                        paragraph.add(new Paragraph("Total Amount:" + invoiceamount, myfont));


                        try {
                            document.add(paragraph);
                        } catch (DocumentException e) {
                            e.printStackTrace();
                        }

                        document.close();
                        Toast.makeText(MainActivity2.this, "PDF is created!", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customadapter ad = new customadapter(MainActivity2.this, name, price, code, quantity);

                        name.clear();
                        price.clear();
                        code.clear();
                        quantity.clear();
                        db.delete();
                        ad.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });
                builder.show();

                Toast.makeText(this, "Payment success!", Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                e.printStackTrace();
            }

        db.delete();
        }
    @Override
    public void onPaymentError(int i, String s) {

    }

    @Override
    public void onBackPressed() {

            Intent i= new Intent(MainActivity2.this,scan_screen.class);
            startActivity(i);
            super.onBackPressed();



    }

}
