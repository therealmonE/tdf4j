/*
 * Copyright 2019 Roman Fatnev
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
package io.github.therealmone.tdf4j.generator.utils;

import io.github.therealmone.tdf4j.commons.Dependency;
import io.github.therealmone.tdf4j.generator.templates.ParserTemplate;
import io.github.therealmone.tdf4j.parser.MetaInf;

import java.util.ArrayList;
import java.util.List;

public class MetaInfCollector {

    public MetaInf collect(ParserTemplate parserTemplate) {
        final MetaInf.Builder builder = new MetaInf.Builder()
                .pack(parserTemplate.pack())
                .className(parserTemplate.className())
                .sourceCode(parserTemplate.build())
                .envImports(parserTemplate.environment().packages())
                .dependencies(collectDependencies(parserTemplate))
                .imports(collectImports(parserTemplate));

        return builder.build();
    }

    private String[] collectImports(final ParserTemplate parserTemplate) {
        final List<String> imports = new ArrayList<>();
        for (String imp :parserTemplate.imports()){
            imports.add(imp.replaceAll(";|\n|;\n|\r|;\r", ""));
        }
        return imports.toArray(new String[]{});
    }

    private String[] collectDependencies(final ParserTemplate parserTemplate) {
        final String[] dependencies = new String[parserTemplate.environment().dependencies().length];
        for (int i = 0; i < parserTemplate.environment().dependencies().length; i++) {
            final Dependency dependency = parserTemplate.environment().dependencies()[i];
            dependencies[i] = dependency.getClazz().getCanonicalName();
        }
        return dependencies;
    }
}
