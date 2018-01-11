package com.example.r.booklistingapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Custom adapter class for book data
 */
public class BookAdapter extends ArrayAdapter {

    public BookAdapter(Context context, ArrayList<Book> bookArrayList) {
        super(context, 0, bookArrayList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        Book book = (Book) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.book_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.bookTitle.setText(book.getBookTitle());
        viewHolder.bookAuthor.setText(book.getBookAuthor());
        return convertView;
    }

    static class ViewHolder {
        @BindView(R.id.book_title)
        TextView bookTitle;
        @BindView(R.id.book_author)
        TextView bookAuthor;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
