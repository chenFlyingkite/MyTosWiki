package com.flyingkite.mytoswiki.data.pack.response;

import com.google.gson.annotations.SerializedName;

public class PackUser {
    @SerializedName("id")
    public long id;

    @SerializedName("fbLink")
    public String fbLink = "";
    //"fbLink": "",

    @SerializedName("displayName")
    public String displayName = "";
    //"displayName": "Flyingkite2",

    @SerializedName("level")
    public int level;
    //"level": 527,

    @SerializedName("reviewedCase")
    public int reviewedCase;
    //"reviewedCase": 0,

    @SerializedName("caseReviewed")
    public int caseReviewed;
    //"caseReviewed": 0,

    @SerializedName("caseReviewedAlive")
    public int caseReviewedAlive;
    //"caseReviewedAlive": 0,

//    @SerializedName("badges")
//    public String badges = "";
//    "badges": [], ?

    @SerializedName("isSeekingHelp")
    public boolean isSeekingHelp;
    //"isSeekingHelp": true,

    @SerializedName("requestMessage")
    public String requestMessage = "";
    //"requestMessage": "",

//    @SerializedName("requestTags")
//    public int requestTags;
//    "requestTags": [],

    @SerializedName("requestUpdatedAt")
    public String requestUpdatedAt = "";
    //"requestUpdatedAt": "2020-04-21T10:44:19.515Z",

    @SerializedName("backpackUpdatedAt")
    public String backpackUpdatedAt = "";
    //"backpackUpdatedAt": "2020-04-30T10:54:20.886Z",

    @SerializedName("isAnswered")
    public boolean isAnswered;
    //"isAnswered": true
}

/*
"userData": {
    "id": 199215954,
    "fbLink": "",
    "displayName": "Flyingkite2",
    "level": 527,
    "reviewedCase": 0,
    "caseReviewed": 0,
    "caseReviewedAlive": 0,
    "badges": [],
    "isSeekingHelp": true,
    "requestMessage": "",
    "requestTags": [],
    "requestUpdatedAt": "2020-04-21T10:44:19.515Z",
    "backpackUpdatedAt": "2020-04-30T10:54:20.886Z",
    "isAnswered": true
  },
*/
