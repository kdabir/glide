clean:
	./gradlew clean

test:
	./gradlew clean
	./gradlew --stop
	./gradlew test intTest

p:
	./gradlew --configure-on-demand glide-gradle-plugin:jWL && ./gradlew --stop

si:
	./gradlew --configure-on-demand sandbox:glideInfo

ss:
	./gradlew --configure-on-demand sandbox:glideSync

sc:
	./gradlew --configure-on-demand sandbox:glideGenerateConf

sr:
	./gradlew --configure-on-demand sandbox:gRunD


run:
	./gradlew --configure-on-demand glide-gradle-plugin:jWL
	./gradlew --stop
	./gradlew --configure-on-demand sandbox:appRun

check:
	curl localhost:8080

sanity: run check
	echo "done"


################################################################
# Setup for integration tests.
# - Only needs to be run first time.
# - Creates symlink for download heavy files/directories
# - Tested only on Mac OSX
################################################################
intSetup:
	mkdir -p ~/.gradle-testkit/caches/modules-2
	ln -s ~/.gradle/appengine-sdk/ ~/.gradle-testkit/
	ln -s ~/.gradle/caches/modules-2/files-2.1/ ~/.gradle-testkit/caches/modules-2/

intCleanup:
	rm -rf ~/.gradle-testkit
