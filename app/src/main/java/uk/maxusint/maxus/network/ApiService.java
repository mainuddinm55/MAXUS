package uk.maxusint.maxus.network;

import java.util.List;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;

import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import uk.maxusint.maxus.network.model.BetOptionCalculation;
import uk.maxusint.maxus.network.model.BetResult;
import uk.maxusint.maxus.network.model.Commission;
import uk.maxusint.maxus.network.model.Notification;
import uk.maxusint.maxus.network.model.Pin;
import uk.maxusint.maxus.network.model.Rank;
import uk.maxusint.maxus.network.model.SharedUser;
import uk.maxusint.maxus.network.model.Transaction;
import uk.maxusint.maxus.network.model.User;
import uk.maxusint.maxus.network.model.UserBet;
import uk.maxusint.maxus.network.model.UserBetHistory;
import uk.maxusint.maxus.network.response.AllBetResponse;
import uk.maxusint.maxus.network.response.AllUserResponse;
import uk.maxusint.maxus.network.response.BetRateResponse;
import uk.maxusint.maxus.network.response.BetResponse;
import uk.maxusint.maxus.network.response.DefaultResponse;
import uk.maxusint.maxus.network.response.InsertedMatchResponse;
import uk.maxusint.maxus.network.response.MatchBetRateResponse;
import uk.maxusint.maxus.network.response.MatchResponse;
import uk.maxusint.maxus.network.response.UserResponse;

public interface ApiService {
    //Users
    //Create Club
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
            @Field("pin_id") int pinId
    );

    @FormUrlEncoded
    @POST("createshareduser")
    Single<DefaultResponse> createSharedUser(
            @Field("name") String name,
            @Field("username") String username,
            @Field("email") String email,
            @Field("mobile") String mobile,
            @Field("district") String district,
            @Field("upazilla") String upazilla,
            @Field("up") String up,
            @Field("shared_percent") double sharedPercent
    );

    @GET("allsharedusers")
    Single<List<SharedUser>> getAllSharedUser();

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
    @POST("login")
    Single<UserResponse> login(
            @Field("email") String email,
            @Field("password") String password
    );

    //Normal User exists or not
    @GET("isuserexists/{username}")
    Single<DefaultResponse> isUserExist(@Path("username") String username);

    //Get User Balance by Id
    @GET("getuserbalance/{id}")
    Single<Double> getUserBalance(@Path("id") int id);

    //Update user balance
    @FormUrlEncoded
    @PUT("appreovedtransaction")
    Single<DefaultResponse> transactionApproved(
            @Field("amount") double amount,
            @Field("to_username") String toUsername,
            @Field("from_username") String fromUsername
    );

    //Get User notification
    @GET("usernotification/{username}")
    Single<List<Notification>> getUserNotification(@Path("username") String username);

    //Seen notification
    @PUT("seennotification/{id}")
    Single<DefaultResponse> seenNotification(@Path("id") int id);

    @GET("alluser")
    Single<AllUserResponse> getAllUsers();

    @GET("getallclubuser/{club_id}")
    Single<List<User>> getAllUsersByClub(@Path("club_id") int id);

    //Get All Club Agent
    @GET("getallclubagent/{club_id}")
    Single<List<User>> getAllAgentByClub(@Path("club_id") int id);

    //Get All Agent Users
    @GET("getallagentuser/{agentid}")
    Single<List<User>> getAllAgentUser(@Path("agentid") int agentId);

    //Get User by username
    @GET("userbyusername/{username}")
    Single<User> getUserByUsername(@Path("username") String username);

    //Get User by ID
    @GET("getuserbyid/{id}")
    Single<User> getUserById(@Path("id") int id);

    @GET("allagent")
    Single<AllUserResponse> getAllAgents();

    @GET("allclub")
    Single<AllUserResponse> getAllClubs();

    //Get Commission
    @FormUrlEncoded
    @POST("addcommission")
    Single<DefaultResponse> giveCommission(
            @Field("comm_rate") double commRate,
            @Field("amount") double amount,
            @Field("username") String username,
            @Field("from_user_id") int fromUserId,
            @Field("bet_id") int betId,
            @Field("purpose") String purpose
    );

    //Get Commission by Username
    @GET("commissionbyusername/{username}")
    Single<List<Commission>> getCommissionByUsername(@Path("username") String username);

    //Get Commission by Id
    @GET("commissionbyid/{id}")
    Single<Commission> getCommissionById(@Path("id") int id);

    //Get Total Bonus
    @GET("usertotalbonus/{username}")
    Single<Double> getUserBonus(@Path("username") String username);

    //Get Agent By ID
    @GET("agentbyid/{id}")
    Single<UserResponse> getAgentById(@Path("id") int id);

    //Get Club By ID
    @GET("clubbyid/{id}")
    Single<UserResponse> getClubByID(@Path("id") int id);

    //Get Admin
    @GET("getadmin")
    Single<User> getAdmin();

    @GET("getagentidbyreference/{reference}")
    Single<ResponseBody> getAgentIdByReference(@Path("reference") String reference);

    @FormUrlEncoded
    @POST("createsecuritypin")
    Single<DefaultResponse> createSecurityPin(@Field("pin") String pin, @Field("user_type_id") int userTypeId);

    //All Security pin
    @GET("allsecuritypin")
    Single<List<Pin>> getAllPins();

    //All User pin
    @GET("alluserpin/{id}")
    Single<List<Pin>> getUserPins(@Path("id") int id);

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

    //User Bets
    //Place Bets
    @FormUrlEncoded
    @POST("userbet")
    Single<DefaultResponse> placeUserBet(
            @Field("user_id") int userId,
            @Field("bet_id") int betId,
            @Field("bet_option_id") int betOptionId,
            @Field("bet_rate") double betRate,
            @Field("bet_amount") double betAmount,
            @Field("bet_return_amount") double betReturnAmount,
            @Field("bet_mode_id") int betModeId
    );

    @GET("getbetresult/{user_id}/{bet_id}")
    Single<BetResult> getBetResult(@Path("user_id") int userId, @Path("bet_id") int betId);

    @GET("userbetshistory/{id}")
    Single<List<UserBetHistory>> getUserBetHistory(@Path("id") int userId);

    @GET("isuseralreayplacebettoday/{user_id}")
    Single<DefaultResponse> isUserAlreadyPlaceTradeBet(@Path("user_id") int userId);

    @GET("gettotalbetotioncalculation/{bet_id}")
    Single<List<BetOptionCalculation>> getOptionTotalCalculation(@Path("bet_id") int betId);

    //Get All Users Bets by Club ID
    @GET("allusersbetsbyclub/{id}")
    Single<List<UserBet>> getUsersBetByClub(@Path("id") int clubId);

    //Get All Users Bets By Agent ID
    @GET("alluserbetsbyagentid/{id}")
    Single<List<UserBet>> getUsersBetsByAgent(@Path("id") int agentId);

    @GET("allusersbets")
    Single<List<UserBet>> getAllUserBets();

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

    @GET("optionbyid/{id}")
    Single<String> getOptionById(@Path("id") int id);

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


    //Transaction

    //add transaction
    @FormUrlEncoded
    @POST("addtransaction")
    Single<DefaultResponse> addTransaction(
            @Field("from_username") String fromUsername,
            @Field("to_username") String toUsername,
            @Field("amount") double amount,
            @Field("trans_type") String transType,
            @Field("trans_charge") double transCharge
    );


    //Update transaction status
    @FormUrlEncoded
    @PUT("updatetransacationstatus/{id}")
    Single<Transaction> updateTransactionStatus(@Field("status") String status, @Path("id") int id);

    //Get Transaction By ID
    @GET("transactionbyid/{id}")
    Single<Transaction> getTransactionById(@Path("id") int id);

    //Set To user seen
    @PUT("settouserseen/{id}")
    Single<DefaultResponse> setToUserSeen(@Path("id") int id);

    //all Deposit Transaction
    @GET("alldeposittransactions/{username}")
    Single<List<Transaction>> getAllDepositTransaction(@Path("username") String username);

    //All Withdraw Transaction
    @GET("allwithdrawtransactions/{username}")
    Single<List<Transaction>> getAllWithDrawTransaction(@Path("username") String username);

    //All Balance Transfer Transaction
    @GET("allbalancetransfertransactions/{username}")
    Single<List<Transaction>> getAllBalanceTransferTransaction(@Path("username") String username);

    //All Requested Transactions
    @GET("allrequestedtransactions/{username}")
    Single<List<Transaction>> getAllRequestedTransaction(@Path("username") String username);

    //Get all Ranks
    @GET("ranks")
    Single<List<Rank>> getAllRanks();


}
