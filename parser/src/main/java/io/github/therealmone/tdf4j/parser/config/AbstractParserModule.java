/*
 * Copyright Roman Fatnev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.therealmone.tdf4j.parser.config;

import io.github.therealmone.tdf4j.commons.Environment;
import io.github.therealmone.tdf4j.commons.Module;
import io.github.therealmone.tdf4j.commons.model.ebnf.First;
import io.github.therealmone.tdf4j.commons.model.ebnf.Follow;
import io.github.therealmone.tdf4j.commons.model.ebnf.Grammar;
import io.github.therealmone.tdf4j.commons.model.ebnf.Production;
import io.github.therealmone.tdf4j.commons.utils.FirstSetCollector;
import io.github.therealmone.tdf4j.commons.utils.FollowSetCollector;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class AbstractParserModule extends BindingMapper implements Module {
    private final FirstSetCollector firstSetCollector = new FirstSetCollector();
    private final FollowSetCollector followSetCollector = new FollowSetCollector();
    private boolean built;
    private Grammar grammar;
    private Environment environment;

    public AbstractParserModule build() {
        if(!built) {
            this.configure();
            final List<Production> productions = productionBindStrategy.build();
            this.environment = environmentBindStrategy.build();
            this.grammar = new Grammar.Builder()
                    .addAllProductions(productions)
                    .initProduction(initProduction)
                    .firstSet(firstSetCollector.collect(productions))
                    .followSet(followSetCollector.collect(productions))
                    .build();
            built = true;
        }
        return this;
    }

    @Nonnull
    public Grammar getGrammar() {
        return grammar != null
                ? grammar
                : new Grammar.Builder()
                    .firstSet(new First.Builder().build())
                    .followSet(new Follow.Builder().build())
                    .build();
    }

    @Nonnull
    public Environment getEnvironment() {
        return environment != null
                ? environment
                : new Environment.Builder().build();
    }
}
