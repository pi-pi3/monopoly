
SRC=$(shell echo ./src/main/java/**/*.java) # for some reason wildcard isn't working

default: monopoly.jar

client: monopoly.jar
	java -jar monopoly.jar

server: monopoly.jar
	java -jar monopoly.jar -s

monopoly.jar: build/libs/monopoly-all.jar
	@cp build/libs/monopoly-all.jar ./monopoly.jar

build/libs/monopoly-all.jar: $(SRC) ./gradlew
	@./gradlew shadowJar

clean: ./gradlew
	@./gradlew clean
	@rm -f monopoly.jar
