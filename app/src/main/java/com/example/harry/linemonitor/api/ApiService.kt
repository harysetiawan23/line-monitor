package com.example.harry.submission_2kade.api


import com.example.harry.linemonitor.data.*
import io.reactivex.Observable
import retrofit2.http.*

interface ApiService {

    @GET("line-master/lines")
    fun getLineMasterLines(): Observable<List<LineMaster>>

    @GET("line-master/lines")
    fun getLineMasterLinesMaps(): Observable<List<LineMasterMap>>


    @GET("line-history/{line_id}")
    fun getLineHistory(@Path("line_id") lineId: String): Observable<List<LineHistory>>

    @GET("node-master/nodes/")
    fun getNodeList(): Observable<List<NodeMaster>>


    @GET("line-history/hour/{line_id}")
    fun getLineRecordHourly(@Path("line_id") lineId: String): Observable<List<LineRecap>>


    @GET("line-recap/{line_id}")
    fun getLineRecap(@Path("line_id") lineId: String): Observable<LineChartRecap>





}