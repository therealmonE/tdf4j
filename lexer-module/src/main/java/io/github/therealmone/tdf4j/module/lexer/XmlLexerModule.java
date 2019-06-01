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
package io.github.therealmone.tdf4j.module.lexer;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.xml.sax.Attributes;

import java.io.*;

public class XmlLexerModule extends AbstractLexerModule {
    private final Digester digester = new Digester();
    private final Reader xml;

    {
        digester.addRule("terminals/terminal", new Rule() {
            @Override
            public void begin(String namespace, String name, Attributes attributes) throws Exception {
                tokenize(attributes.getValue("tag"))
                        .pattern(attributes.getValue("pattern"))
                        .priority(attributes.getValue("priority") != null ? Long.parseLong(attributes.getValue("priority")) : 0)
                        .hidden(attributes.getValue("hidden") != null && Boolean.parseBoolean(attributes.getValue("hidden")));
            }
        });
    }

    public XmlLexerModule(final String xml) {
        this.xml = new StringReader(xml);
    }

    public XmlLexerModule(final InputStream xml) {
        this.xml = new InputStreamReader(xml);
    }

    public XmlLexerModule(final File xml) throws FileNotFoundException {
        this.xml = new FileReader(xml);
    }

    @Override
    protected void configure() {
        try {
            digester.parse(xml);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
