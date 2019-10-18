package ua.esputnik.support.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import ua.esputnik.support.R;
import ua.esputnik.support.WordListAdapter;
import ua.esputnik.support.db.PushEntity;
import ua.esputnik.support.db.PushViewModel;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;
    private WordListAdapter mAdapter;
    private PushViewModel pushViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pushViewModel = ViewModelProviders.of(this).get(PushViewModel.class);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CleanDbAsync(pushViewModel).execute();
            }
        });


        mRecyclerView = findViewById(R.id.recyclerview);
        mAdapter = new WordListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));



        pushViewModel.getAllPushes().observe(this, new Observer<List<PushEntity>>() {
            @Override
            public void onChanged(final List<PushEntity> pushes) {
                mAdapter.setData(pushes);
            }
        });

    }


    private class CleanDbAsync extends AsyncTask<Void, Void, Void> {

        private PushViewModel pushViewModel;

        public CleanDbAsync(PushViewModel pushViewModel) {
            this.pushViewModel = pushViewModel;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            pushViewModel.removeAll();
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(MainActivity.this, "History pushes has bean cleaned", Toast.LENGTH_LONG)
                    .show();
        }
    }


}
