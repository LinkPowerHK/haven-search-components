/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.authentication;

import com.hp.autonomy.searchcomponents.core.authentication.AbstractAuthenticationInformationRetrieverTest;
import org.junit.Before;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;

public class IdolAuthenticationInformationRetrieverTest extends AbstractAuthenticationInformationRetrieverTest<Authentication> {
    @Mock
    private Authentication idolAuthentication;

    @Before
    public void setUp() {
        authenticationInformationRetriever = new IdolAuthenticationInformationRetriever();
        authentication = idolAuthentication;
    }
}
