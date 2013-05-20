# Where are all the java source files?
SRC_ROOT=src/java

# Enumerate the files.
ALL_FILES=$(shell find $(SRC_ROOT) -name \*.java) Makefile

# Identify the main entry-point.
MAIN=org.ne.utrino.main.Main

# And the source file that holds it.
MAIN_SRC=$(shell echo $(MAIN) | tr . /).java

# Where does the output go?
BIN=bin

# Generated manifest file.
MANIFEST=$(BIN)/manifest.mf

# Output jar file.
JAR=$(BIN)/neutrino.jar


main:		$(JAR)


# Build the jar file
$(JAR):		classes $(MANIFEST)
		@echo Jarring $@
		@jar cfm $@ $(MANIFEST) -C $(BIN) org/


# Generate the manifest
$(MANIFEST):	
		@echo Generating manifest
		@echo Main-Class: $(MAIN) > $(MANIFEST)


# Compile *all* the classes.
classes:	$(ALL_FILES)
		@echo Creating output directory
		@mkdir -p $(BIN)
		@echo Compiling java files
		@javac -cp $(SRC_ROOT) $(SRC_ROOT)/$(MAIN_SRC) -d $(BIN)


# Cleanup.
clean:
		@echo Cleaning *all* the things
		@rm -rf $(BIN)


.PHONY:		clean
