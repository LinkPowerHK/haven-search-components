/*
 * Copyright 2015 Hewlett-Packard Development Company, L.P.
 * Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
 */

package com.hp.autonomy.searchcomponents.idol.typeahead;

import com.autonomy.aci.client.services.AciService;
import com.autonomy.aci.client.services.Processor;
import com.autonomy.aci.client.util.AciParameters;
import com.hp.autonomy.frontend.configuration.ConfigService;
import com.hp.autonomy.idolutils.processors.AciResponseJaxbProcessorFactory;
import com.hp.autonomy.searchcomponents.core.typeahead.GetSuggestionsFailedException;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadConstants;
import com.hp.autonomy.searchcomponents.core.typeahead.TypeAheadService;
import com.hp.autonomy.searchcomponents.idol.configuration.IdolSearchCapable;
import com.hp.autonomy.types.idol.TypeAheadResponseData;
import com.hp.autonomy.types.requests.qms.actions.typeahead.TypeAheadActions;
import com.hp.autonomy.types.requests.qms.actions.typeahead.params.ModeParam;
import com.hp.autonomy.types.requests.qms.actions.typeahead.params.TypeAheadParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
class QmsTypeAheadService implements TypeAheadService {
    private final ConfigService<? extends IdolSearchCapable> configService;
    private final GetSuggestionsAciExecutor executor;
    private final AciService qmsAciService;
    private final Processor<TypeAheadResponseData> processor;

    @Autowired
    public QmsTypeAheadService(
            final ConfigService<? extends IdolSearchCapable> configService,
            final GetSuggestionsAciExecutor executor,
            final AciService qmsAciService,
            final AciResponseJaxbProcessorFactory processorFactory
    ) {
        this.configService = configService;
        this.executor = executor;
        this.qmsAciService = qmsAciService;
        processor = processorFactory.createAciResponseProcessor(TypeAheadResponseData.class);
    }

    @Override
    public List<String> getSuggestions(final String text) throws GetSuggestionsFailedException {
        final ModeParam mode = configService.getConfig().getQueryManipulation().getTypeAheadMode();

        final AciParameters parameters = new AciParameters(TypeAheadActions.TypeAhead.name());
        parameters.add(TypeAheadParams.Mode.name(), mode);
        parameters.add(TypeAheadParams.MaxResults.name(), TypeAheadConstants.MAX_RESULTS);
        parameters.add(TypeAheadParams.Text.name(), text);

        final TypeAheadResponseData response = executor.executeAction(qmsAciService, processor, parameters);
        final List<String> output = new LinkedList<>();

        for (final TypeAheadResponseData.Expansion expansion : response.getExpansion()) {
            final String value = expansion.getValue();
            // Do not lower case dictionary suggestions (they are explicitly defined by the user)
            output.add(ModeParam.Index == mode ? value.toLowerCase() : value);
        }

        return output;
    }
}
