//R  esempio layout a tessere: https://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/
package brainstorm.spaventapasseri;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;

public class ReceiptList extends AppCompatActivity {

    // R  https://stackoverflow.com/questions/7792942/load-all-images-from-folder-in-android
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {
        final String[] EXTENSIONS = new String[] { "gif", "png", "bmp", "jpg", "jpeg" };
        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS)
            {
                if (name.endsWith("." + ext))
                {
                    return true;
                }
            }
            return false;
        }
    };


    public static final String saveFolderName = "ScontrApp";
    File appDir = new File(Environment.getExternalStorageDirectory(), saveFolderName);
    private final PermissionsHandler permissionsHandler = new PermissionsHandler(this);
    private PhotosAdapter adapter;
    private List<PhotoItem> photoList = new ArrayList<>();
    private SortMode sortMode;
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.receipt_list_title); //R  Non riesco a cambiare il titolo in AndroidManifest senza cambiare il nome dell'app
        setContentView(R.layout.activity_receipt_list);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));
        prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        prefsEditor = prefs.edit();
        sortMode = SortMode.values()[prefs.getInt("sort_mode", SortMode.byDate.ordinal())];
        //appDir = new File(prefs.getString("app_dir", getFilesDir().getAbsolutePath()));
        if (!permissionsHandler.hasStoragePermission())
        {
            permissionsHandler.requestStoragePermission();
        }
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceiptList.this, CameraAcc.class);
                if (!photoList.isEmpty())
                {
                    List<PhotoItem> listCopy = new ArrayList<>(photoList);
                    PhotoItem.sort(listCopy, SortMode.byDate);
                    intent.putExtra("PhotoItem", listCopy.get(0));
                }
                startActivity(intent);
            }
        });
        adapter = new PhotosAdapter(this);
        RecyclerView photoListView = (RecyclerView) findViewById(R.id.photoListView);
        photoListView.setLayoutManager(new GridLayoutManager(this, 2));
        photoListView.setItemAnimator(new DefaultItemAnimator());
        photoListView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (permissionsHandler.hasStoragePermission())
        {
            fetchPhotosInfo();
        }
        else
            permissionsHandler.requestStoragePermission();
    }

    //R  Questo metodo trova le foto e salva le loro informazioni.
    //   Si occupera' Glide di caricarle e mostrarle ottimizzando la memoria
    private void fetchPhotosInfo() {
        if (appDir.exists())
        {
            File[] photos = appDir.listFiles(IMAGE_FILTER);
            photoList = new ArrayList<>();
            for (File file : photos)
            {
                photoList.add(new PhotoItem(file));
            }
            PhotoItem.sort(photoList, sortMode);
            adapter.setPhotoList(photoList);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_receipt_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort)
        {
            String[] options = new String[] {
                getResources().getString(R.string.sort_name),
                //getResources().getString(R.string.sort_amount),
                getResources().getString(R.string.sort_date)
            };
            new AlertDialog.Builder(this)
            .setTitle(R.string.action_sort)
            .setSingleChoiceItems(options, sortMode.ordinal(), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    prefsEditor.putInt("sort_mode", which);
                    prefsEditor.apply();
                    sortMode = SortMode.values()[which];
                    PhotoItem.sort(photoList, sortMode);
                    adapter.setPhotoList(photoList);
                }
            })
            .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            })
            .show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoCardHolder> {

        private Context context;
        private List<PhotoItem> photoList;

        class PhotoCardHolder extends RecyclerView.ViewHolder {
            TextView title;
            ImageView thumbnail;

            PhotoCardHolder(View view) {
                super( view );
                title = (TextView) view.findViewById(R.id.title);
                thumbnail = (ImageView)view.findViewById(R.id.thumbnail);
            }
        }

        PhotosAdapter(Context context) {
            this.context = context;
            this.photoList = new ArrayList<>();
        }

        void setPhotoList(List<PhotoItem> photoList) {
            this.photoList = photoList;
            notifyDataSetChanged();
        }

        @Override
        public PhotoCardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.photo_card, parent, false);
            return new PhotoCardHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final PhotoCardHolder holder, final int position) {
            final PhotoItem currentItem = photoList.get(position);
            holder.title.setText(currentItem.getTitle());

            //R  thumbnail ratio 10%
            Glide.with(context).load(currentItem.getFile())
                .thumbnail(1.f)
                .into(holder.thumbnail);

            holder.thumbnail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ReceiptList.this, EditActivity.class);
                    intent.putExtra("PhotoItem", currentItem);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return photoList.size();
        }
    }
}


