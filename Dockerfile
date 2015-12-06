FROM ubuntu:14.04
MAINTAINER Adam Michael <adam@ajmichael.net>

# Julia
RUN apt-get update && apt-get install -y wget python-software-properties software-properties-common libglfw2 libglfw-dev
RUN add-apt-repository ppa:staticfloat/juliareleases && apt-get update
RUN apt-get install -y julia
RUN apt-get install -y build-essential cmake xorg-dev libglu1-mesa-dev git
RUN apt-get install -y libgmp-dev
RUN julia -e "Pkg.resolve()"

# Dependencies
RUN julia -e 'Pkg.add("GLPKMathProgInterface")'
RUN julia -e 'Pkg.add("JuMP")'
RUN julia -e 'Pkg.add("HttpServer")'

# Server
COPY src/server.jl /server.jl
COPY src/routeoptimizer.jl /routeoptimzer.jl
CMD julia server.jl
