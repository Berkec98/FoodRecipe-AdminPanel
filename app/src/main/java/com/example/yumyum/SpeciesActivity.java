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
import com.example.yumyum.Model.Species;
import com.example.yumyum.Model.category;
import com.example.yumyum.ViewHolder.SpeciesViewHolder;
import com.example.yumyum.ViewHolder.categoryViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import info.hoang8f.widget.FButton;

public class SpeciesActivity extends AppCompatActivity {

    Button species_button;
    MaterialTextView species_edit_name;
    FButton btn_species_select,btn_species_download;

    public static final int Pick_image_request=71;
    Uri EnterUri;

    FirebaseDatabase database;
    DatabaseReference sway;
    FirebaseStorage storage;
    StorageReference pway;
    FirebaseRecyclerAdapter<Species, SpeciesViewHolder> adapter;

    String categoryID="";

    Species newSpecies;

    RecyclerView recyclerView_species;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_species);

        database= FirebaseDatabase.getInstance();
        sway=database.getReference("Species");
        storage=FirebaseStorage.getInstance();
        pway=storage.getReference();


        recyclerView_species=findViewById(R.id.species_recycle);
        recyclerView_species.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView_species.setLayoutManager(layoutManager);

        species_button=findViewById(R.id.species_button);
        species_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speciesadd();
            }
        });

        if (getIntent() != null){
            categoryID=getIntent().getStringExtra("CategoryID");
        }
        if (!categoryID.isEmpty()){
            speciesdownload(categoryID);
        }
    }

    private void speciesdownload(String categoryID) {
        Query filter= sway.orderByChild("categoryID").equalTo(categoryID);
        FirebaseRecyclerOptions<Species> options = new FirebaseRecyclerOptions.Builder<Species>()
                .setQuery(filter,Species.class).build();
        adapter = new FirebaseRecyclerAdapter<Species, SpeciesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SpeciesViewHolder holder, int i, @NonNull Species model) {
                holder.txtspeciesname.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getFoto()).into(holder.imageView);

                Species clicked = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void OnClick(View view, int position, boolean isLongClick) {
                        // What will it do when clicked on the line
                        Intent species = new Intent(SpeciesActivity.this,FoodActivity.class);
                        species.putExtra("SpeciesID",adapter.getRef(position).getKey());

                        startActivity(species);

                    }
                });


            }

            @NonNull
            @Override
            public SpeciesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_species_line_item,parent,false);
                return new SpeciesViewHolder(itemView);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView_species.setAdapter(adapter);

    }

    private void speciesadd() {

        AlertDialog.Builder builder= new AlertDialog.Builder(SpeciesActivity.this);
        builder.setTitle("New Species Add");
        builder.setMessage("Please enter the information");

        LayoutInflater layoutInflater=this.getLayoutInflater();
        View activity_species_add=layoutInflater.inflate(R.layout.activity_new_species_add,null);

        species_edit_name=activity_species_add.findViewById(R.id.species_edit_name);
        btn_species_select=activity_species_add.findViewById(R.id.btn_species_select);
        btn_species_download=activity_species_add.findViewById(R.id.btn_species_download);

        btn_species_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picturespecies_select();
            }
        });

        btn_species_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picturespecies_download();
            }
        });

        builder.setView(activity_species_add);
        builder.setIcon(R.drawable.ic_species_add);

        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface mdialog, int which) {
                //species->database
                if (newSpecies != null){
                    sway.push().setValue(newSpecies);
                    Toast.makeText(SpeciesActivity.this,newSpecies.getName()+" species added.",Toast.LENGTH_SHORT).show();
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

    private void picturespecies_download() {

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
                    Toast.makeText(SpeciesActivity.this, "Installed!", Toast.LENGTH_SHORT).show();
                    fotoline.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //foto->database

                            newSpecies= new Species(species_edit_name.getText().toString(),uri.toString(),categoryID);


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(SpeciesActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Update")){
            //Update..
            species_update(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }

        else if (item.getTitle().equals("Delete")){
            //Delete..
            speciesdelete(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void species_update(String key, Species item) {

        AlertDialog.Builder builder= new AlertDialog.Builder(SpeciesActivity.this);
        builder.setTitle("New Species Add");
        builder.setMessage("Please enter the information");

        LayoutInflater layoutInflater=this.getLayoutInflater();
        View activity_species_add=layoutInflater.inflate(R.layout.activity_new_species_add,null);

        species_edit_name=activity_species_add.findViewById(R.id.species_edit_name);
        btn_species_download=activity_species_add.findViewById(R.id.btn_species_download);
        btn_species_select=activity_species_add.findViewById(R.id.btn_species_select);

        btn_species_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picturespecies_select();
            }
        });

        btn_species_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picturechange(item);
            }
        });

        builder.setView(activity_species_add);
        builder.setIcon(R.drawable.ic_update);

        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface mdialog, int which) {
                //category->update..

                item.setName(species_edit_name.getText().toString());
                sway.child(key).setValue(item);
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

    private void picturechange(Species item) {
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
                    Toast.makeText(SpeciesActivity.this, "Installed!", Toast.LENGTH_SHORT).show();
                    fotoline.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //foto->database

                            item.setFoto(uri.toString());


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(SpeciesActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void speciesdelete(String key) {
        sway.child(key).removeValue();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Pick_image_request && resultCode==RESULT_OK && data != null && data.getData() != null){
            EnterUri = data.getData();
            btn_species_select.setText("OKAY!");
        }
    }

    private void picturespecies_select() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Picture Choose"), Pick_image_request);
    }
}