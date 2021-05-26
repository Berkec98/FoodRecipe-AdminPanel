package com.example.yumyum;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.yumyum.Interface.ItemClickListener;
import com.example.yumyum.Model.category;
import com.example.yumyum.ViewHolder.categoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class MainActivity extends AppCompatActivity {

    Button category_button;
    MaterialTextView category_edit_name;
    FButton btn_select,btn_download;
    MaterialTextView category_edit_name_update;

    public static final int Pick_image_request=71;
    Uri EnterUri;

    FirebaseDatabase database;
    DatabaseReference cway;
    FirebaseStorage storage;
    StorageReference pway;
    FirebaseRecyclerAdapter<category, categoryViewHolder> adapter;

    RecyclerView recyclerView_category;
    RecyclerView.LayoutManager layoutManager;

    category new_category;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database= FirebaseDatabase.getInstance();
        cway=database.getReference("category");
        storage=FirebaseStorage.getInstance();
        pway=storage.getReference();

        recyclerView_category=findViewById(R.id.category_recycle);
        recyclerView_category.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView_category.setLayoutManager(layoutManager);


        category_button=findViewById(R.id.category_button);

        category_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryadd();
            }
        });

        categorydownload();
    }

    private void categorydownload() {
        FirebaseRecyclerOptions<category> options = new FirebaseRecyclerOptions.Builder<category>()
                .setQuery(cway,category.class).build();
        adapter = new FirebaseRecyclerAdapter<category, categoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull categoryViewHolder categoryViewHolder, int i, @NonNull category category) {
                categoryViewHolder.txtcategoryname.setText(category.getName());
                Picasso.with(getBaseContext()).load(category.getFoto()).into(categoryViewHolder.imageView);

                category clicked = category;
                categoryViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void OnClick(View view, int position, boolean isLongClick) {
                        // What will it do when clicked on the line

                        Intent species = new Intent(MainActivity.this,SpeciesActivity.class);
                        species.putExtra("CategoryID",adapter.getRef(position).getKey());

                        startActivity(species);

                    }
                });


            }

            @NonNull
            @Override
            public categoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_category_line_item,parent,false);
                return new categoryViewHolder(itemView);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView_category.setAdapter(adapter);
    }

    private void categoryadd() {
        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("New Category Add");
        builder.setMessage("Please enter the information");

        LayoutInflater layoutInflater=this.getLayoutInflater();
        View activity_category_add=layoutInflater.inflate(R.layout.activity_new_category_add,null);

        category_edit_name=activity_category_add.findViewById(R.id.category_edit_name);
        btn_select=activity_category_add.findViewById(R.id.btn_select);
        btn_download=activity_category_add.findViewById(R.id.btn_download);

        btn_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pictureselect();
            }
        });

        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picturedownload();
            }
        });

        builder.setView(activity_category_add);
        builder.setIcon(R.drawable.ic_image_name);

        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface mdialog, int which) {
                        //category->database
                        if (new_category != null){
                            cway.push().setValue(new_category);
                            Toast.makeText(MainActivity.this,new_category.getName()+" category added.",Toast.LENGTH_SHORT).show();
                        }
                        mdialog.dismiss();
                    }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface mdialog, int which) {
                                //sonra
                                mdialog.dismiss();
                            }
        });

        builder.show();
    }

    private void picturedownload() {
        if (EnterUri != null) {
            ProgressDialog mdialog = new ProgressDialog(this);
            mdialog.setMessage("Loading...");
            mdialog.show();
            String fotoname = UUID.randomUUID().toString();
            StorageReference fotoline = pway.child("foto/" + fotoname);
            fotoline.putFile(EnterUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mdialog.dismiss();
                    Toast.makeText(MainActivity.this, "Installed!", Toast.LENGTH_SHORT).show();
                    fotoline.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //foto->database

                            new_category= new category(category_edit_name.getText().toString(),uri.toString());


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(MainActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    mdialog.setMessage("%"+progress+"installed.");


                }
            });
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Pick_image_request && resultCode==RESULT_OK && data != null && data.getData() != null){
            EnterUri = data.getData();
            btn_select.setText("OKAY!");
        }
    }

    private void pictureselect() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Picture Choose"), Pick_image_request);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Update")){
            //Update..
            category_update(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }

        else if (item.getTitle().equals("Delete")){
            //Delete..
            categorydelete(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void category_update(String key, category item) {

        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("New Category Add");
        builder.setMessage("Please enter the information");

        LayoutInflater layoutInflater=this.getLayoutInflater();
        View activity_category_add=layoutInflater.inflate(R.layout.activity_new_category_update,null);

        category_edit_name_update=activity_category_add.findViewById(R.id.category_edit_name_update);

        builder.setView(activity_category_add);
        builder.setIcon(R.drawable.ic_update);

        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface mdialog, int which) {
                //category->update..

                item.setName(category_edit_name_update.getText().toString());
                cway.child(key).setValue(item);
                mdialog.dismiss();
            }
        });

        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface mdialog, int which) {
                //sonra
                mdialog.dismiss();
            }
        });

        builder.show();

    }

    private void categorydelete(String key) {
        cway.child(key).removeValue();
    }
}