package com.apphunt.app.constants;

import com.apphunt.app.constants.tracking.AddAppEvents;
import com.apphunt.app.constants.tracking.AppDetailsEvents;
import com.apphunt.app.constants.tracking.CollectionEvents;
import com.apphunt.app.constants.tracking.HistoryEvents;
import com.apphunt.app.constants.tracking.LoginEvents;
import com.apphunt.app.constants.tracking.TrendingAppsEvents;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/6/15.
 */
public interface TrackingEvents extends LoginEvents, TrendingAppsEvents, HistoryEvents, AppDetailsEvents, AddAppEvents, CollectionEvents {

    String UserOpenedApp = "user.opened.app";
    String UserVotedApp = "user.voted.app";
    String UserDownVotedApp = "user.down.voted.app";
    String UserViewedSelectCollection = "user.viewed.select.collection";
    String UserCreatedCollectionFromSelectCollection = "user.created.collection.from.select.collection";
    String UserAddedAppToCollection = "user.add.app.to.collection";
    String UserOpenedProfileFromTrendingOrSearchApps = "user.opened.profile.from.trending.or.search.apps";
    String UserOpenedProfileFromAppDetails = "user.opened.profile.from.app.details";
    String UserOpenedOwnProfileFromDrawer = "user.opened.own.profile.from.drawer";
    String UserOpenedRandomApp = "user.opened.random.app";
    String UserLoggedOut = "user.logged.out";
    String UserLoggedIn = "user.logged.in";

    String UserClickedLoginButton = "user.clicked.login.button";

    String UserPressedBackButton = "user.pressed.back.button";
    String UserSearchedForApp = "user.searched.for.app";
    String UserClickedMoreApps = "user.clicked.more.apps";
    String UserClickedMoreCollections = "user.clicked.more.collections";
    String UserMadeSuggestion = "user.made.suggestion";
    String UserDisabledSound = "user.disabled.sound";
    String UserEnabledSound = "user.enabled.sound";
    String UserViewedSettings = "user.viewed.settings";
    String UserViewedTopHuntersStatus = "user.viewed.top.hunter.status";
    String UserDisabledInstalledAppsNotification = "user.disabled.installed.apps.notification";
    String UserOpenedAppDetailsFromTopApps = "user.opened.apps.details.from.top.apps";
    String UserOpenedProfileFromComment = "user.opened.profile.from.comment";
    String UserOpenedProfileFromTopHunters = "user.opened.profile.from.top.hunters";
    String UserOpenedProfileFromCollectionsList = "user.opened.profile.from.collections.list";
    String UserOpenedProfileFromCollection = "user.opened.profile.from.collection";
    String UserViewedUserProfile = "user.viewed.user.profile";

    String UserViewedFollowers = "user.viewed.followers";
    String UserViewedFollowing = "user.viewed.following";
    String UserViewedProfileSection = "user.viewed.profile.section";
    String UserViewedFindFriends = "user.viewed.find.fragments";
    String UserSkippedFriendsSuggestions = "user.skipped.friends.suggestions";
    String UserViewedInvites = "user.viewed.invites";
    String UserClickedFollowFriendButton = "user.clicked.follow.friend.button";
    String UserFailedToSendTwitterInvite = "user.failed.to.send.twitter.invite";
    String UserViewedTwitterInvitation = "user.viewed.invitation.twitter";
    String UserSentTwitterInvite = "user.sent.invites.twitter";
    String UserSkippedInvitation = "user.skipped.invitation";
    String UserSentEmailInvite = "user.sent.invite.email";
    String UserViewedEmailInvitation = "user.viewed.invitation.email";
    String UserSentSMSInvite = "user.sent.invite.sms";
    String UserViewedSMSInvitation = "user.viewed.invitation.sms";

    String UserFoundNoResults = "user.found.no.results";

    String UserViewedSuggestion = "user.viewed.suggestion";
    String UserViewedPreviousTopAppsRanking = "user.viewed.previous.top.apps.ranking";
    String UserViewedTopApps = "user.viewed.top.apps";
    String UserViewedTopHunters = "user.viewed.top.hunters";
    String UserViewedPreviousTopHuntersRanking = "user.viewed.previous.top.hunters.ranking";
    String UserViewedHelpAddApp = "user.viewed.help.add.app";
    String UserViewedHelpAppsRequirements = "user.viewed.help.apps.requirements";
    String UserViewedHelpTopHuntersPoints = "user.viewed.help.top.hunters.points";

    String UserFollowedSomeone = "user.followed.someone";
    String UserUnfollowedSomeone = "user.unfollowed.someone";
    String UserViewedWelcomeScreen = "user.viewed.welcome";
    String UserViewedUpdateAppDialog = "user.viewed.update.app.dialog";
    String UserClickedUpdateAppHunt = "user.clicked.update.apphunt";

    String UserStartedAppFromNotification = "user.started.app.from.notification";
    String AppShowedNotification = "app.showed.notification";
    String AppNullNotification = "app.null.notification";

    String UserViewedLogin = "user.viewed.login";

    String UserViewedFacebookSuggestions = "user.viewed.suggestions.facebook";
    String UserViewedTwitterSuggestions = "user.viewed.suggestions.twitter";
    String UserViewedAddAppToAppHuntNotification = "user.viewed.add.app.to.apphunt.notification";
    String UserViewedCommentAppNotification = "user.viewed.comment.app.notification";

    String UserViewedSaveAppFragmentFromNotification = "user.viewed.save.app.from.notification";
    String UserViewedCommentAppFromNotification = "user.viewed.comment.app.from.notification";
    String UserInstalledApp = "user.installed.app";
    String UserRemovedApp = "user.removed.app";
    String UserOpenedAdd = "user.opened.add";
    String UserOpenedPaidAdd = "user.opened.paid.ad";
    String UserOpenedPaidInterstitialAd = "user.opened.paid.interstitial.ad";
    String UserViewedPaidInterstitialAd = "user.viewed.paid.interstitial.ad";
    String UserDisabledDailyNotification = "user.disabled.daily.notification";
    String UserEnabledDailyNotification = "user.enabled.daily.notification";
    String UserEnabledInstalledAppNotification = "user.enabled.installed.app.notification";
    String UserClickedAboutSocialLink = "user.clicked.about.social.link";
    String UserViewedAbout = "user.viewed.about";
    String UserSearchedAppToAdd = "user.searched.app.to.add";
    String UserViewedAppDetailsFromSharedLink = "user.viewed.app.details.from.shared.link";
    String UserViewedNews = "user.viewed.news";
    String UserRefreshedNews = "user.refreshed.news";
    String UserClickedShareApp = "user.clicked.share.app";
    String UserViewedCallToAction = "user.viewed.call.to.action";
    String UserClickedCallToAction = "user.clicked.call.to.action";
    String UserOpenedNews = "user.opened.news";
    String UserFavouritedApp = "user.favourited.app";
    String UserUnfavouritedApp = "user.unfavourited.app";
    String UserSwitchedToComments = "user.switched.to.comments";
    String UserSwitchedToGallery = "user.switched.to.gallery";
    String UserSwitchedDailyApps = "user.switched.daily.apps";
    String UserSwitchedTrendingApps = "user.switched.trending.apps";
    String UserRefreshedDailyApps = "user.refreshed.daily.apps";
    String UserScrolledDownTrendingAppList = "user.scrolled.down.trending.app.list";
    String UserOpenedAppDetailsFromTrendingApps = "user.opened.app.from.trending.apps";
    String UserOpenedAppFromDailyApps = "user.opened.app.from.daily.apps";
    String UserViewedAdStatusDialog = "user.viewed.ad.status.dialog";
    String UserViewedHunterStatusDialog = "user.viewed.hunters.status.dialog";
}
