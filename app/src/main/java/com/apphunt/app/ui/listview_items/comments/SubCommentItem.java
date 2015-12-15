package com.apphunt.app.ui.listview_items.comments;

import com.apphunt.app.api.apphunt.models.comments.Comment;
import com.apphunt.app.constants.Constants;

public class SubCommentItem extends CommentItem {

    private Constants.ItemType type;
    private Comment comment;

    public SubCommentItem(Comment comment) {
        super(comment);
        this.comment = comment;
        this.type = Constants.ItemType.SUBCOMMENT;
    }

    @Override
    public Constants.ItemType getType() {
        return type;
    }

    public Comment getData() {
        return comment;
    }
}
