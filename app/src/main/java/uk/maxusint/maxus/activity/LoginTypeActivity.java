package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import uk.maxusint.maxus.R;
import uk.maxusint.maxus.activity.LoginActivity;

public class LoginTypeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_type);
    }

    public void gotoLoginActivity(View view) {
        String userType = null;
        switch (view.getId()) {
            case R.id.admin_btn:
                userType = LoginActivity.ADMIN_TYPE;
                break;
            case R.id.club_btn:
                userType = LoginActivity.CLUB_TYPE;
                break;
            case R.id.agent_btn:
                userType = LoginActivity.AGENT_TYPE;
                break;
            case R.id.normal_btn:
                userType = LoginActivity.NORMAL_TYPE;
                break;
        }
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.LOGIN_TYPE, userType);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
