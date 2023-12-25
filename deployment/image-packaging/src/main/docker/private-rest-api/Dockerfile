ARG REGISTRY_PREFIX
ARG ABSTRACTO_VERSION
FROM ${REGISTRY_PREFIX}abstracto-rest-api-image-gen:${ABSTRACTO_VERSION:-latest} AS image-gen-api

FROM ${REGISTRY_PREFIX}abstracto-rest-api:${ABSTRACTO_VERSION:-latest} AS running-image
COPY --from=image-gen-api /python /python