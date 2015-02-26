package com.apphunt.app.ui.listview_items.comments;

import com.apphunt.app.api.models.Comment;
import com.apphunt.app.ui.listview_items.Item;
import com.apphunt.app.utils.Constants;

public class SubCommentItem implements Item {

    private Constants.ItemType type;
    private Comment comment;

    public SubCommentItem(Comment comment) {
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
