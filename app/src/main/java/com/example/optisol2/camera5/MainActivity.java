package com.example.optisol2.camera5;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends Activity {
    public ImageAdapter imageAdapter;
    private final static int TAKE_IMAGE = 1;
    private Uri imageUri;
    private MediaScannerConnection mScanner;
    public GridView imagegrid;
    private long lastId;
    String userChoosenTask="",path;
    Cursor imagecursor;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageAdapter = new ImageAdapter();
        imageAdapter.initialize();
        imagegrid = (GridView) findViewById(R.id.PhoneImageGrid);
        imagegrid.setAdapter(imageAdapter);
       // imagegrid.setVisibility(View.VISIBLE);
        final Button captureBtn = (Button) findViewById(R.id.captureBtn);
        captureBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
                String fileName = "IMG_" + sdf.format(new Date()) + ".jpg";
                File myDirectory = new File(Environment
                        .getExternalStorageDirectory() + "/REOAllegiance/");
                myDirectory.mkdirs();
                File file = new File(myDirectory, fileName);
                imageUri = Uri.fromFile(file);
                Intent intent = new Intent(
                        android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_IMAGE);
            }
        });
    }

    @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                try {
                    // we need to update the gallery by starting MediaSanner service.
                    mScanner = new MediaScannerConnection(
                            MainActivity.this,
                            new MediaScannerConnection.MediaScannerConnectionClient() {
                                public void onMediaScannerConnected() {
                                    mScanner.scanFile(imageUri.getPath(), null /* mimeType */);
                                }

                                public void onScanCompleted(String path, Uri uri) {

                                    if (path.equals(imageUri.getPath())) {
                                        mScanner.disconnect();
                                        MainActivity.this
                                                .runOnUiThread(new Runnable() {
                                                    public void run() {
                                                        updateUI();
                                                    }
                                                });
                                    }
                                }
                            });
                    mScanner.connect();

                } catch (Exception e) {
                    e.printStackTrace();
                }

        }
    }

    public void updateUI() {
        imageAdapter.checkForNewImages();
    }


    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        String path="";
        private int count;
        public ArrayList<ImageItem> images = new ArrayList<ImageItem>();

        public ImageAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        public void initialize() {
            images.clear();
            int id;
            final String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String orderBy = MediaStore.Images.Media._ID;
            imagecursor = getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                    null, null, orderBy);
            int image_column_index = imagecursor
                    .getColumnIndex(MediaStore.Images.Media._ID);
            int image_column_data = imagecursor.getColumnIndex(MediaStore.Images.Media.DATA);
            this.count = imagecursor.getCount();
            if (count != 0) {
                for (int i = 0; i < count; i++) {
                    imagecursor.moveToPosition(i);
                    id = imagecursor.getInt(image_column_index);
                    ImageItem imageItem = new ImageItem();
                    path = imagecursor.getString(image_column_data);
                    imageItem.id = id;
                    lastId = id;
                    imageItem.img = MediaStore.Images.Thumbnails.getThumbnail(
                            getApplicationContext().getContentResolver(), id,
                            MediaStore.Images.Thumbnails.MICRO_KIND, null);
                    if (imageItem.img != null) {
                        images.add(imageItem);
                    }
                }


                notifyDataSetChanged();
            }
            else{
                Toast.makeText(MainActivity.this, "null array", Toast.LENGTH_SHORT).show();
            }
        }
        public void checkForNewImages(){
            final String[] columns = { MediaStore.Images.Thumbnails._ID };
            final String orderBy = MediaStore.Images.Media._ID;


                   imagecursor = getContentResolver().query(
                           MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                           MediaStore.Images.Media._ID + " > " + lastId, null, orderBy);
                   int image_column_index = imagecursor
                           .getColumnIndex(MediaStore.Images.Media._ID);
                   int count = imagecursor.getCount();
                   for (int i = 0; i < count; i++) {
                       imagecursor.moveToPosition(i);
                       int id = imagecursor.getInt(image_column_index);
                       String filepath = imagecursor.getString(image_column_index);
                       Uri uri = Uri.parse(filepath);
                       ImageItem imageItem = new ImageItem();
                       imageItem.id = id;
                       imageItem.path=filepath;
                       lastId = id;
                       try {
                           imageItem.img = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                       imageItem.selection = true; //newly added item will be selected by default
                       images.add(imageItem);

                   }
            notifyDataSetChanged();
        }
        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.gallery_list_item, null);
                holder.imageview = (ImageView) convertView
                        .findViewById(R.id.imageView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImageItem item = images.get(position);
            holder.imageview.setId(item.id);
            holder.imageview.setOnClickListener(new OnClickListener() {
                @SuppressWarnings("deprecation")
                public void onClick(final View v) {
                    int id = v.getId();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    final String[] columns = { MediaStore.Images.Media.DATA };
                    Cursor imagecursor = managedQuery(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns,
                            MediaStore.Images.Media._ID + " = " + id, null, MediaStore.Images.Media._ID);
                    if (imagecursor != null && imagecursor.getCount() > 0){
                        imagecursor.moveToPosition(0);
                        String path = imagecursor.getString(imagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                        intent.setDataAndType(
                                Uri.parse("file://" + path),
                                "image/*");
                        startActivityForResult(intent, 3);
                    }
                }
            });

            holder.imageview.setImageBitmap(item.img);
            holder.imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.imageview.setLayoutParams(new GridView.LayoutParams(355, 355));
            return convertView;
        }
    }

    class ViewHolder {
        ImageView imageview;
    }

    class ImageItem {
        boolean selection;
        int id;
        Bitmap img;
        String path;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}