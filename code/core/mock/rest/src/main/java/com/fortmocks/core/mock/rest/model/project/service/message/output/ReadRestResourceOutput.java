/*
 * Copyright 2015 Karl Dahlgren
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.fortmocks.core.mock.rest.model.project.service.message.output;

import com.fortmocks.core.basis.model.Output;
import com.fortmocks.core.basis.model.validation.NotNull;
import com.fortmocks.core.mock.rest.model.project.dto.RestResourceDto;

/**
 * @author Karl Dahlgren
 * @since 1.0
 */
public class ReadRestResourceOutput implements Output{

    @NotNull
    private RestResourceDto restResource;

    public ReadRestResourceOutput(RestResourceDto restResource) {
        this.restResource = restResource;
    }

    public RestResourceDto getRestResource() {
        return restResource;
    }

    public void setRestResource(RestResourceDto restResource) {
        this.restResource = restResource;
    }
}
