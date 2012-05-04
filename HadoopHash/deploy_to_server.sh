remote=$1

scp classes-*.jar remote:~
scp hashcat-*.jar remote:~
ssh remote hadoop fs -copyFromLocal ~/hashcat*.zip /app/hashcat.zip
