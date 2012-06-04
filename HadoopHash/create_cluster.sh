juju bootstrap
juju deploy --constraints "instance-type=c1.medium" --repository=~/charms local:hadoop hadoop-master
juju deploy --constraints "instance-type=c1.xlarge" --repository=~/charms local:hadoop  hadoop-slavecluster
juju add-unit -n $1 hadoop-slavecluster
juju add-relation hadoop-master:namenode hadoop-slavecluster:datanode
juju add-relation hadoop-master:jobtracker hadoop-slavecluster:tasktracker
