#!/bin/bash

echo '!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'
echo 'MUST run this shell script from `yawl/build/jar` folder.'
echo '!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'

# Cannot find the following JARs in maven repo, so have to install them locally first.
cp ../3rdParty/lib/appbase.jar install-jsf-appbase/ && \
cp ../3rdParty/lib/webui.jar   install-jsf-webui/   && \
cp ../3rdParty/lib/wsif.jar    install-wsif/        && \
mvn -f install-jsf-appbase/pom.xml package          && \
mvn -f install-jsf-webui/pom.xml   package          && \
mvn -f install-wsif/pom.xml        package

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
