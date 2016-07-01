clean:
	./gradlew clean

test:
	./gradlew clean
	./gradlew --stop
	./gradlew test intTest

plugin:
	./gradlew --configure-on-demand glide-gradle-plugin:jWL && g --stop
