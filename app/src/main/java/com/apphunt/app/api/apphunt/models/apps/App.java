package com.apphunt.app.api.apphunt.models.apps;

import com.apphunt.app.api.apphunt.models.votes.AppVote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class App extends BaseApp implements Serializable {

    private List<AppVote> votes = new ArrayList<>();

    private List<String> tags = new ArrayList<>();

    public List<AppVote> getVotes() {
        return votes;
    }

    public void setVotes(List<AppVote> votes) {
        this.votes = votes;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "App{" +
                "votes=" + votes +
                "tags=" + tags +
                '}';
    }
}
