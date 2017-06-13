#!/bin/sh

# gets shell script directory
currdir=`dirname $0`

cd $currdir

if [ ! -r "jBrickBuilder.jar" ]; then
  echo "Can't find program directory, check your istallation."
  exit 1
fi

java -jar jBrickBuilder.jar


