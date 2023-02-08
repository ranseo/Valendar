package com.ranseo.valendar.data.model.business

data class LandFcst(
    val numOFRows : Int, // 한 페이지 결과 수
    val pageNo:Int, // 페이지 번호
    val totalCount :Int, // 데이터 총 개수
    val resultCode: Int, //응답 메시지
    val resultMsg: String, // 응답메시지 내용
    val dataType:String, // 데이터 타입
    val regId: String, // 예보구역코드
    val announceTime:Int, // 발표시간
    val numEf:Int, //발표번호
    val wd1:String, //풍향(1)
    val wdTnd:Int, // 풍향연결코드
    val wd2:String, // 풍향(2)
    val wslt:Int, //풍속 강도코드
    val ta:Int, // 예상기온
    val rnSt:Int, //강수화률
    val wf:String, // 날씨
    val wfCd:String, //날씨코드 (하늘상태)
    val rnYn:Int //강수형태
) {

}
