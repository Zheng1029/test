package idv.ron.autocompletetextviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        final String[] countries = {
                "CANADA", "CHINA", "FRANCE", "GERMANY",
                "ITALY", "JAPAN", "KOREA", "TAIWAN", "UK", "USA"
        };
        AutoCompleteTextView actvCountry =
                (AutoCompleteTextView) findViewById(R.id.actvCountry);
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(this, R.layout.list_item, countries);
        actvCountry.setAdapter(arrayAdapter);
        actvCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(
                        MainActivity.this,
                        item,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }
}
