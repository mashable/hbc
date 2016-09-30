/**
 * Copyright 2014 Twitter, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.twitter.hbc.core.endpoint;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.twitter.hbc.core.HttpConstants;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

public abstract class EnterpriseStreamingEndpoint implements StreamingEndpoint {
  private static final String BASE_PATH = "/stream/%s/accounts/%s/publishers/%s/%s.json";;
  protected final String account;
  protected final String publisher;
  protected final String product;
  protected final String label;
  protected final ConcurrentMap<String, String> queryParameters = Maps.newConcurrentMap();

  public EnterpriseStreamingEndpoint(String account, String product, String label) {
      this(account, "twitter", product, label, 0);
  }

  public EnterpriseStreamingEndpoint(String account, String product, String label, int partitionId) {
      this(account, "twitter", product, label, partitionId);
  }

  public EnterpriseStreamingEndpoint(String account, String publisher, String product, String label, int partitionId) {
    this.account = Preconditions.checkNotNull(account);
    this.product = Preconditions.checkNotNull(product);
    this.label = Preconditions.checkNotNull(label);
    this.publisher = Preconditions.checkNotNull(publisher);

    if (partitionId > 0) {
      addQueryParameter("partition", String.valueOf(partitionId));
    }
  }

  @Override
  public String getURI() {
    String uri = getBaseUri();

    if (queryParameters.isEmpty()) {
      return uri;
    } else {
      return uri + "?" + generateParamString(queryParameters);
    }
  }

  protected String generateParamString(Map<String, String> params) {
    return Joiner.on("&")
            .withKeyValueSeparator("=")
            .join(params);
  }

  protected String getBaseUri() {
    return String.format(BASE_PATH, product.trim(), account.trim(), publisher.trim(), label.trim());
  }

  protected String getAccount() {
    return account;
  }

  protected String getProduct() {
    return product;
  }

  protected String getLabel() {
    return label;
  }

  protected String getPublisher() {
    return publisher;
  }

  @Override
  public String getHttpMethod() {
    return HttpConstants.HTTP_GET;
  }

  @Override
  public String getPostParamString() {
    return null;
  }

  @Override
  public String getQueryParamString() {
    return generateParamString(queryParameters);
  }

  @Override
  public void addQueryParameter(String param, String value) {
    queryParameters.put(param, value);
  }

  @Override
  public void removeQueryParameter(String param) {
    queryParameters.remove(param);
  }

  // These don't do anything
  @Override
  public void setBackfillCount(int count) { }

  @Override
  public void setApiVersion(String apiVersion) { }

  @Override
  public void addPostParameter(String param, String value) { }

  @Override
  public void removePostParameter(String param) { }

}
