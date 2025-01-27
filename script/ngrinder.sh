#!/bin/sh
# https://github.com/naver/ngrinder
NGRINDER_HOME="$HOME/.ngrinder"
CONTROLLER_PATH="${NGRINDER_HOME}/ngrinder-controller-3.5.9-p1.war"
AGENT_PATH="${NGRINDER_HOME}/ngrinder-agent/run_agent.sh"
mkdir -p ${NGRINDER_HOME}/lib
JAVA_HOME_ONCE=$(/usr/libexec/java_home -v 11)

JAVA_HOME="$JAVA_HOME_ONCE" java -Djava.io.tmpdir=${NGRINDER_HOME}/lib -jar "$CONTROLLER_PATH" --port 8099 &
PID1=$!
if [ -f "$AGENT_PATH" ]; then
  JAVA_HOME="$JAVA_HOME_ONCE" "$AGENT_PATH" &
  PID2=$!
else
  PID2=$PID1
fi

cleanup() {
    NGRINDER_PIDS=$(ps -ef | grep ngrinder | grep -v grep | grep -v ngrinder.sh | awk '{print $2}')
    if [ -n "$NGRINDER_PIDS" ]; then
    for PID in $NGRINDER_PIDS; do
        kill $PID
        echo "ngrinder process with PID $PID has been terminated."
    done
    fi

    kill $PID1
    kill $PID2
    echo "ngrinder processes have been terminated."
}

trap cleanup SIGINT SIGTERM

echo "Press Enter or Ctrl+C to stop ngrinder processes..."
read

cleanup
