/*
 * Copyright © 2015 The Gravitee team (http://gravitee.io)
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
package io.gravitee.definition.jackson.datatype.plugins.resource.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import io.gravitee.definition.model.plugins.resources.Resource;
import java.io.IOException;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class ResourceDeserializer extends StdScalarDeserializer<Resource> {

    public ResourceDeserializer(Class<Resource> vc) {
        super(vc);
    }

    @Override
    public Resource deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        Resource resource = new Resource();

        final JsonNode nameNode = node.get("name");
        if (nameNode != null) {
            resource.setName(nameNode.asText());
        } else {
            throw ctxt.mappingException("[resource] Name is required");
        }

        final JsonNode typeNode = node.get("type");
        if (typeNode != null) {
            resource.setType(typeNode.asText());
        } else {
            throw ctxt.mappingException("[resource] Type is required");
        }

        final JsonNode configurationNode = node.get("configuration");
        if (configurationNode != null) {
            resource.setConfiguration(configurationNode.toString());
        } else {
            throw ctxt.mappingException("[resource] Configuration is required");
        }

        final JsonNode enabledNode = node.get("enabled");
        if (enabledNode != null) {
            resource.setEnabled(enabledNode.asBoolean(true));
        } else {
            resource.setEnabled(true);
        }

        return resource;
    }
}
