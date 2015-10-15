#!/bin/bash
# This will make the build work from the command line even if you do not have the proper
# credentials. No promises on the app actually loading anything though!
chmod 777 gradlew
printf "MEH_API_KEY = \"\"\nYOUTUBE_API_KEY = \"\"\nMEH_FABRIC_KEY = \"\"\n" > gradle.properties
