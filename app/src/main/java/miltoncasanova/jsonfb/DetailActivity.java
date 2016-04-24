package miltoncasanova.jsonfb;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {
    private DataEntry mDataEntry;
    private Firebase rootRef;
    private String key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent i = getIntent();
        mDataEntry = (DataEntry) i.getSerializableExtra("data");
        String firebaseUrl = (String) i.getSerializableExtra("firebase");
        key = (String) i.getSerializableExtra("key");
        rootRef = new Firebase(firebaseUrl);

        final TextView nombre = (TextView) findViewById(R.id.textViewNombre);
        final TextView apellido = (TextView) findViewById(R.id.textViewApellido);
        final TextView genero = (TextView) findViewById(R.id.textViewGenero);

        nombre.setText(mDataEntry.getFirstName());
        apellido.setText(mDataEntry.getLastName());
        genero.setText(mDataEntry.getGender());

        final Button editUserBtn = (Button) findViewById(R.id.userEditBtn);
        assert editUserBtn != null;
        editUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog editUser = new Dialog(DetailActivity.this);
                editUser.setContentView(R.layout.custom_dialog);
                editUser.setTitle("Editar Usuario");
                editUser.show();

                final TextView firstNameTV = (TextView) editUser.findViewById(R.id.userNameEdit);
                firstNameTV.setText(mDataEntry.getFirstName());
                final TextView lastNameTV = (TextView) editUser.findViewById(R.id.userLastNameEdit);
                lastNameTV.setText(mDataEntry.getLastName());
                final RadioButton maleRB = (RadioButton) editUser.findViewById(R.id.maleRadioBtn);
                RadioButton femaleRB = (RadioButton) editUser.findViewById(R.id.femaleRadioBtn);
                if (mDataEntry.getGender().compareTo("male")==0){
                    maleRB.setChecked(true);
                    femaleRB.setChecked(false);
                }else{
                    maleRB.setChecked(false);
                    femaleRB.setChecked(true);
                }

                Button cancel = (Button) editUser.findViewById(R.id.userCancelSave);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editUser.dismiss();
                    }
                });
                Button save = (Button) editUser.findViewById(R.id.userSaveBtn);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = firstNameTV.getText().toString();
                        String lasteName = lastNameTV.getText().toString();
                        String gender;
                        if (maleRB.isChecked()){
                            gender = "male";
                        }else{
                            gender = "female";
                        }
                        Firebase myUser = rootRef.child(key);
                        Map<String, Object> user = new HashMap<String, Object>();
                        user.put("firstName",name);
                        user.put("gender",gender);
                        user.put("lastName", lasteName);
                        myUser.updateChildren(user);
                        nombre.setText(name);
                        apellido.setText(lasteName);
                        genero.setText(gender);
                        editUser.dismiss();
                    }
                });
            }
        });

        new DownloadImageTask((ImageView) findViewById(R.id.imageView)).execute(mDataEntry.getPicture());
    }
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
