package in.nvm_abhinav_vutukuri.apps.android.wikipediasearch;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static in.nvm_abhinav_vutukuri.apps.android.wikipediasearch.Utils.*;

class AutoCompleteAdapter extends ArrayAdapter<Article> implements Filterable
{
    private static final String QUERY = "query";
    private static final String PAGES = "pages";
    private static final String TITLE = "title";
    private static final String THUMBNAIL = "thumbnail";
    private static final String SOURCE = "source";
    private static final String TERMS = "terms";
    private static final String DESCRIPTION = "description";

    //used while logging
    private static final String TAG = "ACAdapter";

    private static final String BASE_URL
            = "https://en.wikipedia.org//w/api.php?action=query&format=json&prop=pageimages|pageterms"
            + "&generator=prefixsearch&redirects=1&formatversion=2&piprop=thumbnail&pithumbsize=50"
            + "&pilimit=10&wbptterms=description&gpslimit=10&gpssearch=";


    private MainActivity mainActivity;
    private Context context;
    private ArrayList<Article> articles;
    private String jsonString;
    private StringBuilder jsonContent;

    AutoCompleteAdapter(Context context, int resource, MainActivity mainActivity)
    {
        super(context, resource);
        this.articles = new ArrayList<>();
        this.context = context;
        this.mainActivity = mainActivity;
    }

    @Override
    @NonNull
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence input)
            {
                FilterResults filterResults = new FilterResults();

                if (input == null )
                {
                    return filterResults;
                }

                String[] searchTermsArr = input.toString().split(" ");
                StringBuilder searchTermBuilder = new StringBuilder();

                for (String str : searchTermsArr)
                {
                    searchTermBuilder.append(str);
                    searchTermBuilder.append("+");
                }

                if (searchTermBuilder.length() > 0 && searchTermBuilder.charAt(searchTermBuilder.length() - 1) == '+')
                {
                    searchTermBuilder.deleteCharAt(searchTermBuilder.length() - 1);
                }

                String searchTerm = searchTermBuilder.toString();

                String finalURL = BASE_URL + searchTerm;

                InputStream inputStream;
                final int responseCode;

                try
                {
                    URL url = new URL(finalURL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    responseCode = connection.getResponseCode();

                    if (responseCode == 200)
                    {
                        inputStream = connection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                        jsonContent = new StringBuilder();

                        while ((jsonString = bufferedReader.readLine()) != null)
                        {
                            jsonContent.append(jsonString);
                        }

                        bufferedReader.close();
                        connection.disconnect();
                        inputStream.close();
                    }
                    else
                    {
                        showDailogOnUIthread(mainActivity, R.string.error_something_wrong, "response code: " + responseCode);
                    }
                }
                catch (Exception e)
                {
                    showDailogOnUIthread(mainActivity, R.string.error_something_wrong, R.string.msg_try_later);
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence input, FilterResults filterResults)
            {
                if (jsonContent == null) {
                    filterResults.count = 0;
                }
                else {
                    parseJsonAndFillAdapter(jsonContent.toString());
                    filterResults.values = articles;
                    filterResults.count = articles.size();
                }

                if (filterResults.count > 0)
                    notifyDataSetChanged();
                else
                    notifyDataSetInvalidated();
            }
        };
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        final Article currArticle = getItem(position);

        TextView title_TextView;
        TextView description_TextView;
        ImageView thumbnail_ImageView;

        if (convertView == null)
        {
            convertView = LayoutInflater.from(context)
                                        .inflate(R.layout.listitem_article, null);

            title_TextView = convertView.findViewById(R.id.title_TextView);
            description_TextView = convertView.findViewById(R.id.description_TextView);
            thumbnail_ImageView = convertView.findViewById(R.id.thumbnail_ImageView);

            ViewHolder viewHolder = new ViewHolder();
            viewHolder.title_TextView = title_TextView;
            viewHolder.description_TextView = description_TextView;
            viewHolder.thumbnail_ImageView = thumbnail_ImageView;

            convertView.setTag(viewHolder);
        }
        else
        {
            ViewHolder viewHolder = (ViewHolder) convertView.getTag();
            title_TextView = viewHolder.title_TextView;
            description_TextView = viewHolder.description_TextView;
            thumbnail_ImageView = viewHolder.thumbnail_ImageView;
        }

        title_TextView.setText(currArticle.getTitle());
        description_TextView.setText(currArticle.getDescription());

        String thumbUrl = currArticle.getThumbnailSource();
        if (URLUtil.isValidUrl(thumbUrl))
            loadImageInto(thumbnail_ImageView, currArticle.getThumbnailSource());
        else
            thumbnail_ImageView.setImageResource(R.drawable.ic_image);

        convertView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String title = currArticle.getTitle().replace(" ", "_");
                String url = "https://en.wikipedia.org/wiki/" + title;
                Intent intent = new Intent(mainActivity, WebViewActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, url);
                mainActivity.startActivity(intent);
            }
        });

        return convertView;
    }

    private void parseJsonAndFillAdapter(String JSON_Content)
    {
        articles.clear();

        try
        {
            JSONObject jsonObject = new JSONObject(JSON_Content);

            if (jsonObject.has(QUERY))
            {
                JSONObject query = jsonObject.getJSONObject(QUERY);

                if (query.has(PAGES))
                {
                    JSONArray pagesArr = query.getJSONArray(PAGES);

                    for (int i = 0; i < pagesArr.length(); i++)
                    {
                        JSONObject currentPage = pagesArr.getJSONObject(i);

                        String title, thumbnailSrc, description;
                        title = "";
                        description = "description not found";
                        thumbnailSrc = "";

                        if (currentPage.has(TITLE))
                        {
                            title = currentPage.getString(TITLE);
                        }

                        if (currentPage.has(THUMBNAIL))
                        {
                            JSONObject thumbnail = currentPage.getJSONObject(THUMBNAIL);

                            if (thumbnail.has(SOURCE))
                            {
                                thumbnailSrc = thumbnail.getString(SOURCE);
                            }
                        }

                        if (currentPage.has(TERMS))
                        {
                            JSONObject terms = currentPage.getJSONObject(TERMS);

                            if (terms.has(DESCRIPTION))
                            {
                                JSONArray descriptionArr = terms.getJSONArray(DESCRIPTION);

                                if (descriptionArr.length() > 0)
                                {
                                    description = String.valueOf(descriptionArr.get(0));
                                }
                            }
                        }

                        articles.add(new Article(title, description, thumbnailSrc));
                    }
                }
            }
        }
        catch (JSONException e)
        {
            showDailogOnUIthread(mainActivity, R.string.error_something_wrong, e.toString());
        }
    }

    @Override
    public Article getItem(int position)
    {
        return articles.get(position);
    }

    @Override
    public int getCount()
    {
        return articles.size();
    }

    void setData(ArrayList<Article> articles)
    {
        this.articles = articles;
    }

    private void loadImageInto(ImageView imageView, String url)
    {
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        Picasso.get()
               .load(url)
               .placeholder(R.drawable.ic_error)
               .error(R.drawable.ic_image)
               .into(imageView);
    }

    private static class ViewHolder
    {
        ImageView thumbnail_ImageView;
        TextView title_TextView;
        TextView description_TextView;
    }
}
