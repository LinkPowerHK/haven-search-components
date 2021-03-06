/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.core.parametricvalues;

import com.google.common.collect.ImmutableMap;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequest;
import com.hp.autonomy.searchcomponents.core.fields.FieldsRequestBuilder;
import com.hp.autonomy.searchcomponents.core.fields.FieldsService;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.test.TestUtils;
import com.hp.autonomy.types.idol.responses.RecursiveField;
import com.hp.autonomy.types.requests.idol.actions.tags.QueryTagInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.RangeInfo;
import com.hp.autonomy.types.requests.idol.actions.tags.TagName;
import com.hp.autonomy.types.requests.idol.actions.tags.ValueDetails;
import com.hp.autonomy.types.requests.idol.actions.tags.params.FieldTypeParam;
import com.hp.autonomy.types.requests.idol.actions.tags.params.SortParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringRunner.class)
public abstract class AbstractParametricValuesServiceIT<R extends ParametricRequest<Q>, F extends FieldsRequest, FB extends FieldsRequestBuilder<F, ?>, Q extends QueryRestrictions<?>, E extends Exception> {
    @Autowired
    private FieldsService<F, E> fieldsService;
    @Autowired
    private ParametricValuesService<R, Q, E> parametricValuesService;
    @Autowired
    protected ObjectFactory<FB> fieldsRequestBuilderFactory;
    @Autowired
    private ObjectFactory<ParametricRequestBuilder<R, Q, ?>> parametricRequestBuilderFactory;

    @Autowired
    protected TestUtils<Q> testUtils;

    protected abstract FieldsRequestBuilder<F, ?> fieldsRequestParams(final FB fieldsRequestBuilder);

    @Test
    public void getAllParametricValues() throws E {
        final Set<QueryTagInfo> results = parametricValuesService.getAllParametricValues(createParametricRequest());
        assertThat(results, is(not(empty())));
    }

    @Test
    public void ranges() throws E {
        final Map<TagName, ValueDetails> valueDetailsOutput = parametricValuesService.getValueDetails(createNumericParametricRequest());
        final ValueDetails valueDetails = valueDetailsOutput.get(new TagName(ParametricValuesService.AUTN_DATE_FIELD));
        final List<RangeInfo> ranges = parametricValuesService.getNumericParametricValuesInBuckets(createNumericParametricRequest(), ImmutableMap.of(ParametricValuesService.AUTN_DATE_FIELD, new BucketingParams(35, valueDetails.getMin(), valueDetails.getMax())));
        assertThat(ranges, not(empty()));
    }

    @Test
    public void getDependentParametricValues() throws E {
        final Collection<RecursiveField> results = parametricValuesService.getDependentParametricValues(createParametricRequest());
        assertThat(results, is(not(empty())));
    }

    @Test
    public void getValueDetails() throws E {
        final Map<TagName, ValueDetails> valueDetailsMap = parametricValuesService.getValueDetails(createNumericParametricRequest());
        assertThat(valueDetailsMap.size(), is(not(0)));

        for (final Map.Entry<TagName, ValueDetails> entry : valueDetailsMap.entrySet()) {
            assertThat(entry.getKey().getId(), not(nullValue()));

            final ValueDetails valueDetails = entry.getValue();

            // min <= average <= max
            assertThat(valueDetails.getMin(), lessThanOrEqualTo(valueDetails.getAverage()));
            assertThat(valueDetails.getAverage(), lessThanOrEqualTo(valueDetails.getMax()));
        }
    }

    private R createNumericParametricRequest() {
        return parametricRequestBuilderFactory.getObject()
                .fieldName(ParametricValuesService.AUTN_DATE_FIELD)
                .queryRestrictions(testUtils.buildQueryRestrictions())
                .sort(SortParam.ReverseDate)
                .build();
    }

    private R createParametricRequest() throws E {
        final FieldsRequestBuilder<F, ?> fieldsRequestBuilder = fieldsRequestParams(fieldsRequestBuilderFactory.getObject());
        final List<TagName> fields = fieldsService.getFields(fieldsRequestBuilder.build(), FieldTypeParam.Parametric).get(FieldTypeParam.Parametric);
        final Collection<String> fieldIds = new ArrayList<>(fields.size());

        fieldIds.addAll(fields.stream().map(TagName::getId).collect(Collectors.toList()));

        return parametricRequestBuilderFactory.getObject()
                .fieldNames(fieldIds)
                .queryRestrictions(testUtils.buildQueryRestrictions())
                .build();
    }
}
