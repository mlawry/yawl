#!/bin/bash

echo '!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'
echo 'MUST run this shell script from `yawl/build/jar` folder.'
echo '!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'

# Cannot find the following JARs in maven central repo, so have to install them locally first.
# These JARs were found in bits.netbeans.org/nexus
mvn -f install-jsf-appbase/pom.xml        package   && \
mvn -f install-jsf-errorhandler/pom.xml   package   && \
mvn -f install-jsf-webui/pom.xml          package
# Use local copy for these JARs, javax.faces:jsf-api (from central repo) and org.netbeans.external:jsf-api
# (from bits.netbeans.org/nexus) equivalents don't actually work. Same goes for jsf-impl equivalents.
cp ../3rdParty/lib/jsf-api.jar            install-jsf-api/           && \
cp ../3rdParty/lib/jsf-impl.jar           install-jsf-impl/          && \
mvn -f install-jsf-api/pom.xml            package                    && \
mvn -f install-jsf-impl/pom.xml           package
# Use local copy for these JARs, cannot find them in any repo.
cp ../3rdParty/lib/wsif.jar               install-wsif/              && \
cp ../3rdParty/lib/defaulttheme-gray.jar  install-jsf-webui-theme/   && \
mvn -f install-wsif/pom.xml               package                    && \
mvn -f install-jsf-webui-theme/pom.xml    package

# Now we can copy over the src and build.
# For xml and xsd files, copy them to src/main/resources using tar.
cd yawl-lib                      && \
    rm -rf src/                  && \
    mkdir -p src/main/java       && \
    mkdir -p src/main/resources  && \
    cp -r ../../../src/org ./src/main/java                    && \
    rm -r src/main/java/org/yawlfoundation/yawl/controlpanel/ && \
    tar -zcvf additional.tgz `find src/ -name '*.x??'`        && \
    tar -zxvf additional.tgz --directory src/main/resources --strip-components=3 && \
    mkdir -p ./src/main/resources/org/yawlfoundation/yawl/unmarshal/             && \
    cp ../../../schema/* ./src/main/resources/org/yawlfoundation/yawl/unmarshal/ && \
    mvn clean package

echo '..................................................................'
echo 'The output JAR file is in `yawl/build/jar/yawl-lib/target` folder.'
echo '..................................................................'
