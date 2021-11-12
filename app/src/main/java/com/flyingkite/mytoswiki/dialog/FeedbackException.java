package com.flyingkite.mytoswiki.dialog;

import com.flyingkite.firebase.CloudMessaging;

public class FeedbackException extends Exception {
    public FeedbackException(CharSequence s) {
        super("\n" + s + "\n\nFCM Token= " + CloudMessaging.getToken() + "\n");
    }
}
