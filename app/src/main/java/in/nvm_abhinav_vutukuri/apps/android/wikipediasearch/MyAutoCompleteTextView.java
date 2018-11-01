package in.nvm_abhinav_vutukuri.apps.android.wikipediasearch;

import android.content.Context;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.util.AttributeSet;
import android.widget.ProgressBar;

class MyAutoCompleteTextView extends AppCompatAutoCompleteTextView
{
    private ProgressBar progressBar;

    public MyAutoCompleteTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    @Override
    protected void performFiltering(CharSequence text, int keyCode)
    {
        super.performFiltering(text, keyCode);
        progressBar.setVisibility(VISIBLE);
    }

    @Override
    public void onFilterComplete(int count)
    {
        super.onFilterComplete(count);
        progressBar.setVisibility(GONE);
    }

    void setProgressBar(ProgressBar progressBar)
    {
        this.progressBar = progressBar;
    }
}
