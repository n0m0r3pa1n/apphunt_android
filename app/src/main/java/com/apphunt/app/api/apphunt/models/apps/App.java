package com.apphunt.app.api.apphunt.models.apps;

import com.apphunt.app.api.apphunt.models.votes.Vote;

import java.util.ArrayList;
import java.util.List;

public class App extends BaseApp {

    private List<Vote> votes = new ArrayList<>();

    public List<Vote> getVotes() {
        return votes;
    }

    public void setVotes(List<Vote> votes) {
        this.votes = votes;
    }

    @Override
    public String toString() {
        return "App{" +
                "votes=" + votes +
                '}';
    }
}
