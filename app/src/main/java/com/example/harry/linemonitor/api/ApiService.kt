package com.example.harry.submission_2kade.api


import com.example.harry.linemonitor.data.*
import io.reactivex.Observable
import retrofit2.http.*

interface ApiService {


    //    USER END POINT
//    - LOGIN
    @FormUrlEncoded
    @POST("login")
    fun login(
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("fcm_token") fcm_token: String
    ): Observable<LoginResponse>


    @FormUrlEncoded
    @POST("register")
    fun register(
            @Field("name") name: String,
            @Field("email") email: String,
            @Field("password") password: String,
            @Field("c_password") c_password: String
    ): Observable<RegisterResponse>







    //    NODE END POINT
//    - GET ALL NODE
//    - NODE BY ID
//    - STORE NODE
//    - UPDATE NODE
//    - DELETE NODE
    @GET("node-master/")
    fun getNodeList(): Observable<List<NodeMaster>>

    @GET("node-master/{id}")
    fun getNodeById(@Path("id") id: String): Observable<NodeMaster>

    @FormUrlEncoded
    @POST("node-master/store")
    fun storeNodeMaster(
            @Field("sn") nodeSN: String,
            @Field("phone_number") phone_number: String,
            @Field("lat") lat: String,
            @Field("lng") lng: String,
            @Field("isStartNode") isStartNode: String,
            @Field("pressOffset") pressOffset: String,
            @Field("liquidFlowKonstanta") liquidFlowKonstanta: String,
            @Field("flow_rate_model") flow_rate_model: String,
            @Field("pressure_tranducer_model") pressure_tranducer_model: String
    ): Observable<NodeMaster>


    @FormUrlEncoded
    @POST("node-master/update/{node_id}")
    fun updateNodeMester(
            @Path("node_id") id: String,
            @Field("sn") nodeSN: String,
            @Field("phone_number") phone_number: String,
            @Field("lat") lat: String,
            @Field("lng") lng: String,
            @Field("isStartNode") isStartNode: String,
            @Field("pressOffset") pressOffset: String,
            @Field("liquidFlowKonstanta") liquidFlowKonstanta: String,
            @Field("flow_rate_model") flow_rate_model: String,
            @Field("pressure_tranducer_model") pressure_tranducer_model: String
    ): Observable<NodeMaster>


    @POST("node-master/destroy/{node_id}")
    fun deleteNodeMaster(@Path("node_id") id: String): Observable<ResponseDeleteSuccess>


//    LINE ENDPOINT
//    - GET ALL LINE
//    - LINE BY ID
//    - UPDATE LINE
//    - STORE LINE
//    - DELETE LINE

    @GET("line-master/lines")
    fun getLineMasterLinesMaps(): Observable<List<LineMasterMap>>

    @GET("line-master/{id}")
    fun getLineMasterLineMpasById(@Path("id") id: String): Observable<List<LineMasterMap>>


    @FormUrlEncoded
    @POST("line-master/store")
    fun storeLineMaster(
            @Field("name") name: String,
            @Field("start") start: String,
            @Field("end") end: String,
            @Field("start_node_id") start_node_id: String,
            @Field("end_node_id") end_node_id: String,
            @Field("distance") distance: String,
            @Field("diameter") diameter: String,
            @Field("thicknes") thicknes: String,
            @Field("manufacture") manufacture: String
    ): Observable<LineMaster>


    @FormUrlEncoded
    @POST("line-master/update/{id}")
    fun updateLineMaster(
            @Path("id") id: String,
            @Field("name") name: String,
            @Field("start") start: String,
            @Field("end") end: String,
            @Field("start_node_id") start_node_id: String,
            @Field("end_node_id") end_node_id: String,
            @Field("distance") distance: String,
            @Field("diameter") diameter: String,
            @Field("thicknes") thicknes: String,
            @Field("manufacture") manufacture: String
    ): Observable<LineMaster>


    @POST("line-master/destroy/{line_id}")
    fun deleteLineMaster(@Path("line_id") id: String): Observable<ResponseDeleteSuccess>


    //    LEAKAGE ENDPOINT
//    - LEAKAGE LIST BY LINE
    @GET("leakage/{lineId}")
    fun getLeakage(@Path("lineId") id: String): Observable<List<LeakageMaster>>
}