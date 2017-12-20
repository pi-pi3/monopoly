
SRC=$(wildcard ./src/main/java/**/*.java)

default: all

run: client

client: all
	java -jar monopoly.jar

server: all
	java -jar monopoly.jar -s

all:
	@echo " * Building shadowJar * "
	@$(MAKE) shadowJar
	@cp build/libs/monopoly-all.jar ./monopoly.jar
	@echo " * Done * "

shadowJar: $(SRC) ./gradlew
	@./gradlew shadowJar

clean: ./gradlew
	@./gradlew clean
	@rm -rf monopoly.jar
