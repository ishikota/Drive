package jp.ikota.drive.network.oauth;

public class OauthRequestParams {

    final String client_id;
    final String client_secret;
    final String code;

    OauthRequestParams(String client_id, String client_secret, String code) {
        this.client_id = client_id;
        this.client_secret = client_secret;
        this.code = code;
    }
}
