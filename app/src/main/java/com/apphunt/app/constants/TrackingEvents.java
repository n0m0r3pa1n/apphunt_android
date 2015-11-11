package com.apphunt.app.constants;

import com.apphunt.app.constants.tracking.HistoryEvents;
import com.apphunt.app.constants.tracking.LoginEvents;
import com.apphunt.app.constants.tracking.TrendingAppsEvents;

/**
 * Created by Naughty Spirit <hi@naughtyspirit.co>
 * on 2/6/15.
 */
public interface TrackingEvents extends LoginEvents, TrendingAppsEvents, HistoryEvents {

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
    String UserAddedApp = "user.added.app";
    String UserClickedLoginButton = "user.clicked.login.button";
    String UserAddedUnknownApp = "user.added.unknown.app";
    String UserPressedBackButton = "user.pressed.back.button";

    //User Actions


    String UserSentComment = "user.sent.comment";
    String UserSentReplyComment = "user.sent.reply.comment";
    String UserVotedReplyComment = "user.voted.reply.comment";

    String UserDownVotedReplyComment = "user.down.voted.reply.comment";
    String UserMadeSuggestion = "user.made.suggestion";
    String UserDisabledSound = "user.disabled.sound";
    String UserClickedMoreApps = "user.clicked.more.apps";
    String UserClickedMoreCollections = "user.clicked.more.collections";
    String UserSkippedLoginWhenAddApp = "user.skipped.login.when.add.app";
    String UserSearchedForApp = "user.searched.for.app";


    String UserSharedApp = "user.shared.app";
    String UserOpenedAppInMarket = "user.opened.app.in.market";
    String UserOpenedAppDetailsFromTopApps = "user.opened.apps.details.from.top.apps";
    String UserOpenedInstalledApp = "user.opened.installed.app";


    String UserOpenedProfileFromComment = "user.opened.profile.from.comment";
    String UserOpenedProfileFromTopHunters = "user.opened.profile.from.top.hunters";
    String UserOpenedProfileFromCollectionsList = "user.opened.profile.from.collections.list";
    String UserOpenedProfileFromCollection = "user.opened.profile.from.collection";



    String UserVotedCollection = "user.voted.collection";
    String UserDownVotedCollection = "user.down.voted.collection";
    String UserFavouritedCollection = "user.favourited.collection";
    String UserUnfavouritedCollection = "user.unfavourited.collection";
    String UserSelectedCollectionBanner = "user.selected.collection.banner";
    String UserTriedToCreateCollectionWithEmptyDesc = "user.tried.to.create.collection.with.empty.desc";
    String UserFoundNoResults = "user.found.no.results";

    //User Viewed
    String UserStartedAppFromNotification = "user.started.app.from.notification";
    String AppShowedNotification = "app.showed.notification";
    String AppNullNotification = "app.null.notification";
    String UserScrolledDownCommentList = "user.scrolled.down.comment.list";

    String UserViewedAppDetails = "user.viewed.app.details";
    String UserViewedLogin = "user.viewed.login";
    String UserViewedSettings = "user.viewed.settings";
    String UserViewedAddApp = "user.viewed.add.app";
    String UserViewedSelectApp = "user.viewed.select.app";
    String UserViewedSuggestion = "user.viewed.suggestion";
    String UserViewedTopApps = "user.viewed.top.apps";
    String UserViewedTopHunters = "user.viewed.top.hunters";
    String UserViewedHelpAddApp = "user.viewed.help.add.app";
    String UserViewedHelpAppsRequirements = "user.viewed.help.apps.requirements";
    String UserViewedHelpTopHuntersPoints = "user.viewed.help.top.hunters.points";
    String UserViewedMyCollections = "user.viewed.my.collections";
    String UserViewedFavouriteCollections="user.viewed.favourite.collections";
    String UserViewedAllCollections = "user.viewed.all.collections";
    String UserViewedChooseCollectionsBannerFragment="user.viewed.choose.collections.banner";
    String UserViewedCollection = "user.viewed.collection.details";
    String UserViewedCollectionApp = "user.viewed.collection.app";

    String UserViewedAddAppToAppHuntNotification = "user.viewed.add.app.to.apphunt.notification";
    String UserViewedSaveAppFragmentFromNotification = "user.viewed.save.app.from.notification";
    String UserViewedUpdateAppDialog = "user.viewed.update.app.dialog";
    String UserViewedTwitterInvitation = "user.viewed.invitation.twitter";
    String UserViewedEmailInvitation = "user.viewed.invitation.email";
    String UserViewedSMSInvitation = "user.viewed.invitation.sms";
    String UserViewedFavouriteApp = "user.viewed.favourite.app";
    String UserViewedFacebookSuggestions = "user.viewed.suggestions.facebook";
    String UserViewedTwitterSuggestions = "user.viewed.suggestions.twitter";
    String UserViewedFollowers = "user.viewed.followers";
    String UserViewedFollowing = "user.viewed.following";
    String UserViewedWelcomeScreen = "user.viewed.welcome";
    String UserViewedPreviousTopAppsRanking = "user.viewed.previous.top.apps.ranking";
    String UserViewedPreviousTopHuntersRanking = "user.viewed.previous.top.hunters.ranking";


    String AppShowedRegularLogin = "app.showed.regular.login.when.save.app";
    String AppShowedSkippableLogin = "app.showed.skippable.login.when.save.app";

    String UserCreatedCollection = "user.created.collection";

    String UserDeleteCollection = "user.delete.collection";
    String UserDidntSaveCollection = "user.didnt.save.collection.after.edit";
    String UserEditDescription = "user.edit.description";
    String UserDisabledInstalledAppsNotification = "user.disabled.installed.apps.notification";

    String UserSearchedWithTagFromAppDetails = "user.searched.with.tag.from.app.details";

    String UserSkippedInvitation = "user.skipped.invitation";
    String UserSentTwitterInvite = "user.sent.invites.twitter";
    String UserSentEmailInvite = "user.sent.invite.email";
    String UserSentSMSInvite = "user.sent.invite.sms";
    String UserSkippedFriendsSuggestions = "user.skipped.friends_suggestions";

    String UserFollowedSomeone = "user.followed.someone";
    String UserUnfollowedSomeone = "user.unfollowed.someone";
}
