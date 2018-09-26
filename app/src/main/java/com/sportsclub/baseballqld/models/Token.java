package com.sportsclub.baseballqld.models;

public class Token {

    public String access_token;
    public String token_type;
    public int expires_in;
    public String userName;
    public int memberId;

    //NB: these followign vars cannot be mapped automatically as they use a "." before the var name in JSON
    public String issued;
    public String expires;

}
