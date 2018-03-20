#!/bin/bash

echo START_TIME: `date`

# declare APP_PATH
export APP_PATH="../app-internal-automation.apk"

# start emulator and appium in background with
##################################################
# PATHS
##################################################
WORKSPACE="./"
ANDROID_TOOLS="../Library/Android/sdk/tools"
cd ${ANDROID_TOOLS}
devices=( $(./emulator -list-avds) )


# uinstall
adb shell pm uninstall com.company.package.name
#clear app cache from simulator - do not have to re-install mvn test
#will automatically re-install once it runs the tests
adb shell pm clear com.company.package.name

##################################################
# SCRIPT VARIABLES
##################################################
emulator_pid=()
appium_pid=()
counter=0
APPIUMSTARTPORT=4900

# store all running mvn pid in run_tests here
# running mvn pid stored here
running=()
RUNNING_LIMIT=1
# save all exit code
exit_codes=()

##################################################
# FUNCTIONS
##################################################
kill_all_pid () {
 threads=$((${#emulator_pid[@]} - 1))
 echo "[END]"

 adb kill-server
 for ((j = 0; j < ${#emulator_pid[@]}; ++j))
 do
  kill -9 ${emulator_pid[j]} ${appium_pid[j]} ${running[j]}
 done
}

run_tests (){
  port=$1
  device=$2

  BASE_PACKAGE="**/test"
  # add to list once packages are completed
  LOGIN="$BASE_PACKAGE/login/*.java"
  deviceprofile=$(echo $device | tr /A-Z/  /a-z/)

  # post-processing
  # emulator_pid+=($EMU_PID)

  cd ${WORKSPACE}
  sleep 5s
  mvn -e clean test -Dtest=${LOGIN} -DappiumPort=$port -D=deviceProfile="$deviceprofile.json"
}

##################################################
# MAIN
##################################################
# kill all potentially open appium ports
appium_ports_to_close=( $(ps -A | grep appium | cut -c 1-5) )
for j in "${appium_ports_to_close[@]}"
do
  kill -9 "$j"
done

# scheduler
while (( ${#devices[@]}  > 0)) || (( ${#running[@]}  > 0))  # devices not empty
do
  # echo "MAIN: NUM DEVICES:${#devices[@]} NUM RUNNING:${#running[@]}"

  if (( ${#running[@]} < $RUNNING_LIMIT ))  && (( ${#devices[@]}  > 0)); then # start a process and add it to running

    appium_port=$(($APPIUMSTARTPORT + $counter))
    echo "HIT TEST:  ${devices[0]} ${running[@]}"

    cd ${ANDROID_TOOLS}
    ./emulator -avd "${devices[0]}"&
    EMU_PID=$!

    appium -p $appium_port&
    APP_PID=$!

    run_tests $appium_port ${devices[0]}&
    MVN_PID=$!

    # add current test to running
    emulator_pid+=($EMU_PID)
    appium_pid+=($APP_PID)
    running+=($MVN_PID)

    #remove from queue
    unset devices[0]
    devices=( "${devices[@]}" )

    # up counter for port
    counter=$(($counter + 1))
  else # running is full check which ones can be take off
    # echo "HIT AT RUNNING LIMIT"

    i=0
    while (($i < ${#running[@]})) # while not at end of running processes
    do
      # echo "CHECK RUNNING: " ${running[$i]}
      # [[ $(ps -A | grep ${running[$i]}) ]]
      if [[ $(ps -p ${running[$i]} | tail -n +2) ]]; then # if found running in ps -p skip; let it run
        # echo "RUNNING: " ${running[$i]}
        i=$(($i + 1))
      else #remove it if not running anymore

        wait ${running[$i]}
        echo "DONE RUNNING: " ${running[$i]}
        exit_codes+=($?) # grab exit code

        echo "Killing emulator: " ${emulator_pid[$i]}
        kill -9 ${emulator_pid[$i]}
        kill -9 ${appium_pid[$i]}

        unset running[i]
        running=( "${running[@]}" )
        unset emulator_pid[$i]
        emulator_pid=( "${emulator_pid[@]}" )
        unset appium_pid[$i]
        appium_pid=( "${appium_pid[@]}" )
      fi
    done
  fi

done

# final check exit code and fail job -1 if non 0 exit code
for code in "${exit_codes[@]}"
do
  if (( $code < 0)); then
    kill_all_pid
    exit -1
  fi
done
kill_all_pid
exit 0
