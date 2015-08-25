package com.apphunt.app.event_bus.events.api.users;

import com.apphunt.app.api.apphunt.models.comments.Comments;
import com.apphunt.app.api.apphunt.models.comments.ProfileComments;

public class GetUserCommentsApiEvent {
    private ProfileComments comments;

    public GetUserCommentsApiEvent(ProfileComments comments) {
        this.comments = comments;
    }

    public ProfileComments getComments() {
        return comments;
    }
}
