package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uk.maxusint.maxus.R;

public class LoginActivity extends AppCompatActivity {
    public static final String LOGIN_TYPE = "LOGIN_TYPE";
    public static final String ADMIN_TYPE = "ADMIN_TYPE";
    public static final String CLUB_TYPE = "CLUB_TYPE";
    public static final String AGENT_TYPE = "AGENT_TYPE";
    public static final String NORMAL_TYPE = "NORMAL_TYPE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        String userType = intent.getStringExtra(LOGIN_TYPE);
        switch (userType) {
            case ADMIN_TYPE:
                adminLogin();
                break;
            case CLUB_TYPE:
                clubLogin();
                break;
            case AGENT_TYPE:
                agentLogin();
                break;
            case NORMAL_TYPE:
                userLogin();
                break;
            default:
                break;
        }
    }

    private void userLogin() {

    }

    private void agentLogin() {

    }

    private void adminLogin() {

    }

    private void clubLogin() {

    }
}
