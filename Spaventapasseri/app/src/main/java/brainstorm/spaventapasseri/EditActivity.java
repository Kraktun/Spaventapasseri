package brainstorm.spaventapasseri;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.content.DialogInterface;

public class EditActivity extends AppCompatActivity {

    PhotoItem photoItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_edit);
        photoItem = (PhotoItem) getIntent().getSerializableExtra("PhotoItem");

        //R  usa questo per il debug
        //Toast.makeText(this, photoItem.getStringDate(), Toast.LENGTH_LONG).show();

        /*private void ShowPhotos(){
        
    }*/
        final EditText txt1 = new EditText(this);
        final EditText txt2 = new EditText(this);
        final EditText txt3 = new EditText(this);



// Set the default text to a link of the Queen

        //F: Popup
        //creo e inizializzo l'alert
        AlertDialog.Builder miaAlert = new AlertDialog.Builder(this);
        miaAlert.setTitle("Modifica metadata");

        //F: non compare solo un EditText
        //   trovare modo di mettere le label con il nome del campo che si va a modificare
        miaAlert.setView(txt1);
        miaAlert.setView(txt2);
        miaAlert.setView(txt3);


        //bottone salva todo fare collegamento
        miaAlert.setPositiveButton("Salva", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //qui acquisisco le stringhe
               /* String campo1 = txt1.getText().toString();
                String campo2 = txt2.getText().toString();
                String campo3 = txt3.getText().toString();*/

            }
        })
                //bottone cancella, esce semplicemente
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });

        final AlertDialog alert = miaAlert.create();

        ImageButton btn=(ImageButton)findViewById(R.id.imgBtn1);
        btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0)
            {
                alert.show();
            }
        });

    }
}


    //R  usa questa chiamata per mostrare la foto
    //Glide.with(this).load(photoItem.getFile()).into(R.layout.il_mio_imageview);

