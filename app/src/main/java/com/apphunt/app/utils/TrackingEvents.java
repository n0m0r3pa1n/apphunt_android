package com.apphunt.app.utils;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/6/15.
 */
public interface TrackingEvents {
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
    static String UserScrolledDownAppList = "user.scrolled.down.app.list";
    static String UserRequestedMoreApps = "user.requested.more.apps";
    static String UserStartedAppFromDailyTrendingAppsNotification = "user.started.app.from.daily.trending.apps.notification";
    static String AppShowedTrendingAppsNotification = "app.showed.trending.apps.notification";
    static String UserOpenedAppFromList = "user.opened.app.from.list";
    static String UserVotedAppFromDetails = "user.voted.app.from.details";
    static String UserDownVotedAppFromDetails = "user.down.voted.app.from.details";
    static String UserSentComment = "user.sent.comment";
    static String UserSentReplyComment = "user.sent.reply.comment";
    static String UserVotedComment = "user.voted.comment";
    static String UserDownVotedComment = "user.down.voted.comment";
    static String UserVotedReplyComment = "user.voted.reply.comment";
    static String UserDownVotedReplyComment = "user.down.voted.reply.comment";
    static String UserScrolledDownCommentList = "user.scrolled.down.comment.list";
    static String UserOpenedAppInMarket = "user.opened.app.in.market";
    static String UserMadeSuggestion = "user.made.suggestion";
}
