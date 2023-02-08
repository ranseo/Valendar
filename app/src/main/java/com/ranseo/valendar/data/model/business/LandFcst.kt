package com.ranseo.valendar.data.model.business

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LandFcst(
    @field:Json(name="numOfRows")
    val numOfRows : String, // 한 페이지 결과 수
    @field:Json(name="pageNo")
    val pageNo:String, // 페이지 번호
    @field:Json(name="totalCount")
    val totalCount :String, // 데이터 총 개수
    @field:Json(name="resultCode")
    val resultCode: String, //응답 메시지
    @field:Json(name="resultMsg")
    val resultMsg: String, // 응답메시지 내용
    @field:Json(name="dataType")
    val dataType:String, // 데이터 타입
    @field:Json(name="regId")
    val regId: String, // 예보구역코드
    @field:Json(name="announceTime")
    val announceTime:String, // 발표시간
    @field:Json(name="numEf")
    val numEf:String, //발표번호
    @field:Json(name="wd1")
    val wd1:String, //풍향(1)
    @field:Json(name="wdTnd")
    val wdTnd:String, // 풍향연결코드
    @field:Json(name="wd2")
    val wd2:String, // 풍향(2)
    @field:Json(name="wsIt")
    val wsIt:String, //풍속 강도코드
    @field:Json(name="ta")
    val ta:String, // 예상기온
    @field:Json(name="rnSt")
    val rnSt:String, //강수화률
    @field:Json(name="wf")
    val wf:String, // 날씨
    @field:Json(name="wfCd")
    val wfCd:String, //날씨코드 (하늘상태)
    @field:Json(name="rnYn")
    val rnYn:String //강수형태
) {

}
