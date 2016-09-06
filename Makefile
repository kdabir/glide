clean:
	./gradlew clean
	./gradlew --stop


test:
	./gradlew clean
	./gradlew --stop
	./gradlew test intTest -Dorg.gradle.parallel.intra=true --parallel

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

################################################################################
# Task to make changes to plugin and run sandbox app
################################################################################
run:
	./gradlew --configure-on-demand glide-gradle-plugin:jWL
	./gradlew --stop
	./gradlew --configure-on-demand -Dorg.gradle.parallel.intra=true sandbox:appRun


################################################################################
# Running With CLI
################################################################################
runI:
	glide-snapshot/bin/glide --app sandbox foo


runL:
	./gradlew --configure-on-demand glide-runner:run -Pcli="-a ../sandbox"


################################################################################
# Local Verification Tasks
################################################################################
check:
	curl localhost:8080
	curl localhost:8080/g
	curl localhost:8080/j


sanity: run check
	echo "done"


################################################################################
# Setup for integration tests.
# - Only needs to be run first time.
# - Creates symlink for download heavy files/directories
# - Tested only on Mac OSX
# - The path of dir is ~/.gradle-testkit
################################################################################
intSetup:
	mkdir -p ~/.gradle-testkit/caches/modules-2
	ln -s ~/.gradle/appengine-sdk/ ~/.gradle-testkit/
	ln -s ~/.gradle/caches/modules-2/files-2.1/ ~/.gradle-testkit/caches/modules-2/

intCleanup:
	rm -rf ~/.gradle-testkit

################################################################################
# Integration Tests
################################################################################
intTestWithDaemon:
	./gradlew --configure-on-demand -Dorg.gradle.parallel.intra=true --parallel intTest --daemon

intTestWithoutDaemon:
	./gradlew --configure-on-demand -Dorg.gradle.parallel.intra=true --parallel intTest --no-daemon


################################################################################
# Some important Gradle Flags
################################################################################
#
# -Dorg.gradle.parallel.intra=true					parallel tasks in same project
# --configure-on-demand								only configure relevant project in multi-project build
# --continuous / -t									watch @input files and keep running tasks
# --parallel										parallelize tasks in multi-project builds
# --stop											stop all the daemons
# --no-daemon										runs tasks in foreground
# --daemon											runs tasks in background hot/loaded jvm
#
################################################################################
