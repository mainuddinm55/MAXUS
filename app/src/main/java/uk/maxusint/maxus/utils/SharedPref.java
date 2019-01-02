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
    private static final String USER_REFERENCE = "USER_REFERENCE";
    private static final String USER_AGENT_ID = "USER_AGENT_ID";
    private static final String USER_DISTRICT = "USER_DISTRICT";
    private static final String USER_UPAZILLA = "USER_UPAZILLA";
    private static final String USER_UP = "USER_UP";
    private static final String USER_TYPE_ID = "USER_TYPE_ID";
    private static final String USER_ADVANCED_BALANCE = "USER_ADVANCED_BALANCE";
    private static final String USER_TRADE_BALANCE = "USER_TRADE_BALANCE";
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
        editor.putString(USER_REFERENCE, user.getReference());
        editor.putInt(USER_AGENT_ID, user.getAgentId());
        editor.putString(USER_DISTRICT, user.getDistrict());
        editor.putString(USER_UPAZILLA, user.getUpazilla());
        editor.putString(USER_UP, user.getUp());
        editor.putInt(USER_TYPE_ID, user.getTypeId());
        editor.putFloat(USER_ADVANCED_BALANCE, user.getAdvancedBalance());
        editor.putFloat(USER_TRADE_BALANCE, user.getTradeBalance());
        editor.putInt(USER_RANK_ID, user.getRankId());
        editor.apply();
    }

    public User getUser() {
        int id = sharedPreferences.getInt(USER_ID, -1);
        String name = sharedPreferences.getString(USER_NAME, null);
        String username = sharedPreferences.getString(USER_USERNAME, null);
        String email = sharedPreferences.getString(USER_EMAIL, null);
        String mobile = sharedPreferences.getString(USER_MOBILE, null);
        String reference = sharedPreferences.getString(USER_REFERENCE, null);
        int agentId = sharedPreferences.getInt(USER_AGENT_ID, -1);
        String district = sharedPreferences.getString(USER_DISTRICT, null);
        String upazilla = sharedPreferences.getString(USER_UPAZILLA, null);
        String up = sharedPreferences.getString(USER_UP, null);
        int userTypeId = sharedPreferences.getInt(USER_TYPE_ID, -1);
        float advancedBalance = sharedPreferences.getFloat(USER_ADVANCED_BALANCE, -1);
        float tradeBalance = sharedPreferences.getFloat(USER_TRADE_BALANCE, -1);
        int rankId = sharedPreferences.getInt(USER_RANK_ID, -1);

        if (id != -1) {
            return new User(id, name, username, email, mobile, reference, agentId, district, upazilla, up, userTypeId, advancedBalance, tradeBalance, rankId);
        } else {
            return null;
        }
    }
}
