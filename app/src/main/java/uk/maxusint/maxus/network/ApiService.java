package uk.maxusint.maxus.network;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import uk.maxusint.maxus.network.response.AdminResponse;
import uk.maxusint.maxus.network.response.AgentResponse;
import uk.maxusint.maxus.network.response.AllBetRateResponse;
import uk.maxusint.maxus.network.response.AllBetResponse;
import uk.maxusint.maxus.network.response.BetRateResponse;
import uk.maxusint.maxus.network.response.BetResponse;
import uk.maxusint.maxus.network.response.ClubResponse;
import uk.maxusint.maxus.network.response.InsertedMatchResponse;
import uk.maxusint.maxus.network.response.MatchBetRateResponse;
import uk.maxusint.maxus.network.response.MatchResponse;
import uk.maxusint.maxus.network.response.UserResponse;

public interface ApiService {
    @FormUrlEncoded
    @POST("createclub")
    Single<ResponseBody> createClub(
            @Field("name") String name,
            @Field("username") String username,
            @Field("email") String email,
            @Field("mobile") String mobile,
            @Field("password") String password,
            @Field("district") String district,
            @Field("upazilla") String upazilla,
            @Field("up") String up
    );

    @FormUrlEncoded
    @POST("clublogin")
    Single<ClubResponse> clubLogin(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("clubidbyusername/{username}")
    Single<ResponseBody> getIdByUsername(@Path("username") String username);

    @FormUrlEncoded
    @POST("createagent")
    Single<ResponseBody> registerNewAgent(
            @Field("name") String name,
            @Field("username") String username,
            @Field("email") String email,
            @Field("mobile") String mobile,
            @Field("password") String password,
            @Field("club_id") int clubId,
            @Field("district") String district,
            @Field("upazilla") String upazilla,
            @Field("up") String up
    );

    @FormUrlEncoded
    @POST("agentlogin")
    Single<AgentResponse> agentLogin(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("createuser")
    Single<ResponseBody> registerNewUser(
            @Field("name") String name,
            @Field("username") String username,
            @Field("email") String email,
            @Field("mobile") String mobile,
            @Field("password") String password,
            @Field("reference") String reference,
            @Field("agent_id") int agentId,
            @Field("district") String district,
            @Field("upazilla") String upazilla,
            @Field("up") String up,
            @Field("type_id") int typeId,
            @Field("pin_id") int pinId,
            @Field("trade_balance") float tradeBalance
    );

    @FormUrlEncoded
    @POST("createpremiumuser")
    Single<ResponseBody> registerNewPremiumUser(
            @Field("name") String name,
            @Field("username") String username,
            @Field("email") String email,
            @Field("mobile") String mobile,
            @Field("password") String password,
            @Field("reference") String reference,
            @Field("agent_id") int agentId,
            @Field("district") String district,
            @Field("upazilla") String upazilla,
            @Field("up") String up
    );

    @FormUrlEncoded
    @POST("userlogin")
    Single<UserResponse> userLogin(
            @Field("email") String email,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("adminlogin")
    Single<AdminResponse> adminLogin(
            @Field("email") String email,
            @Field("password") String password
    );

    @GET("getagentidbyreference/{reference}")
    Single<ResponseBody> getAgentIdByReference(@Path("reference") String reference);

    @FormUrlEncoded
    @POST("ispinvalid")
    Single<ResponseBody> isPinValid(
            @Field("pin") String pin,
            @Field("user_type_id") int userTypeId
    );

    @FormUrlEncoded
    @PUT("setpinused")
    Single<ResponseBody> setPinUsed(@Field("pin") String pin);

    @FormUrlEncoded
    @POST("addmatch")
    Single<InsertedMatchResponse> addMatch(
            @Field("team1") String team1,
            @Field("team2") String team2,
            @Field("date_time") String dateTime,
            @Field("tournament") String tournament,
            @Field("match_type") String matchType,
            @Field("match_format") String matchFormat
    );

    @GET("allrunningmatch")
    Single<MatchResponse> getAllRunningMatch();

    @GET("allupcomingmatch")
    Single<MatchResponse> getAllUpcomingMatch();

    @GET("allfinishmatch")
    Single<MatchResponse> getAllFinishMatch();


    @FormUrlEncoded
    @POST("createbet")
    Single<BetResponse> createBet(
            @Field("question") String question,
            @Field("started_date") String statedDate,
            @Field("match_id") int matchId,
            @Field("bet_mode") int betMode
    );

    @FormUrlEncoded
    @POST("userbet")
    Single<ResponseBody> placeUserBet(
            @Field("user_id") int userId,
            @Field("bet_id") int betId,
            @Field("bet_option_id") int betOptionId,
            @Field("bet_rate") double betRate,
            @Field("bet_amount") double betAmount,
            @Field("bet_return_amount") double betReturnAmount,
            @Field("bet_mode_id") int betModeId
    );

    @FormUrlEncoded
    @PUT("updatebet/{id}")
    Single<BetResponse> updateBet(
            @Field("question") String question,
            @Field("match_id") int matchId,
            @Field("bet_mode") int betMode,
            @Path("id") int id
    );

    @FormUrlEncoded
    @PUT("updatebetresult/{id}")
    Single<BetResponse> finishBet(
            @Field("result") String result,
            @Field("right_ans") int rightBetOptionId,
            @Path("id") int id
    );

    @PUT("cancelbet/{id}")
    Single<BetResponse> cancelBet(@Path("id") int id);

    @GET("allbets")
    Single<AllBetResponse> getAllBets();

    @FormUrlEncoded
    @POST("setbetrate")
    Single<ResponseBody> setBetRate(
            @Field("bet_id") int bet_id,
            @Field("options") String options,
            @Field("rate") float rate,
            @Field("user_type_id") int userTypeId,
            @Field("bet_mode_id") int betModeId
    );

    @FormUrlEncoded
    @PUT("updatebetrate/{id}")
    Single<BetRateResponse> updateBetRate(
            @Field("options") String options,
            @Field("rate") float rate,
            @Path("id") int betRateId
    );

    @GET("allbetratesbygroupmatchandbet/{bet_mode}/{user_type_id}")
    Single<MatchBetRateResponse> getBetRateWithMatchBetGroup(
            @Path("bet_mode") int betModeId,
            @Path("user_type_id") int userTypeId
    );

    @GET("allmatches")
    Single<MatchResponse> getAllMatch();

    @GET("matchbets/{match_id}")
    Call<AllBetResponse> getMatchBets(@Path("match_id") int id);

    @GET("betratesbybet/{id}")
    Call<AllBetRateResponse> getAllBetRateByBet(@Path("id") int id);
}
