clean:
	./gradlew clean

test:
	./gradlew clean
	./gradlew --stop
	./gradlew test intTest

p:
	./gradlew --configure-on-demand glide-gradle-plugin:jWL && ./gradlew --stop

s:
	./gradlew --configure-on-demand sandbox:tasks
