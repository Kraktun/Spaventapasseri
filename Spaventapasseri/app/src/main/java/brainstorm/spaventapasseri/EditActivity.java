package brainstorm.spaventapasseri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class EditActivity extends AppCompatActivity {

    PhotoItem photoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_edit);
        photoItem = (PhotoItem)getIntent().getSerializableExtra("PhotoItem");

        //R  usa questo per il debug
        Toast.makeText(this, photoItem.getTitle(), Toast.LENGTH_LONG).show();

    }

    //R  usa questa chiamata per mostrare la foto
    //Glide.with(this).load(photoItem.getFile()).into(R.layout.il_mio_imageview);
}
