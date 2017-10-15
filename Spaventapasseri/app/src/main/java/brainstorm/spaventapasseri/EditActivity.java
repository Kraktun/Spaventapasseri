package brainstorm.spaventapasseri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EditActivity extends AppCompatActivity {

    PhotoItem photoItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_edit);
        photoItem = (PhotoItem)getIntent().getSerializableExtra("PhotoItem");


    }

    //R  usa questa chiamata per mostrare la foto
    //Glide.with(this).load(photoItem.getFile()).into(R.layout.il_mio_imageview);
}
