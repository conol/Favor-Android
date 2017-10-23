package jp.co.conol.favor_android.activity;

import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import jp.co.conol.favor_android.R;
import jp.co.conol.favor_android.adapter.EnteringHistoryRecyclerAdapter;

public class EnteringHistoryActivity extends AppCompatActivity {

    private RecyclerView mEnteringHistoryRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entering_history);

        mEnteringHistoryRecyclerView = (RecyclerView) findViewById(R.id.EnteringHistoryRecyclerView);

        // レイアウトマネージャーのセット
        mEnteringHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // アダプターのセット
        EnteringHistoryRecyclerAdapter adapter = new EnteringHistoryRecyclerAdapter(this);
        mEnteringHistoryRecyclerView.setAdapter(adapter);
    }
}
