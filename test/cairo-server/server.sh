#!/bin/bash
socket -slfp "bash -c 'while true; do for name in \$(ls *.cairo); do cat \$name && echo END; read ack; sleep .5; done; done'" 7778

