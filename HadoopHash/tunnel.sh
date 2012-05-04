echo "Creating SOCKS tunnel to master on port 9999."
juju ssh hadoop-master/0 -D 9999
