package jp.ikota.drive.network;


import java.util.HashMap;

import jp.ikota.drive.data.SampleResponse;

public class Util {

    public static final HashMap<String, String> RESPONSE_MAP = new HashMap<String, String>(){
        {put(DribbleURL.PATH_SHOTS, SampleResponse.getShots());}
        {put(DribbleURL.PATH_SHOTS+"/", SampleResponse.getShot());}
        {put(DribbleURL.PATH_USERS+"/", SampleResponse.getUserLikes());}
        {put(DribbleURL.PATH_LIKE, SampleResponse.getLikeResponse());}
    };

}
