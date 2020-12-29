package com.infinitypoint.mynoteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class htmlActivity extends AppCompatActivity {
    RequestQueue requestQueue;
    String webpages;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.htmlactivity);

        requestQueue = Volley.newRequestQueue(this);
        addUserRecordToTheDatabase();
       textView = (TextView) findViewById(R.id.text_view);
      /*  textView.setMovementMethod(new LinkMovementMethod());
        textView.setText(Html.fromHtml(
                webpages,
                new PicassoImageGetter(textView), null));*/
    }

    private static class PicassoImageGetter implements Html.ImageGetter {
        private final TextView mTextView;

        /**
         * Construct an instance of {@link android.text.Html.ImageGetter}
         * @param view      {@link android.widget.TextView} that holds HTML which contains $lt;img&gt; tag to load
         */
        public PicassoImageGetter(TextView view) {
            mTextView = view;
        }

        @Override
        public Drawable getDrawable(String source) {
            if (TextUtils.isEmpty(source)) {
                return null;
            }
            final Uri uri = Uri.parse(source);
            if (uri.isRelative()) {
                return null;
            }
            final URLDrawable urlDrawable = new URLDrawable(mTextView.getResources(), null);
            new LoadFromUriAsyncTask(mTextView, urlDrawable).execute(uri);
            return urlDrawable;
        }
    }

    private static class LoadFromUriAsyncTask extends AsyncTask<Uri, Void, Bitmap> {
        private final WeakReference<TextView> mTextViewRef;
        private final URLDrawable mUrlDrawable;
        private final Picasso mImageUtils;

        public LoadFromUriAsyncTask(TextView textView, URLDrawable urlDrawable) {
            mImageUtils = Picasso.with(textView.getContext());
            mTextViewRef = new WeakReference<>(textView);
            mUrlDrawable = urlDrawable;
        }

        @Override
        protected Bitmap doInBackground(Uri... params) {
            try {
                return mImageUtils.load(params[0]).get();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result == null) {
                return;
            }
            if (mTextViewRef.get() == null) {
                return;
            }
            TextView textView = mTextViewRef.get();
            // change the reference of the current mDrawable to the result
            // from the HTTP call
            mUrlDrawable.mDrawable = new BitmapDrawable(textView.getResources(), result);
            // set bound to scale image to fit width and keep aspect ratio
            // according to the result from HTTP call
            int width = textView.getWidth();
            int height = Math.round(1.0f * width *
                    mUrlDrawable.mDrawable.getIntrinsicHeight() /
                    mUrlDrawable.mDrawable.getIntrinsicWidth());
            mUrlDrawable.setBounds(0, 0, width, height);
            mUrlDrawable.mDrawable.setBounds(0, 0, width, height);
            // force redrawing bitmap by setting text
            textView.setText(textView.getText());
        }
    }

    private static class URLDrawable extends BitmapDrawable {
        private Drawable mDrawable;

        public URLDrawable(Resources res, Bitmap bitmap) {
            super(res, bitmap);
        }

        @Override
        public void draw(Canvas canvas) {
            if(mDrawable != null) {
                mDrawable.draw(canvas);
            }
        }
    }

    public  void addUserRecordToTheDatabase(){
        String url = "http://choop.one/Training/getpagecontent.php";

        StringRequest stringRequestToServer= new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("RESPONSE "+response);
                        webpages = response;
                        textView.setMovementMethod(new LinkMovementMethod());
                        textView.setText(Html.fromHtml(
                                webpages,
                                new PicassoImageGetter(textView), null));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(htmlActivity.this, "Error : "+error, Toast.LENGTH_SHORT).show();
            }
        }) ;
        requestQueue.add(stringRequestToServer);

    }
}