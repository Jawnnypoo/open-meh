package com.jawnnypoo.openmeh.github;

import com.google.gson.annotations.SerializedName;

/**
 * GitHub contributor
 */
public class Contributor {
    public String login;
    public int contributions;
    @SerializedName("avatar_url")
    public String avatarUrl;
}
