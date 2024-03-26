ARG REGISTRY_PREFIX
ARG ABSTRACTO_VERSION
FROM ${REGISTRY_PREFIX}abstracto-rest-api-experience:${ABSTRACTO_VERSION:-latest} AS experience-api
FROM ${REGISTRY_PREFIX}abstracto-rest-api-core:${ABSTRACTO_VERSION:-latest} AS core-api


FROM ${REGISTRY_PREFIX}abstracto-rest-api:${ABSTRACTO_VERSION:-latest}
COPY --from=core-api /python /python
COPY --from=experience-api /python /python
