package in.nvm_abhinav_vutukuri.apps.android.wikipediasearch;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;

import static in.nvm_abhinav_vutukuri.apps.android.wikipediasearch.Utils.hasInternetAccess;
import static in.nvm_abhinav_vutukuri.apps.android.wikipediasearch.Utils.showAlertDialog;

public class MainActivity extends AppCompatActivity
{
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);

        MyAutoCompleteTextView myAutoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        myAutoCompleteTextView.setProgressBar(progressBar);

        AutoCompleteAdapter autoCompleteAdapter = new AutoCompleteAdapter(this, R.layout.listitem_article, MainActivity.this);
        myAutoCompleteTextView.setAdapter(autoCompleteAdapter);
    }

    @Override
    protected void onPostResume()
    {
        super.onPostResume();

        try
        {
            if (!hasInternetAccess())
            {
                showAlertDialog(this, R.string.error_no_internet_acsess, R.string.msg_check_network);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
