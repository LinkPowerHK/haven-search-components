/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.requests;

import com.hp.autonomy.searchcomponents.core.search.QueryRestrictions;
import com.hp.autonomy.searchcomponents.core.search.QueryRestrictionsTest;
import com.hp.autonomy.searchcomponents.idol.requests.IdolQueryRestrictionsImpl;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;

import java.io.IOException;

public class IdolQueryRestrictionsTest extends QueryRestrictionsTest<String> {
    @Override
    protected QueryRestrictions<String> constructObject() {
        return IdolQueryRestrictionsImpl.builder()
                .queryText("*")
                .fieldText("NOT(EMPTY):{FIELD}")
                .database("Database1")
                .minDate(DateTime.parse("2016-11-15T16:07:00Z"))
                .maxDate(DateTime.parse("2016-11-15T16:07:01Z"))
                .minScore(5)
                .languageType("englishUtf8")
                .anyLanguage(false)
                .stateMatchId("0-ABC")
                .stateDontMatchId("0-ABD")
                .build();
    }

    @Override
    protected String json() throws IOException {
        return IOUtils.toString(getClass().getResourceAsStream("/com/hp/autonomy/searchcomponents/idol/search/queryRestrictions.json"));
    }
}
