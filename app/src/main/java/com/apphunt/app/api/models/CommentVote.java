package com.apphunt.app.api.models;

/**
 * * Created by Seishin <atanas@naughtyspirit.co>
 * * on 2/27/15.
 * *
 * * NaughtySpirit 2015
 */
public class CommentVote {
    private int votesCount;

    public int getVotesCount() {
        return votesCount;
    }

    public void setVotesCount(int votesCount) {
        this.votesCount = votesCount;
    }

    @Override
    public String toString() {
        return "CommentVote{" +
                "votesCount=" + votesCount +
                '}';
    }
}
