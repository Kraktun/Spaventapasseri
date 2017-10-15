//R  esempio layout a tessere: https://www.androidhive.info/2016/05/android-working-with-card-view-and-recycler-view/


package brainstorm.spaventapasseri;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return true;
                }
            }
            return false;
        }
    };


    private PhotosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.receipt_list_title); //R  Non riesco a cambiare il titolo in AndroidManifest senza cambiare il nome dell'app
        setContentView(R.layout.activity_receipt_list);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ReceiptList.this, CameraAcc.class);
                startActivity(intent);
            }
        });

        adapter = new PhotosAdapter(this);

        RecyclerView photoListView = (RecyclerView) findViewById(R.id.photoListView);
        photoListView.setLayoutManager(new GridLayoutManager(this, 1));
        photoListView.setItemAnimator(new DefaultItemAnimator());
        photoListView.setAdapter(adapter);


        //debug
        findViewById(R.id.testbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReceiptList.this, EditActivity.class);
                //Glide.with(ReceiptList.this).load(R.drawable.ic_test_scontrino).into(new SimpleTarget(200, 266) {
                intent.putExtra("PhotoItem", new PhotoItem(new File(Environment.getExternalStorageDirectory().getPath() + "/Spaventapasseri/test.png")));
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchPhotosInfo();
    }

    //R  Questo metodo trova le foto e salva le loro informazioni.
    //   Si occupera' Glide di caricarle e mostrarle ottimizzando la memoria
    private void fetchPhotosInfo() {
        //File[] photos = new File(Environment.getExternalStorageDirectory(), "Spaventapasseri").listFiles(IMAGE_FILTER);
//        File[] photos = getFilesDir().listFiles(IMAGE_FILTER);
//
//        //boolean a  = new File(Environment.getExternalStorageDirectory(), "Spaventapasseri").exists();
//        Toast.makeText(this, String.valueOf(getFilesDir()), Toast.LENGTH_LONG).show();
//        Toast.makeText(this, String.valueOf(new File(Environment.getExternalStorageDirectory(), "Spaventapasseri")), Toast.LENGTH_LONG).show();
//
//
//        List<PhotoItem> photoList = new ArrayList<>();
//        for (File file : photos) {
//            photoList.add(new PhotoItem(file));
//        }
//
//        Collections.sort(photoList, new Comparator<PhotoItem>() {
//            @Override
//            public int compare(PhotoItem p1, PhotoItem p2) {
//                return -p1.getDate().compareTo(p2.getDate()); //R  il segno meno e' per invertire l'ordine
//            }
//        });
//
//        adapter.setPhotoList(photoList);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        //getMenuInflater().inflate(R.menu.menu_receipt_list, menu);
//        //return true;
//        return false;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }



    class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoCardHolder> {

        private Context context;
        private List<PhotoItem> photoList;

        class PhotoCardHolder extends RecyclerView.ViewHolder {
            //public TextView title;
            ImageView thumbnail;//, dotsMenu;

            PhotoCardHolder(View view) {
                super(view);
                //title = (TextView) view.findViewById(R.id.title);
                thumbnail = (ImageView)view.findViewById(R.id.thumbnail);
            }
        }


        PhotosAdapter(Context context) {
            this.context = context;
            this.photoList = new ArrayList<>();
        }

        void setPhotoList(List<PhotoItem> photoList){
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
        public void onBindViewHolder(final PhotoCardHolder holder, int position) {
            PhotoItem currentItem = photoList.get(position);
            //holder.title.setText(currentItem.title);

            //R  thumbnail ratio 20%
            Glide.with(context).load(currentItem.getFile())
                               .thumbnail(0.2f)
                               .into(holder.thumbnail);


//            holder.dotsMenu.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    showPopupMenu(holder.dotsMenu);
//                }
//            });
        }

//        /**
//         * Showing popup menu when tapping on 3 dots
//         */
//        private void showPopupMenu(View view) {
//            // inflate menu
//            PopupMenu popup = new PopupMenu(context, view);
//            popup.getMenuInflater().inflate(R.menu.menu_album, popup.getMenu());
//            popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
//            popup.show();
//        }
//
//        /**
//         * Click listener for popup menu items
//         */
//        class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
//
//            public MyMenuItemClickListener() {
//            }
//
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                switch (menuItem.getItemId()) {
//                    case R.id.action_add_favourite:
//                        Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
//                        return true;
//                    case R.id.action_play_next:
//                        Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
//                        return true;
//                    default:
//                }
//                return false;
//            }
//        }

        @Override
        public int getItemCount() {
            return photoList.size();
        }
    }

}


