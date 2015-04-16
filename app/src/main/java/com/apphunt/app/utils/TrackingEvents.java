package com.apphunt.app.utils;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/6/15.
 */
public interface TrackingEvents {
    String UserLoggedIn = "user.logged.in";
    String UserLoggedOut = "user.logged.out";
    String UserAddedApp = "user.added.app";
    String UserAddedUnknownApp = "user.added.unknown.app";
    String UserVoted = "user.voted";
    String UserDownVoted = "user.down.voted";
    String UserSharedAppHuntWithFacebook = "user.shared.app.hunt.with.facebook";
    String UserSharedAppHuntWithoutFacebook = "user.shared.app.hunt.without.facebook";
    String UserDisabledSound = "user.disabled.sound";
    String UserScrolledDownAppList = "user.scrolled.down.app.list";
    String UserRequestedMoreApps = "user.requested.more.apps";
    String UserStartedAppFromDailyTrendingAppsNotification = "user.started.app.from.daily.trending.apps.notification";
    String AppShowedTrendingAppsNotification = "app.showed.trending.apps.notification";
    String UserOpenedAppFromList = "user.opened.app.from.list";
    String UserVotedAppFromDetails = "user.voted.app.from.details";
    String UserDownVotedAppFromDetails = "user.down.voted.app.from.details";
    String UserSentComment = "user.sent.comment";
    String UserSentReplyComment = "user.sent.reply.comment";
    String UserVotedComment = "user.voted.comment";
    String UserDownVotedComment = "user.down.voted.comment";
    String UserVotedReplyComment = "user.voted.reply.comment";
    String UserDownVotedReplyComment = "user.down.voted.reply.comment";
    String UserScrolledDownCommentList = "user.scrolled.down.comment.list";
    String UserOpenedAppInMarket = "user.opened.app.in.market";
    String UserMadeSuggestion = "user.made.suggestion";
    String UserViewedAppDetails = "user.viewed.app.details";
    String UserViewedLogin = "user.viewed.login";
    String UserViewedSettings = "user.viewed.settings";
    String UserViewedAddApp = "user.viewed.add.app";
    String UserViewedSelectApp = "user.viewed.select.app";
    String UserViewedSuggestion = "user.viewed.suggestion";
    String UserFoundNoResults = "user.found.no.results";
}
