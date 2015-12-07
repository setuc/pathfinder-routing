#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
julia -e "include(\"$(echo $DIR)/PathfinderRouting.jl\"); PathfinderRouting.startserver()"
