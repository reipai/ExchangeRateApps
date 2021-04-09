package com.reivai.exchangerateapps;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    TextView tv_currencyTo;
    ImageView img_flagTo;
    EditText et_fromEur;
    Button btn_convert;
    Spinner spin_country;
    List<String> countryCurr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        et_fromEur  = findViewById(R.id.et_fromEur);

        tv_currencyTo = findViewById(R.id.tv_currencyTo);

        spin_country = findViewById(R.id.spin_country);
        btn_convert = findViewById(R.id.btn_convert);

        try {
            getCurrency();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btn_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_fromEur.getText().toString().isEmpty()) {
                    String rate = spin_country.getSelectedItem().toString();
                    double eur = Double.valueOf(et_fromEur.getText().toString());

                    try {
                        calcRate(rate, eur);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please Enter a Value to Convert", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getCurrency() throws IOException {
        String url = "http://data.fixer.io/api/latest?access_key=43d49c47e87f2386711caec85340d959";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String resp = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(resp);
                            JSONObject obj = object.getJSONObject("rates");

                            Iterator keysCopy = obj.keys();
                            countryCurr = new ArrayList<>();

                            while (keysCopy.hasNext()) {
                                String keys = (String) keysCopy.next();
                                countryCurr.add(keys);
                            }

                            ArrayAdapter<String> spinAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, countryCurr);
                            spin_country.setAdapter(spinAdapter);
                            spinAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void calcRate(final String rate, final double eur) throws IOException {
        String url = "http://data.fixer.io/api/latest?access_key=43d49c47e87f2386711caec85340d959";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String resp = response.body().string();
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject object = new JSONObject(resp);
                            JSONObject obj = object.getJSONObject("rates");

                            String value = obj.getString(rate);
                            double result = eur * Double.valueOf(value);

                            tv_currencyTo.setText(String.valueOf(result));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
