package com.chanko.api.user.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Abstract type for every profile view returned by GET /api/users/{username}
 * Each view is serialized with a "view" field ("public" | "admin" | "private")
 * so the client knows which shape it received.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "view")
@JsonSubTypes({
  @JsonSubTypes.Type(value = PublicProfileResponse.class, name = "public"),
  @JsonSubTypes.Type(value = AdminProfileResponse.class, name = "admin"),
  @JsonSubTypes.Type(value = PrivateProfileResponse.class, name = "private")
})
public sealed interface ProfileResponse
  permits PublicProfileResponse, AdminProfileResponse, PrivateProfileResponse {

  String username();
}
