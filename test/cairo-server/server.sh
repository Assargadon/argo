#!/bin/bash
while true; do
  socket -p "bash -c 'while true; do for name in \$(ls *.cairo); do cat \$name; sleep .5; done; done'" 192.168.1.7 49778
  sleep 1s
done

