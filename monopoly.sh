#!/bin/sh
dir=$(dirname $0)
jar=$dir/monopoly.jar

# start server
# only client output is written to stdout/stderr,
# server output is logged to files
java -jar $jar --server $@ >monopoly.out 2>monopoly.err &
spid=$!

# start client
java -jar $jar $@

# kill server
kill $spid 2>/dev/null

# delete log files if they were empty
[ ! -s monopoly.out ] && rm monopoly.out
[ ! -s monopoly.err ] && rm monopoly.err

return 0
