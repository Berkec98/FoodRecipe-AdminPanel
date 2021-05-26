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
import com.example.yumyum.Model.Food;
import com.example.yumyum.Model.Species;
import com.example.yumyum.ViewHolder.FoodViewHolder;
import com.example.yumyum.ViewHolder.SpeciesViewHolder;
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

public class FoodActivity extends AppCompatActivity {

    Button food_button;
    MaterialTextView food_edit_name;
    MaterialTextView foodmaterials_edit_name;
    MaterialTextView recipes_edit_name;
    MaterialTextView recipeslink_edit_name;
    FButton btn_food_select,btn_food_download;

    FirebaseDatabase database;
    DatabaseReference fway;
    FirebaseStorage storage;
    StorageReference pway;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    public static final int Pick_image_request=71;
    Uri EnterUri;

    String SpeciesID="";
    Food new_food;

    RecyclerView recyclerView_food;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        database= FirebaseDatabase.getInstance();
        fway=database.getReference("Food");
        storage=FirebaseStorage.getInstance();
        pway=storage.getReference();

        recyclerView_food=findViewById(R.id.recycle_food);
        recyclerView_food.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView_food.setLayoutManager(layoutManager);

        food_button=findViewById(R.id.food_button);

        food_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodadd();
            }
        });

        if (getIntent() != null){
            SpeciesID=getIntent().getStringExtra("SpeciesID");
        }
        if (!SpeciesID.isEmpty()){
            fooddownload(SpeciesID);
        }
    }

    private void fooddownload(String speciesID) {
        Query filter= fway.orderByChild("speciesID").equalTo(speciesID);
        FirebaseRecyclerOptions<Food> options = new FirebaseRecyclerOptions.Builder<Food>()
                .setQuery(filter,Food.class).build();
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FoodViewHolder holder, int i, @NonNull Food model) {
                holder.txtfoodname.setText(model.getFoodname());
                holder.txtfoodmaterials.setText(model.getFoodmaterials());
                holder.txtfoodrecipes.setText(model.getFoodrecipes());
                holder.txtfoodrecipeslink.setText(model.getFoodrecipeslink());
                Picasso.with(getBaseContext()).load(model.getFoto()).into(holder.imageView);

                Food clicked = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void OnClick(View view, int position, boolean isLongClick) {
                        // What will it do when clicked on the line
                        Intent species = new Intent(FoodActivity.this,FoodActivity.class);
                        species.putExtra("SpeciesID",adapter.getRef(position).getKey());

                        startActivity(species);

                    }
                });


            }

            @NonNull
            @Override
            public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.activity_food_line_item,parent,false);
                return new FoodViewHolder(itemView);
            }
        };

        adapter.startListening();
        adapter.notifyDataSetChanged();
        recyclerView_food.setAdapter(adapter);
    }

    private void foodadd() {

        AlertDialog.Builder builder= new AlertDialog.Builder(FoodActivity.this);
        builder.setTitle("New Food Add");
        builder.setMessage("Please enter the information");

        LayoutInflater layoutInflater=this.getLayoutInflater();
        View activity_foods_add=layoutInflater.inflate(R.layout.activity_new_food_add,null);

        food_edit_name=activity_foods_add.findViewById(R.id.food_edit_name);
        foodmaterials_edit_name=activity_foods_add.findViewById(R.id.foodmaterials_edit_name);
        recipes_edit_name=activity_foods_add.findViewById(R.id.recipes_edit_name);
        recipeslink_edit_name=activity_foods_add.findViewById(R.id.recipeslink_edit_name);
        btn_food_select=activity_foods_add.findViewById(R.id.btn_food_select);
        btn_food_download=activity_foods_add.findViewById(R.id.btn_food_download);

        btn_food_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picturefood_select();
            }
        });

        btn_food_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picturefood_download();
            }
        });

        builder.setView(activity_foods_add);
        builder.setIcon(R.drawable.ic_food_add);

        builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface mdialog, int which) {
                //food->database
                if (new_food != null){
                    fway.push().setValue(new_food);
                    Toast.makeText(FoodActivity.this,new_food.getFoodname()+" species added.",Toast.LENGTH_SHORT).show();
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

    private void picturefood_download() {

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
                    Toast.makeText(FoodActivity.this, "Installed!", Toast.LENGTH_SHORT).show();
                    fotoline.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //foto->database

                            new_food= new Food();
                            new_food.setFoodname(food_edit_name.getText().toString());
                            new_food.setFoodmaterials(foodmaterials_edit_name.getText().toString());
                            new_food.setFoodrecipes(recipes_edit_name.getText().toString());
                            new_food.setFoodrecipeslink(recipeslink_edit_name.getText().toString());
                            new_food.setFoto(uri.toString());
                            new_food.setSpeciesID(SpeciesID);




                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mdialog.dismiss();
                    Toast.makeText(FoodActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==Pick_image_request && resultCode==RESULT_OK && data != null && data.getData() != null){
            EnterUri = data.getData();
            btn_food_select.setText("OKAY!");
        }
    }

    private void picturefood_select() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Picture Choose"), Pick_image_request);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Update")){
            //Update..
            food_update(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }

        else if (item.getTitle().equals("Delete")){
            //Delete..
            fooddelete(adapter.getRef(item.getOrder()).getKey());
        }

        return super.onContextItemSelected(item);
    }

    private void food_update(String key, Food item) {

        AlertDialog.Builder builder= new AlertDialog.Builder(FoodActivity.this);
        builder.setTitle("Food Update");
        builder.setMessage("Please enter the information");

        LayoutInflater layoutInflater=this.getLayoutInflater();
        View activity_foods_add=layoutInflater.inflate(R.layout.activity_new_food_add,null);

        food_edit_name=activity_foods_add.findViewById(R.id.food_edit_name);
        foodmaterials_edit_name=activity_foods_add.findViewById(R.id.foodmaterials_edit_name);
        recipes_edit_name=activity_foods_add.findViewById(R.id.recipes_edit_name);
        recipeslink_edit_name=activity_foods_add.findViewById(R.id.recipeslink_edit_name);
        btn_food_select=activity_foods_add.findViewById(R.id.btn_food_select);
        btn_food_download=activity_foods_add.findViewById(R.id.btn_food_download);

        btn_food_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picturefood_select();
            }
        });

        btn_food_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picturechange(item);
            }
        });

        builder.setView(activity_foods_add);
        builder.setIcon(R.drawable.ic_food_update);

        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface mdialog, int which) {
                //category->update..

                item.setFoodname(food_edit_name.getText().toString());
                item.setFoodmaterials(foodmaterials_edit_name.getText().toString());
                item.setFoodrecipes(recipes_edit_name.getText().toString());
                item.setFoodrecipeslink(recipeslink_edit_name.getText().toString());
                fway.child(key).setValue(item);
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

    private void picturechange(Food item) {

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
                    Toast.makeText(FoodActivity.this, "Installed!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(FoodActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void fooddelete(String key) {
        fway.child(key).removeValue();
    }
    
    
}