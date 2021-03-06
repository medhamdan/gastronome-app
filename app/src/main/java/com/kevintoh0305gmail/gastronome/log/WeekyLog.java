package com.kevintoh0305gmail.gastronome.log;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kevintoh0305gmail.gastronome.R;
import com.kevintoh0305gmail.gastronome.adapter.RecipeNoAddAdapter;
import com.kevintoh0305gmail.gastronome.model.Logs;
import com.kevintoh0305gmail.gastronome.model.Recipe;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class WeekyLog extends AppCompatActivity {

    private static DecimalFormat df2 = new DecimalFormat(".##");
    RecyclerView rvToday, rvTmr, rvSat, rvSun;
    FirebaseDatabase database;
    DatabaseReference logRef, ref;
    RecipeNoAddAdapter recipeAdapter, recipeAdapter2, recipeAdapter3, recipeAdapter4;
    TextView tvEstWeight;
    ArrayList<Recipe> recipes = new ArrayList<>();
    ArrayList<Recipe> logRecipes = new ArrayList<>();
    ArrayList<Recipe> tmrRecipes = new ArrayList<>();
    ArrayList<Recipe> satRecipes = new ArrayList<>();
    ArrayList<Recipe> sunRecipes = new ArrayList<>();
    FirebaseAuth mAuth;
    Calendar cal;
    double totalCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weeky_log);
        cal = Calendar.getInstance();
        totalCal = 0;
        tvEstWeight = findViewById(R.id.tvEstWeight);

        //Wed is 4
        Log.d("Day", ""+ cal.get(Calendar.DAY_OF_WEEK));


        rvToday = findViewById(R.id.rvToday);
        rvTmr = findViewById(R.id.rvTmr);
        rvSat = findViewById(R.id.rvSat);
        rvSun = findViewById(R.id.rvSun);
        //final String Uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mAuth = FirebaseAuth.getInstance();

        //String userName = mAuth.getCurrentUser().get

        database = FirebaseDatabase.getInstance();
        //String userEmail = mAuth.getCurrentUser().getEmail();
        //Log.d("Email:", userEmail);
        //Log.d("UID", mAuth.getCurrentUser().getUid() );

        GetRecipes();



        logRef = database.getReference("ZLogs");
        logRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Recipe> logRecipes = new ArrayList<>();
                ArrayList<Recipe> tmrRecipes = new ArrayList<>();
                ArrayList<Recipe> satRecipes = new ArrayList<>();
                ArrayList<Recipe> sunRecipes = new ArrayList<>();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    Logs log = ds.getValue(Logs.class);
                    //if (log.getEmail().equals("dom@gmail.com")) { //NEED CHANGE
                    if (log.getDay().equals("" + cal.get(Calendar.DAY_OF_WEEK))) { // FOR TODAY
                        for (Recipe r : recipes) {
                            if (log.getTitle().equals(r.getTitle())) {
                                logRecipes.add(r);
                                totalCal += r.getCalories();
                            }
                        }
                    }
                    if (log.getDay().equals("" + (cal.get(Calendar.DAY_OF_WEEK) + 1))) {
                        for (Recipe r : recipes) {
                            if (log.getTitle().equals(r.getTitle())) {
                                tmrRecipes.add(r);
                                totalCal += r.getCalories();
                            }
                        }
                    }
                    if (log.getDay().equals("7")) {
                        for (Recipe r: recipes) {
                            if (log.getTitle().equals(r.getTitle())){
                                Log.d("SAT", "SAT");
                                satRecipes.add(r);
                                totalCal += r.getCalories();
                            }
                        }
                    }
                    if (log.getDay().equals("1")) {
                        for (Recipe r : recipes) {
                            if (log.getTitle().equals(r.getTitle())) {
                                sunRecipes.add(r);
                                totalCal += r.getCalories();
                            }

                        }
                    }
                }

                Log.d("Total Calories: ", "" + totalCal);
                double supposedCal = 8800;
                double excessCal = supposedCal - totalCal;

                double weightChange = excessCal/8;
                double changeInG = weightChange / 1000;

                Log.d("Weight Changed: ", "" +  changeInG);

                //GET THE USER WEIGHT
                double userWeight = 60;
                double newWeight = userWeight - changeInG;
                Log.d("New Weight: " , "" + df2.format(newWeight));

                tvEstWeight.setText("Estimated Weight after the week: " + df2.format(newWeight) + "kg");




                recipeAdapter = new RecipeNoAddAdapter(WeekyLog.this, logRecipes);
                rvToday.setAdapter(recipeAdapter);
                LinearLayoutManager manager = new LinearLayoutManager(WeekyLog.this);
                rvToday.setLayoutManager(manager);
                rvToday.setItemAnimator(new DefaultItemAnimator());

                recipeAdapter2 = new RecipeNoAddAdapter(WeekyLog.this, tmrRecipes);
                rvTmr.setAdapter(recipeAdapter2);
                LinearLayoutManager manager2 = new LinearLayoutManager(WeekyLog.this);
                rvTmr.setLayoutManager(manager2);
                rvTmr.setItemAnimator(new DefaultItemAnimator());

                recipeAdapter3 = new RecipeNoAddAdapter(WeekyLog.this, satRecipes);
                rvSat.setAdapter(recipeAdapter3);
                LinearLayoutManager manager3 = new LinearLayoutManager(WeekyLog.this);
                rvSat.setLayoutManager(manager3);
                rvSat.setItemAnimator(new DefaultItemAnimator());

                recipeAdapter4 = new RecipeNoAddAdapter(WeekyLog.this, sunRecipes);
                rvSun.setAdapter(recipeAdapter4);
                LinearLayoutManager manager4 = new LinearLayoutManager(WeekyLog.this);
                rvSun.setLayoutManager(manager4);
                rvSun.setItemAnimator(new DefaultItemAnimator());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("The read failed: " , "" + databaseError.getCode());
            }
        });
    }

    public void GetRecipes()
    {
        ref = database.getReference("Recipes");




        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Am i second?", "TEST");
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Recipe recipe = ds.getValue(Recipe.class);
                    recipes.add(recipe);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("The read failed: " , "" + databaseError.getCode());
            }
        });

    }
}



