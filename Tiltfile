allow_k8s_contexts('k8s-cluster')

load('ext://restart_process', 'docker_build_with_restart')
registry = 'harbor.sheldan.dev/oneplus-bot/'
abstracto_registry = 'harbor.sheldan.dev/abstracto/'

local_resource(
  'oneplus-bot-java-compile',
  'mvn install && ' +
  'rm -rf application/executable/target/jar-staging && ' +
  'unzip -o application/executable/target/oneplus-bot-exec.jar -d application/executable/target/jar-staging && ' +
  'rsync --delete --inplace --checksum -r application/executable/target/jar-staging/ application/executable/target/jar && ' +
  'mkdir application/executable/target/jar/snapshots && ' +
  'rsync --delete --inplace --checksum -r application/executable/target/jar/BOOT-INF/lib/*-SNAPSHOT.jar application/executable/target/jar/snapshots && ' +
  'rm -f application/executable/target/jar/BOOT-INF/lib/*-SNAPSHOT.jar ',
  deps=['pom.xml'])

docker_build_with_restart(
  registry + 'oneplus-bot-image',
  './application/executable/target/jar',
  entrypoint=['java', '-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005', '-cp', '.:./lib/*', 'dev.sheldan.oneplus.bot.executable.Application'],
  dockerfile='./application/executable/Dockerfile',
  live_update=[
    sync('./application/executable/target/jar/BOOT-INF/lib', '/app/lib'),
    sync('./application/executable/target/jar/META-INF', '/app/META-INF'),
    sync('./application/executable/target/jar/BOOT-INF/classes', '/app'),
    sync('./application/executable/target/jar/snapshots', '/app/lib')
  ],
)

docker_build(registry + 'oneplus-bot-db-data', 'deployment/image-packaging/src/main/docker/db-data/')
docker_build(registry + 'oneplus-bot-template-data', 'deployment/image-packaging/src/main/docker/template-data/')
docker_build(registry + 'oneplus-bot-private-rest-api', 'deployment/image-packaging/src/main/docker/private-rest-api/', build_args={'REGISTRY_PREFIX': abstracto_registry})
docker_build(registry + 'oneplus-bot-rest-api', 'deployment/image-packaging/src/main/docker/rest-api/', build_args={'REGISTRY_PREFIX': abstracto_registry})


local('cd tilt/oneplusbot-dev && helm dep up')
k8s_yaml(helm('tilt/oneplusbot-dev', values=
['./../OnePlusBot-environments/argocd/apps/oneplus-bot/values/local/values.yaml',
'secrets://./../OnePlusBot-environments/argocd/apps/oneplus-bot/values/local/values.secrets.yaml']
))

local_resource('fetch-packages', 'mvn install -f deployment/image-packaging/pom.xml', auto_init=False, trigger_mode = TRIGGER_MODE_MANUAL)
k8s_resource('chart-oneplus-bot', port_forwards='5005:5005')