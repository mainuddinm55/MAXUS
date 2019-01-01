package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uk.maxusint.maxus.R;
import uk.maxusint.maxus.fragment.AddAgentFragment;
import uk.maxusint.maxus.fragment.AddClubFragment;
import uk.maxusint.maxus.fragment.AddUserFragment;
import uk.maxusint.maxus.listener.FragmentLoader;

public class RegisterActivity extends AppCompatActivity implements FragmentLoader {
    private static final String TAG = "RegisterActivity";
    public static final String REGISTER_TYPE = "uk.maxusint.maxus.activity.REGISTER_TYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Intent intent = getIntent();

        String registerType = intent.getStringExtra(REGISTER_TYPE);
        switch (registerType) {
            case LoginActivity.AGENT_TYPE:
                loadFragment(new AddAgentFragment(),AddAgentFragment.TAG);
                break;
            case LoginActivity.CLUB_TYPE:
                loadFragment(new AddClubFragment(),AddClubFragment.TAG);
                break;
            case LoginActivity.NORMAL_TYPE:
                loadFragment(new AddUserFragment(),AddUserFragment.TAG);
                break;
        }
    }


    @Override
    public void loadFragment(Fragment fragment, String tag) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment, tag);
        //ft.addToBackStack(tag);
        ft.commit();
    }
}
