package brainstorm.spaventapasseri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.content.DialogInterface;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class EditActivity extends AppCompatActivity {

    PhotoItem photoItem;
    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_edit);
        photoItem = (PhotoItem) getIntent().getSerializableExtra("PhotoItem");


        Glide.with(this).load(photoItem.getFile()).into((ImageView)findViewById(R.id.photoPreview));
        title = (TextView)findViewById(R.id.titleTextView);
        title.setText(photoItem.getTitle());
        ((TextView)findViewById(R.id.dateTextView)).setText(photoItem.getStringDate());


        ImageButton btn=(ImageButton)findViewById(R.id.imgBtn1);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0)
            {
                final EditText txt1 = new EditText(EditActivity.this);
                //final EditText txt2 = new EditText(this);
                //final EditText txt3 = new EditText(this);

                txt1.setText(photoItem.getTitle());

                //F: Popup
                //creo e inizializzo l'alert
                AlertDialog.Builder miaAlert = new AlertDialog.Builder(EditActivity.this);
                miaAlert.setTitle(R.string.set_title);

                //F: non compare solo un EditText
                //   trovare modo di mettere le label con il nome del campo che si va a modificare
                miaAlert.setView(txt1);
                //miaAlert.setView(txt2);
                //miaAlert.setView(txt3);


                //bottone salva todo fare collegamento
                miaAlert.setPositiveButton(R.string.dialog_save, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //qui acquisisco le stringhe
                                String campo1 = txt1.getText().toString();
                                /*String campo2 = txt2.getText().toString();
                                String campo3 = txt3.getText().toString();*/


                                photoItem.setTitle(campo1);
                                title.setText(campo1);
                            }
                        })
                        //bottone cancella, esce semplicemente
                        .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });

                final AlertDialog alert = miaAlert.create();
                alert.show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_delete) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.dialog_delete_message)
                    .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            photoItem.deleteFile();

                            //R  Terminate current activity (go back)
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

//R  usa questo per il debug
//Toast.makeText(this, photoItem.getStringDate(), Toast.LENGTH_LONG).show();

