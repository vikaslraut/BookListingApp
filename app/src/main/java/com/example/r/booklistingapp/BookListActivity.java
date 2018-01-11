package com.example.r.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookListActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<ArrayList<Book>> {

    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final String TAG = BookListActivity.class.getSimpleName();
    @BindView(R.id.book_listview)
    ListView listView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.emptyState)
    TextView emptyStateText;

    private String queryStringValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_list);

        queryStringValue = getIntent().getStringExtra(getString(R.string.query_key));
        ButterKnife.bind(this);

        if (Utils.networkAvailable(this)) {
            getLoaderManager().initLoader(0, null, BookListActivity.this);
        } else {
            progressBar.setVisibility(View.GONE);
            emptyStateText.setText(getString(R.string.no_connection));
            listView.setEmptyView(emptyStateText);
        }
    }


    public void updateListUI(ArrayList<Book> books) {

        progressBar.setVisibility(View.GONE);
        if (books.isEmpty()) {
            listView.setEmptyView(findViewById(R.id.emptyState));
            return;
        }
        BookAdapter bookAdapter = new BookAdapter(this, books);
        listView.setAdapter(bookAdapter);
    }

    private URL buildURL() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String maxResults = sharedPreferences.getString(
                getString(R.string.max_books_key),
                getString(R.string.setting_num_of_results));
        Uri baseUrl = Uri.parse(BOOK_REQUEST_URL);
        Uri.Builder uriBuilder = baseUrl.buildUpon();
        uriBuilder.appendQueryParameter(getString(R.string.query), queryStringValue);
        uriBuilder.appendQueryParameter(getString(R.string.max_results), maxResults);
        return Utils.createURL(uriBuilder.toString());
    }

    @Override
    public Loader<ArrayList<Book>> onCreateLoader(int i, Bundle bundle) {
        Log.d(TAG, "onCreateLoader: creating new book loader ");
        return new BookLoader(BookListActivity.this, buildURL());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Book>> loader, ArrayList<Book> books) {
        Log.d(TAG, "onLoadFinished: updating UI");
        if (books == null)
            return;
        updateListUI(books);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Book>> loader) {

    }

    public static class BookLoader extends AsyncTaskLoader<ArrayList<Book>> {
        URL url;

        public BookLoader(Context context, URL url) {
            super(context);
            this.url = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }


        @Override
        public ArrayList<Book> loadInBackground() {
            Log.d(TAG, "loadInBackground: started");
            String jsonResponse = null;
            try {
                jsonResponse = Utils.makeHttpRequest(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return extractBooksFromJson(jsonResponse);
        }

        /**
         * Method to parse JSON response and generate of ArrayList<Book> type
         *
         * @param response
         * @return
         */
        private ArrayList<Book> extractBooksFromJson(String response) {
            if (TextUtils.isEmpty(response))
                return null;

            ArrayList<Book> tempList = new ArrayList<>();
            StringBuilder authorList = new StringBuilder();

            try {
                JSONObject rootJsonObject = new JSONObject(response);
                JSONArray jsonBooksArray = rootJsonObject.getJSONArray(Utils.JSON_BOOK_ARRAY);
                Log.d(TAG, "extractBooks: root books array length : " + jsonBooksArray.length());

                for (int i = 0; i < jsonBooksArray.length(); i++) {
                    JSONObject book = jsonBooksArray.getJSONObject(i);
                    JSONObject bookInfo = book.getJSONObject(Utils.JSON_VOLUME_INFO);
                    JSONArray author = bookInfo.optJSONArray(Utils.JSON_AUTHORS_ARRAY);

                    if (author != null) {
                        for (int j = 0; j < author.length(); j++) {
                            authorList.append(author.getString(j));
                            if (j + 1 != author.length())
                                authorList.append(", ");
                        }
                    } else {
                        authorList.append(Utils.JSON_UNKNOWN_AUTHOR_STRING);
                    }

                    tempList.add(new Book(bookInfo.getString(Utils.JSON_BOOK_TITLE), authorList.toString()));
                    authorList.setLength(0);
                }
            } catch (JSONException e) {
                Log.e(TAG, "extractBooksFromJson: Problem parsing json ", e);
            }
            return tempList;
        }
    }
}
