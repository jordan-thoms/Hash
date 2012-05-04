#!/bin/bash
hash=$1
pattern=$2
arch=`uname -m`
ID=$RANDOM
MYDIR="$(dirname "$(readlink -f "$0")")"
cd $MYDIR
mpargs="-1 ?l?d?u $2 -o hashes$ID"
hcargs="-n 1 hash$ID.hash hashes$ID -o results$ID"
touch results$ID
echo $hash > hash$ID.hash
mkfifo hashes$ID # This is a pipe for the password candidates
if [ $arch = "x86_64" ]; then
    ./mp64.bin $mpargs &
    (./hashcat-cli64.bin $hcargs) > hashoutput$ID   
else
    ./mp32.bin $mpargs &
    (./hashcat-cli32.bin $hcargs) > hashoutput$ID
fi
rm hashes$ID
checked=`cat hashoutput$ID | grep 'Progress..' | cut -d ' ' -f 2 | cut -d '/' -f 2 | tr '\n' '+' | sed -e "s/+$/\n/g" |bc`
rm hashoutput$ID
echo "Checked:$checked"
password=`cat results$ID | cut -d ':' -f 2`
password_len="${#password}"
if [ $password_len -gt 0 ]; then
	echo "Password:$password"
fi
rm results$ID
rm hash$ID.hash
