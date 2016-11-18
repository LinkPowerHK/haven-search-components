/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.search;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.transport.AciParameter;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.types.idol.marshalling.ProcessorFactory;
import com.hp.autonomy.types.idol.responses.Qs;
import com.hp.autonomy.types.idol.responses.QsElement;
import com.hp.autonomy.types.idol.responses.QueryResponseData;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IdolRelatedConceptsServiceTest {
    @Mock
    private HavenSearchAciParameterHandler parameterHandler;

    @Mock
    private AciService contentAciService;

    @Mock
    private ProcessorFactory aciResponseProcessorFactory;

    private IdolRelatedConceptsService idolRelatedConceptsService;

    @Before
    public void setUp() {
        idolRelatedConceptsService = new IdolRelatedConceptsService(parameterHandler, contentAciService, aciResponseProcessorFactory);
    }

    @Test
    public void findRelatedConcepts() {
        final QueryResponseData responseData = new QueryResponseData();
        final Qs qs = new Qs();
        qs.getElement().add(new QsElement());
        responseData.setQs(qs);

        when(contentAciService.executeAction(anySetOf(AciParameter.class), any())).thenReturn(responseData);

        final QueryRestrictions<String> queryRestrictions = IdolQueryRestrictions.builder()
                .queryText("*")
                .fieldText("Some field text")
                .databases(Collections.singletonList("Database1"))
                .maxDate(DateTime.now())
                .build();

        final IdolRelatedConceptsRequest idolRelatedConceptsRequest = IdolRelatedConceptsRequest.builder()
                .queryRestrictions(queryRestrictions)
                .querySummaryLength(250)
                .maxResults(200)
                .build();
        final List<QsElement> results = idolRelatedConceptsService.findRelatedConcepts(idolRelatedConceptsRequest);
        assertThat(results, is(not(empty())));
    }
}
