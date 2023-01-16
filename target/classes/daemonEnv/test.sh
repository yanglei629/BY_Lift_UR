#!/bin/sh
v1=$(readlink -e $0)
echo $v1
v2=$(dirname $(readlink -f $0))
echo $v2
