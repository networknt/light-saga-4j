package com.networknt.saga.core.command.common.paths;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PlaceholderValueMapProvider implements PlaceholderValueProvider {
  private final Map<String, String> params;

  public PlaceholderValueMapProvider(Map<String, String> params) {
    Objects.requireNonNull(params);
    this.params = params;
  }

  @Override
  public Optional<String> get(String name) {
    return Optional.ofNullable(params.get(name));
  }

  @Override
  public Map<String, String> getParams() {
    return params;
  }
}
