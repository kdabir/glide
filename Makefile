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
