package com.ranseo.valendar.util

object FcstBaseTime {
    fun getFcstBaseTime(currTime: String): String {
        return if ("0210" <= currTime && currTime < "0510") "0210"
        else if ("0510" <= currTime && currTime < "0810") "0510"
        else if ("0810" <= currTime && currTime < "1110") "0810"
        else if ("1110" <= currTime && currTime < "1410") "1110"
        else if ("1410" <= currTime && currTime < "1710") "1410"
        else if ("1710" <= currTime && currTime < "2010") "1710"
        else if ("2010" <= currTime && currTime < "2310") "2010"
        else "2310"
    }
}