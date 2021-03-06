/*
 * Copyright (c) 2019 tdf4j
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
package io.github.tdf4j.core.module;

import io.github.tdf4j.core.model.Environment;

public class EnvironmentBindStrategy implements BindStrategy.WithoutArgs<Environment.Builder, Environment> {
    private Environment.Builder environment;

    @Override
    public Environment.Builder bind() {
        if(environment != null) {
            throw new RuntimeException("Environment can't be bind multiple times");
        }
        this.environment = new Environment.Builder();
        return environment;
    }

    @Override
    public Environment build() {
        if(environment == null) {
            environment = new Environment.Builder()
                    .setPackages()
                    .setDependencies();
        }
        return environment.build();
    }
}
