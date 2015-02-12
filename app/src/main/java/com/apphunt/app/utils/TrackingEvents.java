package com.apphunt.app.utils;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/6/15.
 */
public interface TrackingEvents {
    static String Namespace = "app.hunt";
    static String UserLoggedIn = "user.logged.in";
    static String UserLoggedOut = "user.logged.out";
    static String UserAddedApp = "user.added.app";
    static String UserAddedUnknownApp = "user.added.unknown.app";
    static String UserVoted = "user.voted";
    static String UserDownVoted = "user.down.voted";
    static String UserSharedAppHunt = "user.shared.app.hunt";
    static String UserDisabledSound = "user.disabled.sound";
    static String AppShowedInviteScreen = "app.showed.invite.screen";
    static String UserShareForInviteCountDecremented = "app.share.for.invite.count.decremented";
    static String UserSharedForInvite = "user.shared.app.hunt.for.invite";
    static String UserReceivedInvite = "user.received.invite";
}
