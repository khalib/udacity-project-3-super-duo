package it.jaschke.alexandria;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.services.BookService;


public class BookDetail extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = BookDetail.class.getSimpleName();

    public static final String EAN_KEY = "EAN";
    private final int LOADER_ID = 10;
    private View rootView;
    private String ean;
    private String bookTitle;
    private ShareActionProvider shareActionProvider;

    private static final int BOOK_LOADER = 0;

    public BookDetail() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "===== onCreate()");

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v(LOG_TAG, "===== onCreateView()");

        // Get the selected EAN.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(BookDetail.EAN_KEY)) {
            // Get the EAN from the activity intent call.
            ean = intent.getStringExtra(BookDetail.EAN_KEY);
        } else {
            // Get the EAN from the fragment arguments.
            Bundle arguments = getArguments();
            if (arguments != null) {
                ean = arguments.getString(BookDetail.EAN_KEY);
                getLoaderManager().restartLoader(LOADER_ID, null, this);
            }
        }

        rootView = inflater.inflate(R.layout.fragment_full_book, container, false);

        // Click handler for deleting a book.
        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, ean);
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);

                Toast.makeText(getContext(), R.string.book_deleted, Toast.LENGTH_LONG).show();

                if (Utility.isTablet(getActivity())) {
                    getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    getActivity().finish();
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(LOG_TAG, "===== onCreateOptionsMenu()");

        inflater.inflate(R.menu.book_detail, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v(LOG_TAG, "===== onCreateLoader()");

        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(ean)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.v(LOG_TAG, "===== onActivityCreated()");

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "===== onLoadFinished()");

        if (!data.moveToFirst()) {
            return;
        }

        bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        ((TextView) rootView.findViewById(R.id.fullBookTitle)).setText(bookTitle);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + bookTitle);

        // Update the share intent if created by onCreateOptionsMenu().
        if (shareActionProvider != null) {
            shareActionProvider.setShareIntent(shareIntent);
        }

        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        ((TextView) rootView.findViewById(R.id.fullBookSubTitle)).setText(bookSubTitle);

        String desc = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.DESC));
        ((TextView) rootView.findViewById(R.id.fullBookDesc)).setText(desc);

        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));

        // Account for books that return a null author.
        int authorsLines = 0;
        String authorsOutput = "";
        if (authors != null) {
            String[] authorsArr = authors.split(",");
            authorsLines = authorsArr.length;
            authorsOutput = authors.replace(",","\n");
        }

        ((TextView) rootView.findViewById(R.id.authors)).setLines(authorsLines);
        ((TextView) rootView.findViewById(R.id.authors)).setText(authorsOutput);

        // Load bookcover image.
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        ImageView bookCover = (ImageView) rootView.findViewById(R.id.fullBookCover);

        Glide.with(this)
                .load(imgUrl)
                .error(R.drawable.ic_launcher)
                .crossFade()
                .override(400, 400)
                .into(bookCover);

        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));
        ((TextView) rootView.findViewById(R.id.categories)).setText(categories);

        // Add content description to the book cover image.
        bookCover.setContentDescription(bookTitle);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        Log.v(LOG_TAG, "===== onLoaderReset()");
    }

    @Override
    public void onPause() {
        Log.v(LOG_TAG, "===== onPause()");

        super.onDestroyView();
        if(MainActivity.IS_TABLET && rootView.findViewById(R.id.right_container)==null){
            getActivity().getSupportFragmentManager().popBackStack();
        }
    }
}