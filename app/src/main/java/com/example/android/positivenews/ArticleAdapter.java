package com.example.android.positivenews;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Niina on 7.7.2017.
 */


// This is a custom ArrayAdapter to properly display a list of Earthquake objects
// (= setting right data to right views etc.):

public class ArticleAdapter extends ArrayAdapter<ArticleDetails> {

    //First we initialize the ArrayAdapter's internal storage for the context and the list:
    public ArticleAdapter(Activity context, ArrayList<ArticleDetails> articles) {

        super(context, 0, articles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        //ConvertView = Existing view that can be reused;
        View listItemView = convertView;

        // If 'null' = there's no existing view yet -> inflate it with certain layout
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        //Now starting to find views + set data on them:
        //First finding the current article:
        ArticleDetails currentArticle = getItem(position);

        //Here matching the xml textview + its correct values:
        TextView titleTextView = (TextView) listItemView.findViewById(R.id.data_title);
        titleTextView.setText(currentArticle.getmTitle());

        TextView sectionTextView = (TextView) listItemView.findViewById(R.id.data_section);
        sectionTextView.setText(currentArticle.getmSection());

        TextView dateTextView = (TextView) listItemView.findViewById(R.id.data_date);
        String findDate = currentArticle.getmDate();
        //Here taking the 'T' and time out:
        String splitDate[] = findDate.split("T");
        String splittedDate = splitDate[0];
        dateTextView.setText(splittedDate);

        ImageView bitmapImageView = (ImageView) listItemView.findViewById(R.id.image_bitmap);
        if (currentArticle.getmBitmap() != null) {
            Picasso.with(getContext()).load(currentArticle.getmBitmap()).into(bitmapImageView);

        } else {
            bitmapImageView.setImageResource(R.drawable.no_image);
        }
        return listItemView;
    }

}

