package uk.maxusint.maxus.utils;

import android.content.Context;
import android.content.SharedPreferences;

import uk.maxusint.maxus.network.model.User;

public class SharedPref {
    private SharedPreferences sharedPreferences;

    private static final String SHARED_PREF_NAME = "maxus_pref";

    private static final String USER_ID = "USER_ID";
    private static final String USER_NAME = "USER_NAME";
    private static final String USER_USERNAME = "USER_USERNAME";
    private static final String USER_EMAIL = "USER_EMAIL";
    private static final String USER_MOBILE = "USER_MOBILE";
    private static final String CLUB_ID = "CLUB_ID";
    private static final String USER_REFERENCE = "USER_REFERENCE";
    private static final String USER_AGENT_ID = "USER_AGENT_ID";
    private static final String USER_DISTRICT = "USER_DISTRICT";
    private static final String USER_UPAZILLA = "USER_UPAZILLA";
    private static final String USER_UP = "USER_UP";
    private static final String USER_STATUS = "USER_STATUS";
    private static final String USER_TYPE_ID = "USER_TYPE_ID";
    private static final String USER_BALANCE = "USER_BALANCE";
    private static final String USER_RANK_ID = "USER_RANK_ID";

    public SharedPref(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public void putUser(User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(USER_ID, user.getUserId());
        editor.putString(USER_NAME, user.getName());
        editor.putString(USER_USERNAME, user.getUsername());
        editor.putString(USER_EMAIL, user.getEmail());
        editor.putString(USER_MOBILE, user.getMobile());
        editor.putInt(CLUB_ID, user.getClubId());
        editor.putString(USER_REFERENCE, user.getReference());
        editor.putInt(USER_AGENT_ID, user.getAgentId());
        editor.putString(USER_DISTRICT, user.getDistrict());
        editor.putString(USER_UPAZILLA, user.getUpazilla());
        editor.putString(USER_UP, user.getUp());
        editor.putString(USER_STATUS, user.getStatus());
        editor.putFloat(USER_BALANCE, (float) user.getTotalBalance());
        editor.putInt(USER_RANK_ID, user.getRankId());
        editor.putInt(USER_TYPE_ID, user.getTypeId());
        editor.apply();
    }

    public void clearUser() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_ID);
        editor.remove(USER_NAME);
        editor.remove(USER_USERNAME);
        editor.remove(USER_EMAIL);
        editor.remove(USER_MOBILE);
        editor.remove(CLUB_ID);
        editor.remove(USER_REFERENCE);
        editor.remove(USER_AGENT_ID);
        editor.remove(USER_DISTRICT);
        editor.remove(USER_UPAZILLA);
        editor.remove(USER_UP);
        editor.remove(USER_STATUS);
        editor.remove(USER_BALANCE);
        editor.remove(USER_RANK_ID);
        editor.remove(USER_TYPE_ID);
        editor.apply();

    }

    public User getUser() {
        int id = sharedPreferences.getInt(USER_ID, -1);
        String name = sharedPreferences.getString(USER_NAME, null);
        String username = sharedPreferences.getString(USER_USERNAME, null);
        String email = sharedPreferences.getString(USER_EMAIL, null);
        String mobile = sharedPreferences.getString(USER_MOBILE, null);
        int clubId = sharedPreferences.getInt(CLUB_ID, -1);
        String reference = sharedPreferences.getString(USER_REFERENCE, null);
        int agentId = sharedPreferences.getInt(USER_AGENT_ID, -1);
        String district = sharedPreferences.getString(USER_DISTRICT, null);
        String upazilla = sharedPreferences.getString(USER_UPAZILLA, null);
        String up = sharedPreferences.getString(USER_UP, null);
        double balance = sharedPreferences.getFloat(USER_BALANCE, -1);
        String status = sharedPreferences.getString(USER_STATUS, null);
        int rankId = sharedPreferences.getInt(USER_RANK_ID, -1);
        int userTypeId = sharedPreferences.getInt(USER_TYPE_ID, -1);
        if (id != -1) {
            return new User(id, name, username, email, mobile, clubId, reference, agentId, district, upazilla, up, balance, status, rankId, userTypeId);
        } else {
            return null;
        }
    }
}
