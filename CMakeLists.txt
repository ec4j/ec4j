# This file is used for testing only

# To perform the test, run `cmake .` at the root of the project tree followed
# by ctest .

cmake_minimum_required(VERSION 2.6)

# Do not check any compiler
project(org.eclipse.ec4j NONE)

find_package(Java 1.6)

if(NOT JAVA_FOUND)
    message(FATAL_ERROR
        "Java not found. If you have Java installed, please run:
        cmake -DJAVA_HOME=/path/to/java .")
endif()

enable_testing()
set(EDITORCONFIG_CMD ${Java_JAVA_EXECUTABLE} -cp ${PROJECT_SOURCE_DIR}/target/org.eclipse.ec4j-0.1.0-SNAPSHOT.jar org.eclipse.ec4j.cli.Cli)
add_subdirectory(editorconfig-core-test)