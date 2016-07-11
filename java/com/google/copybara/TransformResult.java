// Copyright 2016 Google Inc. All Rights Reserved.
package com.google.copybara;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.copybara.Origin.Reference;
import com.google.copybara.util.PathMatcherBuilder;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

/**
 * Represents the final result of a transformation, including metadata and actual code to be
 * migrated.
 */
public final class TransformResult {
  private final Path path;
  private final Origin.Reference<?> originRef;
  private final Author author;
  private final long timestamp;
  private final String summary;
  private final PathMatcherBuilder excludedDestinationPaths;
  @Nullable
  private final String baseline;

  private static long readTimestampOrCurrentTime(Reference<?> originRef) throws RepoException {
    Long refTimestamp = originRef.readTimestamp();
    return (refTimestamp != null)
        ? refTimestamp : TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
  }

  public TransformResult(Path path, Reference<?> originRef, Author author, String summary,
      PathMatcherBuilder excludedDestinationPaths) throws RepoException {
    this(path, originRef, author,
        readTimestampOrCurrentTime(originRef), summary, excludedDestinationPaths, null);
  }

  private TransformResult(Path path, Reference<?> originRef, Author author, long timestamp,
      String summary, PathMatcherBuilder excludedDestinationPaths, @Nullable String baseline) {
    this.path = Preconditions.checkNotNull(path);
    this.originRef = Preconditions.checkNotNull(originRef);
    this.author = Preconditions.checkNotNull(author);
    this.timestamp = timestamp;
    this.summary = Preconditions.checkNotNull(summary);
    this.excludedDestinationPaths = Preconditions.checkNotNull(excludedDestinationPaths);
    this.baseline = baseline;
  }

  public TransformResult withBaseline(String newBaseline) {
    Preconditions.checkNotNull(newBaseline);
    return new TransformResult(
        this.path, this.originRef, this.author, this.timestamp, this.summary,
        this.excludedDestinationPaths, newBaseline);
  }

  /**
   * Directory containing the tree of files to put in destination.
   */
  public Path getPath() {
    return path;
  }

  /**
   * Reference to the origin revision being moved.
   */
  public Origin.Reference<?> getOriginRef() {
    return originRef;
  }

  /**
   * Destination author to be used.
   */
  public Author getAuthor() {
    return author;
  }

  /**
   * When the code was submitted to the origin repository, expressed as seconds since the UNIX
   * epoch.
   */
  public long getTimestamp() {
    return timestamp;
  }

  /**
   * A description of the migrated changes to include in the destination's change description. The
   * destination may add more boilerplate text or metadata.
   */
  public String getSummary() {
    return summary;
  }

  /**
   * A path matcher which matches files in the destination that should not be deleted even if they
   * don't exist in source.
   */
  public PathMatcherBuilder getExcludedDestinationPaths() {
    return excludedDestinationPaths;
  }

  /**
   * Destination baseline to be used for updating the code in the destination. If null, the
   * destination can assume head baseline.
   *
   * <p>Destinations supporting non-null baselines are expected to do the equivalent of:
   * <ul>
   *    <li>Sync to that baseline</li>
   *    <li>Apply/patch the changes on that revision</li>
   *    <li>Sync to head and auto-merge conflicts if possible</li>
   * </ul>
   */
  @Nullable
  public String getBaseline() {
    return baseline;
  }
}