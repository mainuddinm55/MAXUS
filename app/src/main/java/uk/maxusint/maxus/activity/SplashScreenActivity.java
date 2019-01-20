package uk.maxusint.maxus.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import uk.maxusint.maxus.R;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.utils.SharedPref;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        final User currentUser = new SharedPref(this).getUser();
        Thread timer = new Thread() {

            @Override
            public void run() {

                try {
                    sleep(2000);
                    if (currentUser != null) {
                        Intent intent;
                        switch (currentUser.getTypeId()) {
                            case User.UserType.ROYAL:
                            case User.UserType.CLASSIC:
                                intent = new Intent(SplashScreenActivity.this, UserHomeActivity.class);
                                break;
                            case User.UserType.PREMIUM:
                                intent = new Intent(SplashScreenActivity.this, PremiumUserActivity.class);
                                break;
                            case User.UserType.AGENT:
                                intent = new Intent(SplashScreenActivity.this, AgentHomeActivity.class);
                                break;
                            case User.UserType.CLUB:
                                intent = new Intent(SplashScreenActivity.this, ClubHomeActivity.class);
                                break;
                            case User.UserType.ADMIN:
                                intent = new Intent(SplashScreenActivity.this, AdminHomeActivity.class);
                                break;
                            default:
                                intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                                break;

                        }
                        startActivity(intent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    super.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }


            }
        };

        timer.start();
    }

}

