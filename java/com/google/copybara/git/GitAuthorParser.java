// Copyright 2016 Google Inc. All Rights Reserved.
package com.google.copybara.git;

import com.google.common.base.Preconditions;
import com.google.copybara.Author;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A parser of the Git author format.
 *
 * Git is lenient on the author validation and only requires {@code "Foo <bar>"}, but does not
 * validate that {@code bar} is actually an email. Also {@code "Foo <>"} is valid, but
 * {@code "<bar>"} is not.
 *
 * TODO(danielromero): Consider using JGit's RawParseUtils
 */
class GitAuthorParser {

  private static final Pattern AUTHOR_PATTERN = Pattern.compile("(.+ )<(.*)>");

  /**
   * Parses a Git author {@code string} into an {@link Author}.
   */
  static Author parse(String author) {
    Preconditions.checkNotNull(author);
    Matcher matcher = AUTHOR_PATTERN.matcher(author);
    Preconditions.checkArgument(matcher.matches(),
        "Invalid author '%s'. Must be in the form of 'Name <email>'", author);
    return new Author(matcher.group(1).trim(), matcher.group(2).trim());
  }
}